package com.lumiere.app.service.impl;

import com.lumiere.app.domain.*;
import com.lumiere.app.domain.enumeration.CustomerTier;
import com.lumiere.app.domain.enumeration.LoyaltyTransactionType;
import com.lumiere.app.domain.enumeration.OrderStatus;
import com.lumiere.app.domain.enumeration.PaymentStatus;
import com.lumiere.app.domain.enumeration.RatingType;
import com.lumiere.app.domain.enumeration.ReviewStatus;
import com.lumiere.app.repository.*;
import com.lumiere.app.security.SecurityUtils;
import com.lumiere.app.service.*;
import com.lumiere.app.service.dto.*;
import com.lumiere.app.service.mapper.OrderStatusHistoryMapper;
import com.lumiere.app.service.mapper.OrdersMapper;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import com.lumiere.app.service.mapper.ProductMapper;
import com.lumiere.app.service.mapper.ProductVariantMapper;
import com.lumiere.app.utils.RatingUtils;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.lumiere.app.domain.Orders}.
 */
@Service
@Transactional
public class OrdersServiceImpl implements OrdersService {

    private static final Logger LOG = LoggerFactory.getLogger(OrdersServiceImpl.class);

    private final OrdersRepository ordersRepository;
    private final OrdersMapper ordersMapper;
    @SuppressWarnings("unused")
    private final CustomerService customerService;
    @SuppressWarnings("unused")
    private final OrdersQueryService ordersQueryService;
    private final OrderItemService orderItemService;
    private final CartItemRepository cartItemRepository;
    private final ProductVariantRepository productVariantRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderStatusHistoryRepository orderStatusHistoryRepository;
    private final OrderStatusHistoryMapper orderStatusHistoryMapper;
    private final CustomerRepository customerRepository;
    @SuppressWarnings("unused")
    private final ProductReviewService productReviewService;
    private final ProductReviewRepository productReviewRepository;
    private final ProductRepository productRepository;
    private final LoyaltyTransactionRepository loyaltyTransactionRepository;
    private final VoucherService voucherService;
    private final ProductVariantMapper productVariantMapper;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final InventoryRepository inventoryRepository;
    private final ProductMapper productMapper;
    private final ProductService productService;

    public OrdersServiceImpl(
        OrdersRepository ordersRepository,
        OrdersMapper ordersMapper,
        CustomerService customerService,
        OrdersQueryService ordersQueryService,
        OrderItemService orderItemService,
        CartItemRepository cartItemRepository,
        ProductVariantRepository productVariantRepository,
        OrderItemRepository orderItemRepository,
        OrderStatusHistoryRepository orderStatusHistoryRepository,
        OrderStatusHistoryMapper orderStatusHistoryMapper,
        CustomerRepository customerRepository,
        ProductReviewService productReviewService,
        ProductReviewRepository productReviewRepository,
        ProductRepository productRepository,
        LoyaltyTransactionRepository loyaltyTransactionRepository,
        VoucherService voucherService,
        ProductVariantMapper productVariantMapper,
        KafkaTemplate<String, Object> kafkaTemplate,
        InventoryRepository inventoryRepository, ProductMapper productMapper, ProductService productService, ProductVariantService productVariantService) {
        this.ordersRepository = ordersRepository;
        this.ordersMapper = ordersMapper;
        this.customerService = customerService;
        this.ordersQueryService = ordersQueryService;
        this.orderItemService = orderItemService;
        this.cartItemRepository = cartItemRepository;
        this.productVariantRepository = productVariantRepository;
        this.orderItemRepository = orderItemRepository;
        this.orderStatusHistoryRepository = orderStatusHistoryRepository;
        this.orderStatusHistoryMapper = orderStatusHistoryMapper;
        this.customerRepository = customerRepository;
        this.productReviewService = productReviewService;
        this.productReviewRepository = productReviewRepository;
        this.productRepository = productRepository;
        this.loyaltyTransactionRepository = loyaltyTransactionRepository;
        this.voucherService = voucherService;
        this.productVariantMapper = productVariantMapper;
        this.kafkaTemplate = kafkaTemplate;
        this.inventoryRepository = inventoryRepository;
        this.productMapper = productMapper;
        this.productService = productService;
    }

    @Override
    public OrdersDTO save(OrdersDTO ordersDTO) {
        LOG.debug("Request to save Orders : {}", ordersDTO);
        Orders orders = ordersMapper.toEntity(ordersDTO);
        orders = ordersRepository.save(orders);
        return ordersMapper.toDto(orders);
    }

    @Override
    public OrdersDTO update(OrdersDTO ordersDTO) {
        LOG.debug("Request to update Orders : {}", ordersDTO);
        Orders orders = ordersMapper.toEntity(ordersDTO);
        orders = ordersRepository.save(orders);
        return ordersMapper.toDto(orders);
    }

    @Override
    public Optional<OrdersDTO> partialUpdate(OrdersDTO ordersDTO) {
        LOG.debug("Request to partially update Orders : {}", ordersDTO);

        return ordersRepository
            .findById(ordersDTO.getId())
            .map(existingOrders -> {
                ordersMapper.partialUpdate(existingOrders, ordersDTO);

                return existingOrders;
            })
            .map(ordersRepository::save)
            .map(ordersMapper::toDto);
    }

