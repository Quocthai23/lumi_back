package com.lumiere.app.web.rest;

import com.lumiere.app.domain.Orders;
import com.lumiere.app.domain.enumeration.OrderStatus;
import com.lumiere.app.repository.CustomerRepository;
import com.lumiere.app.repository.OrdersRepository;
import com.lumiere.app.service.OrdersQueryService;
import com.lumiere.app.service.OrdersService;
import com.lumiere.app.service.criteria.OrdersCriteria;
import com.lumiere.app.service.dto.OrderStatusHistoryDTO;
import com.lumiere.app.service.dto.OrdersDTO;
import com.lumiere.app.web.rest.errors.BadRequestAlertException;
import com.lumiere.app.security.SecurityUtils;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.lumiere.app.domain.Orders}.
 */
@RestController
@RequestMapping("/api/orders")
public class OrdersResource {

    private static final Logger LOG = LoggerFactory.getLogger(OrdersResource.class);

    private static final String ENTITY_NAME = "orders";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final OrdersService ordersService;

    private final OrdersRepository ordersRepository;

    private final OrdersQueryService ordersQueryService;

    private final CustomerRepository customerRepository;

    public OrdersResource(
        OrdersService ordersService,
        OrdersRepository ordersRepository,
        OrdersQueryService ordersQueryService,
        CustomerRepository customerRepository
    ) {
        this.ordersService = ordersService;
        this.ordersRepository = ordersRepository;
        this.ordersQueryService = ordersQueryService;
        this.customerRepository = customerRepository;
    }

