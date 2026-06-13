package com.lumiere.app.service;

import com.lumiere.app.domain.OrderItem;
import com.lumiere.app.domain.Orders;
import com.lumiere.app.domain.ProductVariant;
import com.lumiere.app.domain.enumeration.OrderStatus;
import com.lumiere.app.repository.OrderItemRepository;
import com.lumiere.app.repository.OrdersRepository;
import com.lumiere.app.repository.ProductVariantRepository;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service để restore stock khi đơn hàng bị hủy.
 */
@Service
@Transactional
public class OrderStockRestoreService {

    private static final Logger LOG = LoggerFactory.getLogger(OrderStockRestoreService.class);

    private final OrdersRepository ordersRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductVariantRepository productVariantRepository;

    public OrderStockRestoreService(
        OrdersRepository ordersRepository,
        OrderItemRepository orderItemRepository,
        ProductVariantRepository productVariantRepository
    ) {
        this.ordersRepository = ordersRepository;
        this.orderItemRepository = orderItemRepository;
        this.productVariantRepository = productVariantRepository;
    }

    /**
     * Restore stock cho một đơn hàng bị hủy.
     * Method này được gọi async khi đơn hàng bị hủy.
     *
     * @param orderId ID của đơn hàng bị hủy
     */
    @Async
    public void restoreStockForCancelledOrder(Long orderId) {
        LOG.info("Starting to restore stock for cancelled order: {}", orderId);
        
        try {
            Orders order = ordersRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));

            // Chỉ restore stock cho đơn hàng đã bị hủy
            if (order.getStatus() != OrderStatus.CANCELLED) {
                LOG.warn("Order {} is not cancelled, skipping stock restoration", orderId);
                return;
            }

            // Lấy tất cả order items của đơn hàng
            List<OrderItem> orderItems = orderItemRepository.findAllByOrderIdWithVariants(orderId);
            
            if (orderItems.isEmpty()) {
                LOG.warn("No order items found for order: {}", orderId);
                return;
            }

            int restoredCount = 0;
            for (OrderItem orderItem : orderItems) {
                if (orderItem.getProductVariant() == null) {
                    LOG.warn("OrderItem {} has no product variant, skipping", orderItem.getId());
                    continue;
                }

                ProductVariant variant = orderItem.getProductVariant();
                Integer quantity = orderItem.getQuantity();
                
                if (quantity == null || quantity <= 0) {
                    LOG.warn("OrderItem {} has invalid quantity: {}, skipping", orderItem.getId(), quantity);
                    continue;
                }

                // Restore stock vào ProductVariant
                restoreProductVariantStock(variant, quantity.longValue());
                
                restoredCount++;
                LOG.debug("Restored {} units of product variant {} (SKU: {}) for order {}", 
                    quantity, variant.getId(), variant.getSku(), orderId);
            }

            LOG.info("Successfully restored stock for order {}. Restored {} items", orderId, restoredCount);
            
        } catch (Exception e) {
            LOG.error("Error restoring stock for cancelled order {}: {}", orderId, e.getMessage(), e);
            throw new RuntimeException("Failed to restore stock for order: " + orderId, e);
        }
    }

    /**
     * Restore stock vào ProductVariant.
     *
     * @param variant ProductVariant cần restore stock
     * @param quantity Số lượng cần restore
     */
    private void restoreProductVariantStock(ProductVariant variant, Long quantity) {
        Long currentStock = variant.getStockQuantity() != null ? variant.getStockQuantity() : 0L;
        variant.setStockQuantity(currentStock + quantity);
        productVariantRepository.save(variant);
        LOG.debug("Restored {} units to ProductVariant {}. New stock: {}", 
            quantity, variant.getId(), variant.getStockQuantity());
    }

    /**
     * Scheduled job chạy mỗi giờ để xử lý các đơn hàng bị hủy chưa được restore stock.
     * Job này là backup để đảm bảo không có đơn hàng nào bị bỏ sót.
     */
    @Scheduled(cron = "0 0 * * * ?") // Chạy mỗi giờ
    public void processCancelledOrdersStockRestoration() {
        LOG.info("Starting scheduled job to restore stock for cancelled orders");
        
        try {
            // Lấy tất cả đơn hàng bị hủy trong 24 giờ qua
            List<Orders> cancelledOrders = ordersRepository.findAll().stream()
                .filter(order -> order.getStatus() == OrderStatus.CANCELLED)
                .filter(order -> {
                    // Chỉ xử lý đơn hàng bị hủy trong 24 giờ qua
                    java.time.Instant oneDayAgo = java.time.Instant.now().minusSeconds(24 * 60 * 60);
                    return order.getPlacedAt() != null && order.getPlacedAt().isAfter(oneDayAgo);
                })
                .toList();

            if (cancelledOrders.isEmpty()) {
                LOG.info("No cancelled orders found to process");
                return;
            }

            LOG.info("Found {} cancelled orders to process", cancelledOrders.size());
            
            int processedCount = 0;
            for (Orders order : cancelledOrders) {
                try {
                    // Kiểm tra xem đơn hàng đã có order items chưa
                    List<OrderItem> orderItems = orderItemRepository.findAllByOrderId(order.getId());
                    if (orderItems.isEmpty()) {
                        continue;
                    }

                    // Restore stock cho đơn hàng này
                    restoreStockForCancelledOrderSync(order.getId());
                    processedCount++;
                    
                } catch (Exception e) {
                    LOG.error("Error processing cancelled order {}: {}", order.getId(), e.getMessage(), e);
                }
            }

            LOG.info("Completed scheduled job. Processed {} cancelled orders", processedCount);
            
        } catch (Exception e) {
            LOG.error("Error in scheduled job to restore stock for cancelled orders: {}", e.getMessage(), e);
        }
    }

    /**
     * Restore stock synchronously (dùng trong scheduled job).
     *
     * @param orderId ID của đơn hàng
     */
    private void restoreStockForCancelledOrderSync(Long orderId) {
        Orders order = ordersRepository.findById(orderId)
            .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));

        if (order.getStatus() != OrderStatus.CANCELLED) {
            return;
        }

        List<OrderItem> orderItems = orderItemRepository.findAllByOrderIdWithVariants(orderId);
        
        if (orderItems.isEmpty()) {
            return;
        }

        for (OrderItem orderItem : orderItems) {
            if (orderItem.getProductVariant() == null) {
                continue;
            }

            ProductVariant variant = orderItem.getProductVariant();
            Integer quantity = orderItem.getQuantity();
            
            if (quantity == null || quantity <= 0) {
                continue;
            }

            restoreProductVariantStock(variant, quantity.longValue());
        }
    }
}