    public Page<OrdersDTO> findAllWithEagerRelationships(Pageable pageable) {
        return ordersRepository.findAllWithEagerRelationships(pageable).map(ordersMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<OrdersDTO> findOne(Long id) {
        LOG.debug("Request to get Orders : {}", id);
        Optional<Orders> orders = ordersRepository.findById(id);

        Optional<OrdersDTO> ordersDTO = orders.map(order -> {
            OrdersDTO dto = ordersMapper.toDto(order);
            List<OrderItemDTO> orderItems = orderItemService.findAllByOrderId(id);
            dto.setOrderItems(orderItems);
            setCanReview(dto, order);
            return dto;
        });

        return ordersDTO;
   }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Orders : {}", id);
        ordersRepository.deleteById(id);
    }

    @Override
    public void writeOrderInvoiceExcel(Long orderId, HttpServletResponse response) {
        OrdersDTO order = this.findOne(orderId)
            .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));
        List<OrderItemDTO> items = orderItemService.findAllByOrderId(orderId);

        try (Workbook wb = new XSSFWorkbook()) {
            // ====== Styles ======
            Font fTitle = wb.createFont(); fTitle.setBold(true); fTitle.setFontHeightInPoints((short)16);
            CellStyle sTitle = wb.createCellStyle(); sTitle.setFont(fTitle);

            Font fHdr = wb.createFont(); fHdr.setBold(true);
            CellStyle sHdr = wb.createCellStyle();
            sHdr.setFont(fHdr);
            sHdr.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            sHdr.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            setAllBorders(sHdr, BorderStyle.THIN);

            CellStyle sText = wb.createCellStyle(); setAllBorders(sText, BorderStyle.THIN);

            CellStyle sInt  = wb.createCellStyle(); setAllBorders(sInt, BorderStyle.THIN);
            sInt.setDataFormat(wb.createDataFormat().getFormat("#,##0"));

            CellStyle sMoney = wb.createCellStyle(); setAllBorders(sMoney, BorderStyle.THIN);
            sMoney.setDataFormat(wb.createDataFormat().getFormat("#,##0 \"₫\""));

            Font fBold = wb.createFont(); fBold.setBold(true);
            CellStyle sMoneyBold = wb.createCellStyle(); setAllBorders(sMoneyBold, BorderStyle.THIN);
            sMoneyBold.setFont(fBold);
            sMoneyBold.setDataFormat(wb.createDataFormat().getFormat("#,##0 \"₫\""));

            Sheet sheet = wb.createSheet("Invoice");
            int r = 0;

            // ===== Header: tiêu đề + thời điểm tạo =====
            Row row0 = sheet.createRow(r++);
            Cell c0 = row0.createCell(0);
            c0.setCellValue("Đơn hàng #" + safe(order.getCode()));
            c0.setCellStyle(sTitle);
            sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(0, 0, 0, 5));

            Row row1 = sheet.createRow(r++);
            String createdAt = order.getPlacedAt() != null
                ? DateTimeFormatter.ofPattern("HH:mm:ss dd/MM/yyyy")
                .format(order.getPlacedAt().atZone(ZoneId.systemDefault()))
                : "";
            row1.createCell(0).setCellValue(createdAt);

            r++; // dòng trống