    /**
     * {@code POST  /orders} : Create a new orders.
     *
     * @param ordersDTO the ordersDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new ordersDTO, or with status {@code 400 (Bad Request)} if the orders has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<OrdersDTO> createOrders(@Valid @RequestBody OrdersDTO ordersDTO) throws URISyntaxException {
        LOG.debug("REST request to save Orders : {}", ordersDTO);
        if (ordersDTO.getId() != null) {
            throw new BadRequestAlertException("A new orders cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ordersDTO = ordersService.save(ordersDTO);
        return ResponseEntity.created(new URI("/api/orders/" + ordersDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, ordersDTO.getId().toString()))
            .body(ordersDTO);
    }

    /**
     * {@code PUT  /orders/:id} : Updates an existing orders.
     *
     * @param id the id of the ordersDTO to save.
     * @param ordersDTO the ordersDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated ordersDTO,
     * or with status {@code 400 (Bad Request)} if the ordersDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the ordersDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<OrdersDTO> updateOrders(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody OrdersDTO ordersDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Orders : {}, {}", id, ordersDTO);
        if (ordersDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, ordersDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!ordersRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        ordersDTO = ordersService.update(ordersDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, ordersDTO.getId().toString()))
            .body(ordersDTO);
    }

    /**
     * {@code PATCH  /orders/:id} : Partial updates given fields of an existing orders, field will ignore if it is null
     *
     * @param id the id of the ordersDTO to save.
     * @param ordersDTO the ordersDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated ordersDTO,
     * or with status {@code 400 (Bad Request)} if the ordersDTO is not valid,
     * or with status {@code 404 (Not Found)} if the ordersDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the ordersDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<OrdersDTO> partialUpdateOrders(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody OrdersDTO ordersDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Orders partially : {}, {}", id, ordersDTO);
        if (ordersDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, ordersDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!ordersRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<OrdersDTO> result = ordersService.partialUpdate(ordersDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, ordersDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /orders} : get all the orders.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of orders in body.
     */
    @GetMapping("")
    public ResponseEntity<List<OrdersDTO>> getAllOrders(
        OrdersCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get Orders by criteria: {}", criteria);

        Page<OrdersDTO> page = ordersQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /orders/count} : count all the orders.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countOrders(OrdersCriteria criteria) {
        LOG.debug("REST request to count Orders by criteria: {}", criteria);
        return ResponseEntity.ok().body(ordersQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /orders/:id} : get the "id" orders.
     *
     * @param id the id of the ordersDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the ordersDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<OrdersDTO> getOrders(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Orders : {}", id);
        Optional<OrdersDTO> ordersDTO = ordersService.findOne(id);
        return ResponseUtil.wrapOrNotFound(ordersDTO);
    }

    /**
     * {@code DELETE  /orders/:id} : delete the "id" orders.
     *
     * @param id the id of the ordersDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrders(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Orders : {}", id);
        ordersService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * Xuất Excel hóa đơn của đơn hàng.
     * Ví dụ: GET /api/orders/123/invoice.xlsx
     */
    @GetMapping(
        value = "/{orderId}/invoice",
        produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
    )
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')") // chỉnh theo hệ thống phân quyền của bạn
    public void exportInvoiceXlsx(
        @PathVariable Long orderId,
        HttpServletResponse response
    ) {
        ordersService.writeOrderInvoiceExcel(orderId, response);
    }

    /**
     * {@code POST  /orders/create-from-cart} : Tạo đơn hàng từ giỏ hàng.
     *
     * @param request thông tin tạo đơn hàng
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new ordersDTO.
     */
    @PostMapping("/create-from-cart")
    public ResponseEntity<OrdersDTO> createOrderFromCart(@Valid @RequestBody CreateOrderRequest request) {
        LOG.debug("REST request to create order from cart: {}", request);
        OrdersDTO ordersDTO = ordersService.createOrderFromCart(
            request.getPaymentMethod(),
            request.getNote(),
            request.getRedeemedPoints(),
            request.getVoucherCode(),
            request.getShippingFee(),
            request.getShippingInfo()
        );
        return ResponseEntity.ok().body(ordersDTO);
    }

    /**
     * {@code POST  /orders/create-guest-order} : Tạo đơn hàng cho khách vãng lai (không cần đăng nhập).
     *
     * @param request thông tin tạo đơn hàng từ guest
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new ordersDTO.
     */
    @PostMapping("/create-guest-order")
    public ResponseEntity<OrdersDTO> createGuestOrder(@Valid @RequestBody CreateGuestOrderRequest request) {
        LOG.debug("REST request to create guest order: {}", request);
        OrdersDTO ordersDTO = ordersService.createGuestOrder(
            request.getCartItems(),
            request.getPaymentMethod(),
            request.getNote(),
            request.getRedeemedPoints(),
            request.getVoucherCode(),
            request.getShippingFee(),
            request.getShippingInfo()
        );
        return ResponseEntity.ok().body(ordersDTO);
    }

    /**
     * {@code PUT  /orders/{id}/status} : Cập nhật trạng thái đơn hàng.
     *
     * @param id ID đơn hàng
     * @param request thông tin cập nhật trạng thái
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated ordersDTO.
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<OrdersDTO> updateOrderStatus(
        @PathVariable Long id,
        @Valid @RequestBody UpdateOrderStatusRequest request
    ) {
        LOG.debug("REST request to update order status: {} to {}", id, request.getStatus());
        OrdersDTO ordersDTO = ordersService.updateOrderStatus(id, request.getStatus(), request.getDescription());
        return ResponseEntity.ok().body(ordersDTO);
    }

    /**
     * {@code PUT  /orders/{id}/cancel} : Hủy đơn hàng.
     * Có thể được gọi bởi cả admin và customer (chỉ hủy đơn hàng của chính họ).
     *
     * @param id ID đơn hàng
     * @param request thông tin hủy đơn hàng
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated ordersDTO.
     */
    @PutMapping("/{id}/cancel")
    public ResponseEntity<OrdersDTO> cancelOrder(
        @PathVariable Long id,
        @RequestBody(required = false) CancelOrderRequest request
    ) {
        LOG.debug("REST request to cancel order: {}", id);
        
        // Kiểm tra quyền: nếu không phải admin, chỉ cho phép hủy đơn hàng của chính họ
        Optional<Long> currentUserId = SecurityUtils.getCurrentUserId();
        if (currentUserId.isPresent()) {
            Orders order = ordersRepository.findById(id)
                .orElseThrow(() -> new BadRequestAlertException("Order not found", ENTITY_NAME, "idnotfound"));
            
            // Kiểm tra nếu user là customer và đơn hàng không thuộc về họ
            if (order.getCustomer() != null && order.getCustomer().getUser() != null) {
                Long orderUserId = order.getCustomer().getUser().getId();
                if (!currentUserId.get().equals(orderUserId)) {
                    // Kiểm tra xem user có phải admin không
                    boolean isAdmin = SecurityUtils.hasCurrentUserAnyOfAuthorities("ROLE_ADMIN");
                    if (!isAdmin) {
                        throw new BadRequestAlertException("You can only cancel your own orders", ENTITY_NAME, "unauthorized");
                    }
                }
            }
        }
        
        String reason = request != null ? request.getReason() : null;
        OrdersDTO ordersDTO = ordersService.cancelOrder(id, reason);
        return ResponseEntity.ok().body(ordersDTO);
    }

    /**
     * {@code PUT  /orders/{id}/confirm} : Xác nhận đơn hàng.
     *
     * @param id ID đơn hàng
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated ordersDTO.
     */
    @PutMapping("/{id}/confirm")
    public ResponseEntity<OrdersDTO> confirmOrder(@PathVariable Long id) {
        LOG.debug("REST request to confirm order: {}", id);
        OrdersDTO ordersDTO = ordersService.confirmOrder(id);
        return ResponseEntity.ok().body(ordersDTO);
    }

    /**
     * {@code PUT  /orders/{id}/confirm-payment} : Xác nhận đã thanh toán cho đơn hàng.
     *
     * @param id ID đơn hàng
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated ordersDTO.
     */
    @PutMapping("/{id}/confirm-payment")
    public ResponseEntity<OrdersDTO> confirmPayment(@PathVariable Long id) {
        LOG.debug("REST request to confirm payment for order: {}", id);
        OrdersDTO ordersDTO = ordersService.confirmPayment(id);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .body(ordersDTO);
    }

    /**
     * {@code GET  /orders/my-orders} : Lấy đơn hàng của khách hàng hiện tại.
     *
     * @param pageable thông tin phân trang
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of orders in body.
     */
    @GetMapping("/my-orders")
    public ResponseEntity<List<OrdersDTO>> getMyOrders(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get my orders");
        Long userId = SecurityUtils.getCurrentUserId()
            .orElseThrow(() -> new BadRequestAlertException("User not authenticated", ENTITY_NAME, "notauthenticated"));
        
        // Lấy customerId từ userId
        Long customerId = customerRepository.findByUserId(userId)
            .map(customer -> customer.getId())
            .orElseThrow(() -> new BadRequestAlertException("Customer not found for user", ENTITY_NAME, "customernotfound"));
        
        Page<OrdersDTO> page = ordersService.getOrdersByCustomerId(customerId, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /orders/code/{code}} : Lấy đơn hàng theo mã đơn hàng.
     *
     * @param code mã đơn hàng
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the ordersDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/code/{code}")
    public ResponseEntity<OrdersDTO> getOrderByCode(@PathVariable String code) {
        LOG.debug("REST request to get Order by code: {}", code);
        Optional<OrdersDTO> ordersDTO = ordersService.findByCode(code);
        return ResponseUtil.wrapOrNotFound(ordersDTO);
    }

    /**
     * {@code GET  /orders/{id}/status-history} : Lấy lịch sử trạng thái đơn hàng.
     *
     * @param id ID đơn hàng
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of status history in body.
     */
    @GetMapping("/{id}/status-history")
    public ResponseEntity<List<OrderStatusHistoryDTO>> getOrderStatusHistory(@PathVariable Long id) {
        LOG.debug("REST request to get order status history: {}", id);
        List<OrderStatusHistoryDTO> histories = ordersService.getOrderStatusHistory(id);
        return ResponseEntity.ok().body(histories);
    }

    /**
     * {@code GET  /orders/{id}/next-status} : Lấy trạng thái đơn hàng tiếp theo.
     *
     * @param id ID đơn hàng
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the next status in body.
     */
    @GetMapping("/{id}/next-status")
    public ResponseEntity<OrderStatus> getNextOrderStatus(@PathVariable Long id) {
        LOG.debug("REST request to get next order status: {}", id);
        Optional<OrdersDTO> ordersDTO = ordersService.findOne(id);
        if (ordersDTO.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        OrderStatus nextStatus = ordersService.getNextOrderStatus(
            ordersDTO.get().getStatus(),
            ordersDTO.get().getPaymentStatus()
        );
        return ResponseEntity.ok().body(nextStatus);
    }

    /**
     * {@code POST  /orders/{id}/reviews} : Tạo review cho toàn bộ đơn hàng.
     * Tạo review cho từng sản phẩm trong order và bắn Kafka để tính điểm.
     *
     * @param id ID đơn hàng
     * @param reviews danh sách review cho từng sản phẩm
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of created reviews in body.
     */
    @PostMapping("/{id}/reviews")
    public ResponseEntity<List<com.lumiere.app.service.dto.ProductReviewDTO>> createReviewsForOrder(
        @PathVariable Long id,
        @Valid @RequestBody List<com.lumiere.app.service.dto.CreateOrderReviewDTO> reviews
    ) {
        LOG.debug("REST request to create reviews for order: {}", id);
        List<com.lumiere.app.service.dto.ProductReviewDTO> createdReviews = ordersService.createReviewsForOrder(id, reviews);
        return ResponseEntity.ok().body(createdReviews);
    }

    /**
     * Request DTO cho tạo đơn hàng từ giỏ hàng.
     */
    public static class CreateOrderRequest {
        private String paymentMethod;
        private String note;
        private Integer redeemedPoints;
        private String voucherCode;
        private java.math.BigDecimal shippingFee;
        private String shippingInfo;

        public String getPaymentMethod() {
            return paymentMethod;
        }

        public void setPaymentMethod(String paymentMethod) {
            this.paymentMethod = paymentMethod;
        }

        public String getNote() {
            return note;
        }

        public void setNote(String note) {
            this.note = note;
        }

        public Integer getRedeemedPoints() {
            return redeemedPoints;
        }

        public void setRedeemedPoints(Integer redeemedPoints) {
            this.redeemedPoints = redeemedPoints;
        }

        public String getVoucherCode() {
            return voucherCode;
        }

        public void setVoucherCode(String voucherCode) {
            this.voucherCode = voucherCode;
        }

        public java.math.BigDecimal getShippingFee() {
            return shippingFee;
        }

        public void setShippingFee(java.math.BigDecimal shippingFee) {
            this.shippingFee = shippingFee;
        }

        public String getShippingInfo() {
            return shippingInfo;
        }

        public void setShippingInfo(String shippingInfo) {
            this.shippingInfo = shippingInfo;
        }
    }

    /**
     * Request DTO cho cập nhật trạng thái đơn hàng.
     */
    public static class UpdateOrderStatusRequest {
        @NotNull
        private OrderStatus status;
        private String description;

        public OrderStatus getStatus() {
            return status;
        }

        public void setStatus(OrderStatus status) {
            this.status = status;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }

    /**
     * Request DTO cho hủy đơn hàng.
     */
    public static class CancelOrderRequest {
        private String reason;

        public String getReason() {
            return reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }
    }

    /**
     * Request DTO cho tạo đơn hàng từ khách vãng lai.
     */
    public static class CreateGuestOrderRequest {
        private java.util.List<com.lumiere.app.service.dto.GuestCartItemDTO> cartItems;
        private String paymentMethod;
        private String note;
        private Integer redeemedPoints;
        private String voucherCode;
        private java.math.BigDecimal shippingFee;
        private String shippingInfo;

        public java.util.List<com.lumiere.app.service.dto.GuestCartItemDTO> getCartItems() {
            return cartItems;
        }

        public void setCartItems(java.util.List<com.lumiere.app.service.dto.GuestCartItemDTO> cartItems) {
            this.cartItems = cartItems;
        }

        public String getPaymentMethod() {
            return paymentMethod;
        }

        public void setPaymentMethod(String paymentMethod) {
            this.paymentMethod = paymentMethod;
        }

        public String getNote() {
            return note;
        }

        public void setNote(String note) {
            this.note = note;
        }

        public Integer getRedeemedPoints() {
            return redeemedPoints;
        }

        public void setRedeemedPoints(Integer redeemedPoints) {
            this.redeemedPoints = redeemedPoints;
        }

        public String getVoucherCode() {
            return voucherCode;
        }

        public void setVoucherCode(String voucherCode) {
            this.voucherCode = voucherCode;
        }

        public java.math.BigDecimal getShippingFee() {
            return shippingFee;
        }

        public void setShippingFee(java.math.BigDecimal shippingFee) {
            this.shippingFee = shippingFee;
        }

        public String getShippingInfo() {
            return shippingInfo;
        }

        public void setShippingInfo(String shippingInfo) {
            this.shippingInfo = shippingInfo;
        }
    }
}
