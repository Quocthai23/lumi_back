package com.lumiere.app.service.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lumiere.app.domain.Inventory;
import com.lumiere.app.domain.Orders;
import com.lumiere.app.domain.OrderStatusHistory;
import com.lumiere.app.domain.ProductVariant;
import com.lumiere.app.domain.enumeration.OrderStatus;
import com.lumiere.app.repository.InventoryRepository;
import com.lumiere.app.repository.OrderStatusHistoryRepository;
import com.lumiere.app.repository.OrdersRepository;
import com.lumiere.app.repository.ProductVariantRepository;
import com.lumiere.app.service.dto.OrderStockProcessingMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

/**
 * Kafka Consumer để xử lý giảm stock quantity khi tạo đơn hàng.
 */
@Service
public class OrderStockConsumer {

    private static final Logger LOG = LoggerFactory.getLogger(OrderStockConsumer.class);

    private final InventoryRepository inventoryRepository;
    private final ProductVariantRepository productVariantRepository;
    private final OrdersRepository ordersRepository;
    private final OrderStatusHistoryRepository orderStatusHistoryRepository;
    private final ObjectMapper objectMapper;

    public OrderStockConsumer(
        InventoryRepository inventoryRepository,
        ProductVariantRepository productVariantRepository,
        OrdersRepository ordersRepository,
        OrderStatusHistoryRepository orderStatusHistoryRepository,
        ObjectMapper objectMapper
    ) {
        this.inventoryRepository = inventoryRepository;
        this.productVariantRepository = productVariantRepository;
        this.ordersRepository = ordersRepository;
        this.orderStatusHistoryRepository = orderStatusHistoryRepository;
        this.objectMapper = objectMapper;
    }

    /**
     * Lắng nghe topic "order-stock-processing" để xử lý giảm stock quantity.
     *
     * @param message message từ Kafka (JSON string)
     * @param acknowledgment acknowledgment để commit offset
     */
    @KafkaListener(topics = "order-stock-processing", groupId = "${spring.kafka.consumer.group-id}")
    @Transactional
    public void handleStockProcessing(String message, Acknowledgment acknowledgment) {
        Long orderId = null;
        try {
            LOG.debug("Received Kafka message for order-stock-processing: {}", message);

            // Parse message
            OrderStockProcessingMessage stockMessage = objectMapper.readValue(message, OrderStockProcessingMessage.class);
            orderId = stockMessage.getOrderId();
            LOG.info("Processing stock deduction for order: {}", orderId);

            // Xử lý từng item
            for (OrderStockProcessingMessage.StockDeductionItem item : stockMessage.getItems()) {
                processStockDeduction(item);
            }

            // Commit offset sau khi xử lý thành công
            acknowledgment.acknowledge();
            LOG.info("Successfully processed stock deduction for order: {}", orderId);

        } catch (IllegalArgumentException e) {
            // Khi không đủ stock, cập nhật order về CANCELLED
            if (orderId != null) {
                try {
                    cancelOrderDueToInsufficientStock(orderId, e.getMessage());
                    LOG.warn("Order {} cancelled due to insufficient stock: {}", orderId, e.getMessage());
                } catch (Exception cancelException) {
                    LOG.error("Failed to cancel order {}: {}", orderId, cancelException.getMessage());
                }
            }
            // Commit offset để không retry lại message này
            acknowledgment.acknowledge();
        } catch (Exception e) {
            LOG.error("Error processing stock deduction message: {}", message, e);
            // Không commit offset để Kafka retry message này
            // Hoặc có thể gửi vào dead letter queue nếu muốn
        }
    }