            // ===== Box tiêu đề "Chi tiết đơn hàng" =====
            Row boxTitle = sheet.createRow(r++);
            Cell cBoxTitle = boxTitle.createCell(0);
            cBoxTitle.setCellValue("Chi tiết đơn hàng (" + (items != null ? items.size() : 0) + ")");
            cBoxTitle.setCellStyle(fHdrTitle(wb));
            sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(r - 1, r - 1, 0, 5));

            // ===== Header bảng items =====
            Row hdr = sheet.createRow(r++);
            String[] cols = {"Sản phẩm", "SKU", "Số lượng", "Đơn giá", "Tổng"};
            for (int i = 0; i < cols.length; i++) {
                Cell hc = hdr.createCell(i);
                hc.setCellValue(cols[i]);
                hc.setCellStyle(sHdr);
            }

            long subtotal = 0L;
            if (items != null) {
                for (OrderItemDTO it : items) {
                    Row rr = sheet.createRow(r++);

                    String pvName = it.getProductVariant() != null ? safe(it.getProductVariant().getName()) : "";
                    String pvSku  = it.getProductVariant() != null ? safe(it.getProductVariant().getSku())  : "";

                    // Tên
                    Cell a = rr.createCell(0); a.setCellValue(pvName); a.setCellStyle(sText);
                    // SKU
                    Cell b = rr.createCell(1); b.setCellValue(pvSku);  b.setCellStyle(sText);
                    // Số lượng
                    Cell c = rr.createCell(2); c.setCellValue(nz(it.getQuantity())); c.setCellStyle(sInt);
                    // Đơn giá
                    Cell d = rr.createCell(3); d.setCellValue(nz(it.getUnitPrice())); d.setCellStyle(sMoney);
                    // Thành tiền
                    long lineTotal = nz(it.getTotalPrice());
                    subtotal += lineTotal;
                    Cell e = rr.createCell(4); e.setCellValue(lineTotal); e.setCellStyle(sMoney);
                }
            }

            r++; // trống

            // ===== Cột phải: Khách hàng & Trạng thái =====
            int rightCol = 7;
            int r2 = 3;

            Row custTitle = getOrCreate(sheet, r2++);
            Cell cCustTitle = custTitle.createCell(rightCol);
            cCustTitle.setCellValue("Khách hàng");
            cCustTitle.setCellStyle(fHdrTitle(wb));
            sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(r2 - 1, r2 - 1, rightCol, rightCol + 3));

            String fullName = "";
            String phone = "";
            if (order.getCustomer() != null) {
                // Tùy CustomerDTO của bạn, thay đổi cho đúng field
                // ví dụ: getFullName(), getFirstName()/getLastName(), getPhone()...
                try {
                    // demo gộp first/last nếu có
                    String fn = safe((String) order.getCustomer().getClass().getMethod("getFirstName").invoke(order.getCustomer()));
                    String ln = safe((String) order.getCustomer().getClass().getMethod("getLastName").invoke(order.getCustomer()));
                    fullName = (fn + " " + ln).trim();
                } catch (Exception ignore) {}
                try {
                    phone = safe((String) order.getCustomer().getClass().getMethod("getPhone").invoke(order.getCustomer()));
                } catch (Exception ignore) {}
            }
            Row rName = getOrCreate(sheet, r2++); rName.createCell(rightCol).setCellValue(fullName);
            Row rPhone = getOrCreate(sheet, r2++); rPhone.createCell(rightCol).setCellValue(phone);
            Row rPayM = getOrCreate(sheet, r2++); rPayM.createCell(rightCol).setCellValue(safe(order.getPaymentMethod()));

            r2++;
            Row sttTitle = getOrCreate(sheet, r2++);
            Cell cStt = sttTitle.createCell(rightCol);
            cStt.setCellValue("Cập nhật trạng thái");
            cStt.setCellStyle(fHdrTitle(wb));
            sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(r2 - 1, r2 - 1, rightCol, rightCol + 3));

            Row sttRow1 = getOrCreate(sheet, r2++);
            sttRow1.createCell(rightCol).setCellValue("Trạng thái đơn hàng: " + safe(String.valueOf(order.getStatus())));
            Row sttRow2 = getOrCreate(sheet, r2++);
            sttRow2.createCell(rightCol).setCellValue("Trạng thái thanh toán: " + safe(String.valueOf(order.getPaymentStatus())));

            // ===== Box "Thanh toán" dưới bảng items =====
            r++;
            Row payTitle = sheet.createRow(r++);
            Cell cPayTitle = payTitle.createCell(0);
            cPayTitle.setCellValue("Thanh toán");
            cPayTitle.setCellStyle(fHdrTitle(wb));
            sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(r - 1, r - 1, 0, 5));

            // Không có các field subtotal/discount/shipping ở DTO => ghi hiển thị cơ bản
            r = writeAmountRow(sheet, r, "Tạm tính:", subtotal, sText, sMoney);
            r = writeAmountRow(sheet, r, "Phí vận chuyển:", 0L, sText, sMoney);
            r = writeAmountRow(sheet, r, "Giảm giá:", 0L, sText, sMoney);

            long grand = order.getTotalAmount() != null ? order.getTotalAmount().longValue() : subtotal;
            Row grandRow = sheet.createRow(r++);
            Cell gt = grandRow.createCell(0); gt.setCellValue("Tổng cộng:"); gt.setCellStyle(headerRight(wb));
            Cell gv = grandRow.createCell(4); gv.setCellValue(grand);       gv.setCellStyle(sMoneyBold);

            // ===== Auto-fit =====
            for (int i = 0; i <= 5; i++) sheet.autoSizeColumn(i);
            for (int i = rightCol; i <= rightCol + 3; i++) sheet.autoSizeColumn(i);

            // ===== Stream về client =====
            String filename = URLEncoder.encode("invoice_" + safe(order.getCode()) + ".xlsx", StandardCharsets.UTF_8);
            response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + filename);
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            try (ServletOutputStream os = response.getOutputStream()) {
                wb.write(os);
                os.flush();
            }
        } catch (Exception e) {
            throw new RuntimeException("Export invoice error", e);
        }
    }

    @Override
    @Transactional
    public OrdersDTO createOrderFromCart(String paymentMethod, String note, Integer redeemedPoints, String voucherCode) {
        // Lấy userId từ SecurityContext
        Long userId = SecurityUtils.getCurrentUserId()
            .orElseThrow(() -> new IllegalArgumentException("User not authenticated"));

        LOG.debug("Request to create order from cart for user: {} with voucher: {}", userId, voucherCode);

        // Lấy customer từ userId
        Customer customer = customerRepository.findByUserId(userId)
            .orElseThrow(() -> new IllegalArgumentException("Customer not found for user: " + userId));

        // Lấy tất cả items trong giỏ hàng
        List<CartItem> cartItems = cartItemRepository.findAllByCustomerId(userId);
        if (cartItems.isEmpty()) {
            throw new IllegalArgumentException("Cart is empty");
        }

        for (CartItem cartItem : cartItems) {
            ProductVariant variant = productVariantRepository.findById(cartItem.getVariantId())
                .orElseThrow(() -> new IllegalArgumentException("Product variant not found: " + cartItem.getVariantId()));

            if (variant.getStockQuantity() - cartItem.getQuantity() < 0) {
                throw new IllegalArgumentException(
                    "Không thể trừ đủ số lượng cho sản phẩm " + variant.getSku() + ". Vui lòng thử lại."
                );
            }

            variant.setStockQuantity(variant.getStockQuantity() - cartItem.getQuantity());
            variant = productVariantRepository.save(variant);
        }

        // Tạo mã đơn hàng duy nhất
        String orderCode = generateOrderCode();

        BigDecimal subtotal = BigDecimal.ZERO;
        for (CartItem cartItem : cartItems) {
            BigDecimal itemTotal = cartItem.getTotalPrice();
            subtotal = subtotal.add(itemTotal);
        }

        // Tính phí vận chuyển theo tier
        CustomerTier tier = customer.getTier() != null ? customer.getTier() : CustomerTier.BRONZE;
        BigDecimal shippingFee = BigDecimal.ZERO;

        switch (tier) {
            case GOLD:
                // GOLD: Miễn phí vận chuyển (0 VND)
                shippingFee = BigDecimal.ZERO;
                LOG.debug("GOLD tier: Free shipping applied");
                break;
            case SILVER:
                // SILVER: 20,000 VND
                shippingFee = BigDecimal.valueOf(20000);
                LOG.debug("SILVER tier: Shipping fee 20,000 VND");
                break;
            case BRONZE:
            default:
                // BRONZE: 30,000 VND
                shippingFee = BigDecimal.valueOf(30000);
                LOG.debug("BRONZE tier: Shipping fee 30,000 VND");
                break;
        }

        // Giảm giá ship 10k nếu paymentMethod là QR
        if (paymentMethod != null && paymentMethod.trim().equalsIgnoreCase("QR")) {
            BigDecimal qrDiscount = BigDecimal.valueOf(10000);
            if (shippingFee.compareTo(qrDiscount) >= 0) {
                shippingFee = shippingFee.subtract(qrDiscount);
                LOG.debug("QR payment method: Shipping fee reduced by 10,000 VND. New shipping fee: {}", shippingFee);
            } else {
                // Nếu shipping fee nhỏ hơn 10k, set về 0
                shippingFee = BigDecimal.ZERO;
                LOG.debug("QR payment method: Shipping fee reduced to 0 VND (was less than 10,000)");
            }
        }

        // Tính tổng tiền (subtotal + shipping fee)
        BigDecimal totalAmount = subtotal.add(shippingFee);

        BigDecimal discountAmount = BigDecimal.ZERO;
        Voucher voucher = null;

        // Xử lý voucher nếu có
        if (voucherCode != null && !voucherCode.trim().isEmpty()) {
            try {
                // Validate voucher
                voucher = voucherService.validateVoucher(voucherCode, totalAmount);

                // Tính số tiền giảm giá
                discountAmount = voucherService.calculateDiscountAmount(voucher, totalAmount);

                // Trừ số tiền giảm giá từ tổng tiền
                totalAmount = totalAmount.subtract(discountAmount);
                if (totalAmount.compareTo(BigDecimal.ZERO) < 0) {
                    totalAmount = BigDecimal.ZERO;
                }

                LOG.debug("Applied voucher: {}, discount amount: {}", voucherCode, discountAmount);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Lỗi voucher: " + e.getMessage());
            }
        }

        // Trừ điểm tích lũy nếu có
        if (redeemedPoints != null && redeemedPoints > 0) {
            // Kiểm tra khách hàng có đủ điểm không
            Integer currentPoints = customer.getLoyaltyPoints() != null ? customer.getLoyaltyPoints() : 0;
            if (currentPoints < redeemedPoints) {
                throw new IllegalArgumentException("Khách hàng không đủ điểm tích lũy. Hiện có: " + currentPoints + ", yêu cầu: " + redeemedPoints);
            }

            // Giả sử 1 điểm = 1000 VND
            BigDecimal discountFromPoints = BigDecimal.valueOf(redeemedPoints).multiply(BigDecimal.valueOf(1000));
            totalAmount = totalAmount.subtract(discountFromPoints);
            if (totalAmount.compareTo(BigDecimal.ZERO) < 0) {
                totalAmount = BigDecimal.ZERO;
            }

            // Trừ điểm tích lũy của khách hàng
            customer.setLoyaltyPoints(currentPoints - redeemedPoints);
            customerRepository.save(customer);

            // Tạo LoyaltyTransaction để ghi lại việc sử dụng điểm
            LoyaltyTransaction transaction = new LoyaltyTransaction();
            transaction.setCustomer(customer);
            transaction.setType(LoyaltyTransactionType.REDEEMED);
            transaction.setPoints(redeemedPoints);
            transaction.setDescription("Sử dụng điểm tích lũy cho đơn hàng #" + orderCode);
            transaction.setCreatedAt(Instant.now());
            loyaltyTransactionRepository.save(transaction);

        }

        // Tạo đơn hàng
        Orders order = new Orders();
        order.setCode(orderCode);
        order.setStatus(OrderStatus.PENDING);
        order.setPaymentStatus(PaymentStatus.UNPAID);
        order.setTotalAmount(totalAmount);
        order.setNote(note);
        order.setPaymentMethod(paymentMethod);

        if(paymentMethod.equals("COD")){
            order.setStatus(OrderStatus.CONFIRMED);
        }

        order.setPlacedAt(Instant.now());
        order.setRedeemedPoints(redeemedPoints != null ? redeemedPoints : 0);
        order.setDiscountAmount(discountAmount);
        order.setVoucher(voucher);
        order.setCustomer(customer);

        order = ordersRepository.save(order);

        // Áp dụng voucher (tăng usage count) sau khi đơn hàng được tạo thành công
        if (voucher != null) {
            voucherService.applyVoucher(voucher);
            // Đánh dấu voucher đã được sử dụng bởi khách hàng
            voucherService.markVoucherAsUsed(voucher.getId(), customer.getId());
        }

        // Tạo OrderItems từ CartItems
        for (CartItem cartItem : cartItems) {
            ProductVariant variant = productVariantRepository.findById(cartItem.getVariantId())
                .orElseThrow(() -> new IllegalArgumentException("Product variant not found: " + cartItem.getVariantId()));

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProductVariant(variant);
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setUnitPrice(cartItem.getUnitPrice());
            orderItem.setTotalPrice(cartItem.getTotalPrice());

            orderItemRepository.save(orderItem);
        }

        // Xóa giỏ hàng
        cartItemRepository.deleteAll(cartItems);

        // Tạo lịch sử trạng thái
        createOrderStatusHistory(order, OrderStatus.PENDING, "Đơn hàng được tạo từ giỏ hàng");

        OrdersDTO dto = ordersMapper.toDto(order);
        setCanReview(dto, order);
        return dto;
    }

    @Override
    @Transactional
    public OrdersDTO updateOrderStatus(Long orderId, OrderStatus newStatus, String description) {
        LOG.debug("Request to update order status: {} to {}", orderId, newStatus);

        Orders order = ordersRepository.findById(orderId)
            .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));

        OrderStatus oldStatus = order.getStatus();
        order.setStatus(newStatus);

        // Tạo lịch sử trạng thái
        createOrderStatusHistory(order, newStatus, description != null ? description : "Trạng thái thay đổi từ " + oldStatus + " sang " + newStatus);

        // Xử lý loyalty points khi đơn hàng hoàn thành
        if ((newStatus == OrderStatus.DELIVERED || newStatus == OrderStatus.COMPLETED) &&
            (oldStatus != OrderStatus.DELIVERED && oldStatus != OrderStatus.COMPLETED)) {
            processLoyaltyPointsForOrder(order);
        }

        order = ordersRepository.save(order);
        OrdersDTO dto = ordersMapper.toDto(order);
        setCanReview(dto, order);
        return dto;
    }

    @Override
    @Transactional
    public OrdersDTO cancelOrder(Long orderId, String reason) {
        LOG.debug("Request to cancel order: {}", orderId);

        Orders order = ordersRepository.findById(orderId)
            .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));

        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new IllegalArgumentException("Order is already cancelled");
        }

        if (order.getStatus() == OrderStatus.DELIVERED || order.getStatus() == OrderStatus.COMPLETED) {
            throw new IllegalArgumentException("Cannot cancel delivered or completed order");
        }

        order.setStatus(OrderStatus.CANCELLED);
        createOrderStatusHistory(order, OrderStatus.CANCELLED, reason != null ? reason : "Đơn hàng bị hủy");

        order = ordersRepository.save(order);
        OrdersDTO dto = ordersMapper.toDto(order);
        setCanReview(dto, order);
        return dto;
    }

    @Override
    @Transactional
    public OrdersDTO confirmOrder(Long orderId) {
        LOG.debug("Request to confirm order: {}", orderId);

        Orders order = ordersRepository.findById(orderId)
            .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new IllegalArgumentException("Only pending orders can be confirmed");
        }

        order.setStatus(OrderStatus.CONFIRMED);
        createOrderStatusHistory(order, OrderStatus.CONFIRMED, "Đơn hàng đã được xác nhận");

        order = ordersRepository.save(order);
        OrdersDTO dto = ordersMapper.toDto(order);
        setCanReview(dto, order);
        return dto;
    }

    @Override
    @Transactional
    public OrdersDTO confirmPayment(Long orderId) {
        LOG.debug("Request to confirm payment for order: {}", orderId);

        Orders order = ordersRepository.findById(orderId)
            .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));

        if (order.getPaymentStatus() == PaymentStatus.PAID) {
            throw new IllegalArgumentException("Đơn hàng đã được thanh toán rồi");
        }

        if (order.getPaymentStatus() == PaymentStatus.REFUNDED) {
            throw new IllegalArgumentException("Không thể xác nhận thanh toán cho đơn hàng đã được hoàn tiền");
        }

        PaymentStatus oldPaymentStatus = order.getPaymentStatus();
        order.setPaymentStatus(PaymentStatus.PAID);
        order = ordersRepository.save(order);

        LOG.info("Payment confirmed for order {}: {} -> PAID", orderId, oldPaymentStatus);

        OrdersDTO dto = ordersMapper.toDto(order);
        setCanReview(dto, order);
        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrdersDTO> getOrdersByCustomerId(Long customerId, Pageable pageable) {
        LOG.debug("Request to get orders by customer: {}", customerId);
        return ordersRepository.findByCustomerId(customerId, pageable).map(order -> {
            OrdersDTO dto = ordersMapper.toDto(order);
            setCanReview(dto, order);
            return dto;
        });
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<OrdersDTO> findByCode(String code) {
        LOG.debug("Request to get order by code: {}", code);
        return ordersRepository.findByCode(code).map(order -> {
            OrdersDTO dto = ordersMapper.toDto(order);
            // Lấy orderItems từ service và map vào DTO
            List<OrderItemDTO> orderItems = orderItemService.findAllByOrderId(order.getId());
            dto.setOrderItems(orderItems);
            setCanReview(dto, order);
            return dto;
        });
    }

    /**
     * Set canReview field dựa trên status của đơn hàng.
     */
    private void setCanReview(OrdersDTO dto, Orders order) {
        dto.setCanReview(
            order.getStatus() == OrderStatus.COMPLETED ||
            order.getStatus() == OrderStatus.DELIVERED
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderStatusHistoryDTO> getOrderStatusHistory(Long orderId) {
        LOG.debug("Request to get order status history: {}", orderId);
        List<OrderStatusHistory> histories = orderStatusHistoryRepository.findByOrderIdOrderByTimestampDesc(orderId);
        return histories.stream()
            .map(orderStatusHistoryMapper::toDto)
            .collect(Collectors.toList());
    }

    /**
     * Tạo mã đơn hàng duy nhất.
     */
    private String generateOrderCode() {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String random = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return "ORD-" + timestamp.substring(timestamp.length() - 8) + "-" + random;
    }

    /**
     * Tạo lịch sử trạng thái đơn hàng.
     */
    private void createOrderStatusHistory(Orders order, OrderStatus status, String description) {
        OrderStatusHistory history = new OrderStatusHistory();
        history.setOrder(order);
        history.setStatus(status);
        history.setDescription(description);
        history.setTimestamp(Instant.now());
        orderStatusHistoryRepository.save(history);
    }

    /* ================= helpers ================= */

    private static long nz(Integer v) { return v == null ? 0L : v.longValue(); }
    private static long nz(java.math.BigDecimal v) { return v == null ? 0L : v.longValue(); }
    private static String safe(String s){ return s == null ? "" : s; }

    private static void setAllBorders(CellStyle style, BorderStyle b) {
        style.setBorderTop(b); style.setBorderBottom(b); style.setBorderLeft(b); style.setBorderRight(b);
    }
    private static CellStyle fHdrTitle(Workbook wb){
        Font f = wb.createFont(); f.setBold(true);
        CellStyle cs = wb.createCellStyle(); cs.setFont(f);
        cs.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        cs.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return cs;
    }
    private static CellStyle headerRight(Workbook wb){
        Font f = wb.createFont(); f.setBold(true);
        CellStyle cs = wb.createCellStyle(); cs.setFont(f);
        cs.setAlignment(HorizontalAlignment.RIGHT);
        return cs;
    }
    private static Row getOrCreate(Sheet sheet, int rowIdx){
        Row r = sheet.getRow(rowIdx);
        return r != null ? r : sheet.createRow(rowIdx);
    }
    private static int writeAmountRow(Sheet sh, int r, String label, long value, CellStyle sText, CellStyle sMoney){
        Row row = sh.createRow(r++);
        Cell l = row.createCell(0); l.setCellValue(label); l.setCellStyle(sText);
        Cell v = row.createCell(4); v.setCellValue(value); v.setCellStyle(sMoney);
        return r;
    }

    @Override
    @Transactional
    public ProductReviewDTO createReviewForOrderItem(
        Long orderId,
        Long orderItemId,
        RatingType rating,
        String comment,
        String author
    ) {
        LOG.debug("Request to create review for order item: {} in order: {}", orderItemId, orderId);

        // Kiểm tra đơn hàng tồn tại
        Orders order = ordersRepository.findById(orderId)
            .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));

        // Kiểm tra đơn hàng đã được giao chưa
        if (order.getStatus() != OrderStatus.DELIVERED && order.getStatus() != OrderStatus.COMPLETED) {
            throw new IllegalArgumentException("Chỉ có thể đánh giá sản phẩm từ đơn hàng đã được giao");
        }

        // Kiểm tra order item tồn tại và thuộc về đơn hàng
        OrderItem orderItem = orderItemRepository.findById(orderItemId)
            .orElseThrow(() -> new IllegalArgumentException("Order item not found: " + orderItemId));

        if (!orderItem.getOrder().getId().equals(orderId)) {
            throw new IllegalArgumentException("Order item không thuộc về đơn hàng này");
        }

        // Lấy product từ product variant
        ProductVariant variant = orderItem.getProductVariant();
        if (variant == null || variant.getProduct() == null) {
            throw new IllegalArgumentException("Không tìm thấy sản phẩm từ order item");
        }

        Product product = variant.getProduct();

        // Kiểm tra khách hàng đã review sản phẩm này chưa
        if (hasCustomerReviewedProduct(order.getCustomer().getId(), product.getId())) {
            throw new IllegalArgumentException("Bạn đã đánh giá sản phẩm này rồi");
        }

        // Tạo review
        ProductReview review = new ProductReview();
        review.setRating(rating);
        review.setAuthor(author);
        review.setComment(comment);
        review.setStatus(ReviewStatus.PENDING);
        review.setCreatedAt(Instant.now());
        review.setProduct(product);

        review = productReviewRepository.save(review);

        // Cập nhật average rating và review count của product
        updateProductRating(product);

        ProductReviewDTO reviewDTO = new ProductReviewDTO();
        reviewDTO.setId(review.getId());
        reviewDTO.setRating(review.getRating());
        reviewDTO.setAuthor(review.getAuthor());
        reviewDTO.setComment(review.getComment());
        reviewDTO.setStatus(review.getStatus());
        reviewDTO.setCreatedAt(review.getCreatedAt());

        return reviewDTO;
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderItemDTO> getReviewableProductsFromOrder(Long orderId) {
        LOG.debug("Request to get reviewable products from order: {}", orderId);

        Orders order = ordersRepository.findById(orderId)
            .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));

        // Chỉ trả về các sản phẩm trong đơn hàng đã được giao
        if (order.getStatus() != OrderStatus.DELIVERED && order.getStatus() != OrderStatus.COMPLETED) {
            return List.of();
        }

        // Lấy tất cả order items
        List<OrderItem> orderItems = orderItemRepository.findAllByOrderId(orderId);

        // Lọc các sản phẩm chưa được review
        return orderItems.stream()
            .filter(item -> {
                if (item.getProductVariant() == null || item.getProductVariant().getProduct() == null) {
                    return false;
                }
                Long productId = item.getProductVariant().getProduct().getId();
                return !hasCustomerReviewedProduct(order.getCustomer().getId(), productId);
            })
            .map(item -> {
                OrderItemDTO dto = new OrderItemDTO();
                dto.setId(item.getId());
                dto.setQuantity(item.getQuantity());
                dto.setUnitPrice(item.getUnitPrice());
                dto.setTotalPrice(item.getTotalPrice());
                // Có thể thêm thông tin product variant nếu cần
                return dto;
            })
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasCustomerReviewedProduct(Long customerId, Long productId) {
        LOG.debug("Request to check if customer {} has reviewed product {}", customerId, productId);

        Customer customer = customerRepository.findById(customerId)
            .orElseThrow(() -> new IllegalArgumentException("Customer not found: " + customerId));

        // Kiểm tra product tồn tại
        if (!productRepository.existsById(productId)) {
            throw new IllegalArgumentException("Product not found: " + productId);
        }

        // Lấy tên khách hàng (có thể là firstName + lastName hoặc email)
        String customerName = getCustomerName(customer);

        // Kiểm tra xem có review nào của khách hàng này cho sản phẩm này không
        return productReviewRepository.findAll().stream()
            .anyMatch(review ->
                review.getProduct() != null &&
                review.getProduct().getId().equals(productId) &&
                review.getAuthor().equals(customerName)
            );
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductReviewDTO> getReviewsForOrder(Long orderId) {
        LOG.debug("Request to get reviews for order: {}", orderId);

        Orders order = ordersRepository.findById(orderId)
            .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));

        // Lấy tất cả order items
        List<OrderItem> orderItems = orderItemRepository.findAllByOrderId(orderId);

        // Lấy tất cả product IDs từ order items
        List<Long> productIds = orderItems.stream()
            .filter(item -> item.getProductVariant() != null && item.getProductVariant().getProduct() != null)
            .map(item -> item.getProductVariant().getProduct().getId())
            .distinct()
            .collect(Collectors.toList());

        if (productIds.isEmpty()) {
            return List.of();
        }

        // Lấy tất cả reviews của các sản phẩm trong đơn hàng
        String customerName = getCustomerName(order.getCustomer());

        return productReviewRepository.findAll().stream()
            .filter(review ->
                review.getProduct() != null &&
                productIds.contains(review.getProduct().getId()) &&
                review.getAuthor().equals(customerName)
            )
            .map(review -> {
                ProductReviewDTO dto = new ProductReviewDTO();
                dto.setId(review.getId());
                dto.setRating(review.getRating());
                dto.setAuthor(review.getAuthor());
                dto.setComment(review.getComment());
                dto.setStatus(review.getStatus());
                dto.setCreatedAt(review.getCreatedAt());
                return dto;
            })
            .collect(Collectors.toList());
    }

    /**
     * Lấy tên khách hàng để so sánh với author của review.
     */
    private String getCustomerName(Customer customer) {
        try {
            // Thử lấy firstName và lastName
            java.lang.reflect.Method getFirstName = customer.getClass().getMethod("getFirstName");
            java.lang.reflect.Method getLastName = customer.getClass().getMethod("getLastName");
            String firstName = (String) getFirstName.invoke(customer);
            String lastName = (String) getLastName.invoke(customer);
            if (firstName != null || lastName != null) {
                return (firstName != null ? firstName : "") + " " + (lastName != null ? lastName : "").trim();
            }
        } catch (Exception e) {
            // Ignore
        }

        try {
            // Thử lấy email
            java.lang.reflect.Method getEmail = customer.getClass().getMethod("getEmail");
            String email = (String) getEmail.invoke(customer);
            if (email != null) {
                return email;
            }
        } catch (Exception e) {
            // Ignore
        }

        // Fallback: dùng ID
        return "Customer-" + customer.getId();
    }

    /**
     * Cập nhật average rating và review count của product.
     */
    private void updateProductRating(Product product) {
        ProductDTO productDTO = productMapper.toDto(product);
        List<ProductReview> approvedReviews = productReviewRepository.findAll().stream()
            .filter(review ->
                review.getProduct() != null &&
                review.getProduct().getId().equals(product.getId())
            )
            .collect(Collectors.toList());

        if (approvedReviews.isEmpty()) {
            productDTO.setAverageRating(0.0);
            productDTO.setReviewCount(0);
        } else {
            double totalRating = approvedReviews.stream()
                .mapToDouble(review -> RatingUtils.toNumber(review.getRating()))
                .sum();

            productDTO.setAverageRating(totalRating / approvedReviews.size());
            productDTO.setReviewCount(approvedReviews.size());
        }

        productService.save(productDTO);
    }

    /**
     * Xử lý cộng điểm loyalty khi đơn hàng hoàn thành.
     * Quy tắc tính điểm theo tier:
     * - BRONZE: 1% giá trị sản phẩm (không tính phí vận chuyển)
     * - SILVER: 1.5% giá trị sản phẩm (không tính phí vận chuyển)
     * - GOLD: 2% giá trị sản phẩm (không tính phí vận chuyển)
     * (1 điểm = 1000 VND)
     */
    private void processLoyaltyPointsForOrder(Orders order) {
        if (order.getCustomer() == null) {
            LOG.warn("Order {} has no customer, skipping loyalty points", order.getId());
            return;
        }

        Customer customer = order.getCustomer();
        CustomerTier tier = customer.getTier() != null ? customer.getTier() : CustomerTier.BRONZE;

        // Tính subtotal từ order items (chỉ giá trị sản phẩm, không tính phí vận chuyển)
        BigDecimal subtotal = BigDecimal.ZERO;
        List<OrderItem> orderItems = orderItemRepository.findAllByOrderId(order.getId());
        for (OrderItem item : orderItems) {
            if (item.getTotalPrice() != null) {
                subtotal = subtotal.add(item.getTotalPrice());
            }
        }

        if (subtotal.compareTo(BigDecimal.ZERO) <= 0) {
            LOG.warn("Order {} has invalid subtotal, skipping loyalty points", order.getId());
            return;
        }

        // Xác định tỷ lệ tích điểm theo tier
        double pointsRate;
        switch (tier) {
            case BRONZE:
                pointsRate = 0.01; // 1%
                break;
            case SILVER:
                pointsRate = 0.015; // 1.5%
                break;
            case GOLD:
                pointsRate = 0.02; // 2%
                break;
            default:
                pointsRate = 0.01; // Mặc định 1% cho BRONZE
                break;
        }

        // Tính điểm: subtotal * pointsRate / 1000 (vì 1 điểm = 1000 VND)
        BigDecimal pointsDecimal = subtotal.multiply(BigDecimal.valueOf(pointsRate)).divide(BigDecimal.valueOf(1000), 0, java.math.RoundingMode.DOWN);
        int pointsEarned = pointsDecimal.intValue();

        // Tối thiểu 1 điểm nếu đơn hàng đủ lớn
        BigDecimal minOrderAmount = BigDecimal.valueOf(100000).divide(BigDecimal.valueOf(pointsRate), 2, java.math.RoundingMode.UP);
        if (pointsEarned == 0 && subtotal.compareTo(minOrderAmount) >= 0) {
            pointsEarned = 1;
        }

        if (pointsEarned > 0) {
            // Cập nhật điểm tích lũy của customer
            Integer currentPoints = customer.getLoyaltyPoints() != null ? customer.getLoyaltyPoints() : 0;
            customer.setLoyaltyPoints(currentPoints + pointsEarned);
            customerRepository.save(customer);

            // Tạo LoyaltyTransaction
            LoyaltyTransaction transaction = new LoyaltyTransaction();
            transaction.setCustomer(customer);
            transaction.setType(LoyaltyTransactionType.EARNED);
            transaction.setPoints(pointsEarned);
            transaction.setDescription(
                String.format("Tích điểm từ đơn hàng #%s - %s VND (Tier: %s, Tỷ lệ: %.1f%%)",
                    order.getCode(), subtotal, tier, pointsRate * 100)
            );
            transaction.setCreatedAt(Instant.now());
            loyaltyTransactionRepository.save(transaction);

            LOG.info("Added {} loyalty points to customer {} (tier: {}) for order {} (subtotal: {})",
                pointsEarned, customer.getId(), tier, order.getCode(), subtotal);
        }
    }

    @Override
    @Transactional
    public List<ProductReviewDTO> createReviewsForOrder(Long orderId, List<CreateOrderReviewDTO> reviews) {
        LOG.debug("Request to create reviews for order: {}", orderId);

        // Kiểm tra đơn hàng tồn tại
        Orders order = ordersRepository.findById(orderId)
            .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));

        // Kiểm tra đơn hàng đã được giao chưa
        if (order.getStatus() != OrderStatus.DELIVERED && order.getStatus() != OrderStatus.COMPLETED) {
            throw new IllegalArgumentException("Chỉ có thể đánh giá sản phẩm từ đơn hàng đã được giao");
        }

        // Kiểm tra customer tồn tại
        if (order.getCustomer() == null) {
            throw new IllegalArgumentException("Order không có customer");
        }

        Customer customer = order.getCustomer();
        String customerName = getCustomerName(customer);

        // Lấy tất cả order items của đơn hàng
        List<OrderItem> orderItems = orderItemRepository.findAllByOrderId(orderId);
        Map<Long, OrderItem> orderItemMap = orderItems.stream()
            .collect(Collectors.toMap(OrderItem::getId, item -> item));

        List<ProductReviewDTO> createdReviews = new ArrayList<>();

        // Tạo review cho từng sản phẩm
        for (CreateOrderReviewDTO reviewRequest : reviews) {
            Long orderItemId = reviewRequest.getOrderItemId();
            OrderItem orderItem = orderItemMap.get(orderItemId);

            if (orderItem == null) {
                LOG.warn("Order item {} not found in order {}", orderItemId, orderId);
                continue;
            }

            // Kiểm tra order item thuộc về đơn hàng này
            if (!orderItem.getOrder().getId().equals(orderId)) {
                LOG.warn("Order item {} does not belong to order {}", orderItemId, orderId);
                continue;
            }

            // Lấy product từ product variant
            ProductVariant variant = orderItem.getProductVariant();
            if (variant == null || variant.getProduct() == null) {
                LOG.warn("Product variant or product not found for order item {}", orderItemId);
                continue;
            }

            Product product = variant.getProduct();

            // Kiểm tra khách hàng đã review sản phẩm này chưa
            if (hasCustomerReviewedProduct(customer.getId(), product.getId())) {
                LOG.warn("Customer {} has already reviewed product {}", customer.getId(), product.getId());
                continue;
            }

            // Tạo review
            ProductReview review = new ProductReview();
            review.setRating(reviewRequest.getRating());
            review.setAuthor(customerName);
            review.setComment(reviewRequest.getComment());
            review.setStatus(ReviewStatus.PENDING);
            review.setCreatedAt(Instant.now());
            review.setProduct(product);

            review = productReviewRepository.save(review);

            // Cập nhật average rating và review count của product
            updateProductRating(product);

            // Tạo DTO
            ProductReviewDTO reviewDTO = new ProductReviewDTO();
            reviewDTO.setId(review.getId());
            reviewDTO.setRating(review.getRating());
            reviewDTO.setAuthor(review.getAuthor());
            reviewDTO.setComment(review.getComment());
            reviewDTO.setStatus(review.getStatus());
            reviewDTO.setCreatedAt(review.getCreatedAt());
            createdReviews.add(reviewDTO);

            // Bắn Kafka message để tính điểm
            try {
                ReviewRatingMessage message = new ReviewRatingMessage(
                    product.getId(),
                    review.getId(),
                    review.getRating(),
                    customer.getId(),
                    orderId
                );
                LOG.info("Sent Kafka message for review rating: {}", message);
            } catch (Exception e) {
                LOG.error("Failed to send Kafka message for review rating", e);
                // Không throw exception để không rollback transaction
            }
        }

        return createdReviews;
    }
}