    /**
     * Xử lý giảm stock cho một item.
     *
     * @param item thông tin item cần trừ stock
     */
    private void processStockDeduction(OrderStockProcessingMessage.StockDeductionItem item) {
        Long variantId = item.getVariantId();
        Long quantity = item.getQuantity();

        LOG.debug("Processing stock deduction: variantId={}, quantity={}", variantId, quantity);

        // Lấy product variant
        ProductVariant variant = productVariantRepository.findById(variantId)
            .orElseThrow(() -> new IllegalArgumentException("Product variant not found: " + variantId));

        // Lấy inventory với pessimistic lock để đảm bảo atomic
        java.util.List<Inventory> inventories = inventoryRepository.findByProductVariantIdForUpdate(variantId);

        if (inventories.isEmpty()) {
            throw new IllegalArgumentException(
                "Sản phẩm " + variant.getSku() + " không có trong kho. Vui lòng kiểm tra lại."
            );
        }

        // Tính tổng số lượng có sẵn
        long totalAvailable = inventories.stream()
            .mapToLong(Inventory::getStockQuantity)
            .sum();

        if (totalAvailable < quantity) {
            throw new IllegalArgumentException(
                "Sản phẩm " + variant.getSku() + " không đủ số lượng. " +
                "Có sẵn: " + totalAvailable + ", yêu cầu: " + quantity
            );
        }

        // Trừ stock từ các warehouse theo thứ tự ưu tiên (từ nhiều đến ít)
        long remainingQuantity = quantity;
        for (Inventory inventory : inventories) {
            if (remainingQuantity <= 0) {
                break;
            }

            long deductAmount = Math.min(remainingQuantity, inventory.getStockQuantity());
            if (deductAmount > 0) {
                // Sử dụng atomic update
                int updated = inventoryRepository.deductStockQuantity(inventory.getId(), deductAmount);
                if (updated == 0) {
                    // Nếu không cập nhật được (có thể do race condition), thử lại với inventory mới
                    Inventory refreshedInventory = inventoryRepository.findById(inventory.getId())
                        .orElseThrow(() -> new IllegalArgumentException("Inventory not found: " + inventory.getId()));

                    if (refreshedInventory.getStockQuantity() < deductAmount) {
                        throw new IllegalArgumentException(
                            "Sản phẩm " + variant.getSku() + " không đủ số lượng trong kho. " +
                            "Vui lòng thử lại sau."
                        );
                    }
                    // Thử lại với số lượng thực tế
                    deductAmount = Math.min(remainingQuantity, refreshedInventory.getStockQuantity());
                    updated = inventoryRepository.deductStockQuantity(inventory.getId(), deductAmount);
                    if (updated == 0) {
                        throw new IllegalArgumentException(
                            "Không thể cập nhật tồn kho cho sản phẩm " + variant.getSku() + ". Vui lòng thử lại."
                        );
                    }
                }
                remainingQuantity -= deductAmount;
                LOG.debug(
                    "Deducted {} from inventory {} for variant {}, remaining: {}",
                    deductAmount,
                    inventory.getId(),
                    variant.getSku(),
                    remainingQuantity
                );
            }
        }

        if (remainingQuantity > 0) {
            throw new IllegalArgumentException(
                "Không thể trừ đủ số lượng cho sản phẩm " + variant.getSku() + ". Vui lòng thử lại."
            );
        }

        LOG.info("Successfully deducted stock: variantId={}, quantity={}", variantId, quantity);
    }

    /**
     * Hủy đơn hàng do không đủ stock.
     *
     * @param orderId ID đơn hàng
     * @param reason lý do hủy
     */
    private void cancelOrderDueToInsufficientStock(Long orderId, String reason) {
        Orders order = ordersRepository.findById(orderId)
            .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));

        // Chỉ hủy nếu đơn hàng chưa bị hủy hoặc đã giao
        if (order.getStatus() == OrderStatus.CANCELLED || 
            order.getStatus() == OrderStatus.DELIVERED || 
            order.getStatus() == OrderStatus.COMPLETED) {
            LOG.warn("Order {} cannot be cancelled, current status: {}", orderId, order.getStatus());
            return;
        }

        order.setStatus(OrderStatus.CANCELLED);
        ordersRepository.save(order);

        // Tạo lịch sử trạng thái
        OrderStatusHistory history = new OrderStatusHistory();
        history.setOrder(order);
        history.setStatus(OrderStatus.CANCELLED);
        history.setDescription("Đơn hàng bị hủy do không đủ số lượng sản phẩm trong kho: " + reason);
        history.setTimestamp(Instant.now());
        orderStatusHistoryRepository.save(history);

        LOG.info("Order {} cancelled due to insufficient stock: {}", orderId, reason);
    }
}


