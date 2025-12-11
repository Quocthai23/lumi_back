package com.lumiere.app.service;

import com.lumiere.app.service.dto.OrdersDTO;
import java.util.List;
import java.util.Optional;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.lumiere.app.domain.Orders}.
 */
public interface OrdersService {
    /**
     * Save a orders.
     *
     * @param ordersDTO the entity to save.
     * @return the persisted entity.
     */
    OrdersDTO save(OrdersDTO ordersDTO);

    /**
     * Updates a orders.
     *
     * @param ordersDTO the entity to update.
     * @return the persisted entity.
     */
    OrdersDTO update(OrdersDTO ordersDTO);

    /**
     * Partially updates a orders.
     *
     * @param ordersDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<OrdersDTO> partialUpdate(OrdersDTO ordersDTO);

    /**
     * Get all the orders with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<OrdersDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" orders.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<OrdersDTO> findOne(Long id);

    /**
     * Delete the "id" orders.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    void writeOrderInvoiceExcel(Long orderId, HttpServletResponse response);

    /**
     * Tạo đơn hàng từ giỏ hàng của khách hàng hiện tại.
     *
     * @param paymentMethod phương thức thanh toán
     * @param note ghi chú đơn hàng
     * @param redeemedPoints điểm tích lũy sử dụng
     * @param voucherCode mã voucher (tùy chọn)
     * @param shippingCost phí vận chuyển
     * @param shippingInfo thông tin giao hàng dạng JSON
     * @return đơn hàng đã tạo
     */
    OrdersDTO createOrderFromCart(String paymentMethod, String note, Integer redeemedPoints, String voucherCode, java.math.BigDecimal shippingCost, String shippingInfo);

    /**
     * Cập nhật trạng thái đơn hàng.
     *
     * @param orderId ID đơn hàng
     * @param newStatus trạng thái mới
     * @param description mô tả thay đổi
     * @return đơn hàng đã cập nhật
     */
    OrdersDTO updateOrderStatus(Long orderId, com.lumiere.app.domain.enumeration.OrderStatus newStatus, String description);

    /**
     * Hủy đơn hàng.
     *
     * @param orderId ID đơn hàng
     * @param reason lý do hủy
     * @return đơn hàng đã hủy
     */
    OrdersDTO cancelOrder(Long orderId, String reason);

    /**
     * Xác nhận đơn hàng.
     *
     * @param orderId ID đơn hàng
     * @return đơn hàng đã xác nhận
     */
    OrdersDTO confirmOrder(Long orderId);

    /**
     * Xác nhận đã thanh toán cho đơn hàng.
     *
     * @param orderId ID đơn hàng
     * @return đơn hàng đã cập nhật trạng thái thanh toán
     */
    OrdersDTO confirmPayment(Long orderId);

    /**
     * Lấy tất cả đơn hàng của khách hàng.
     *
     * @param customerId ID khách hàng
     * @param pageable thông tin phân trang
     * @return danh sách đơn hàng
     */
    Page<OrdersDTO> getOrdersByCustomerId(Long customerId, Pageable pageable);

    /**
     * Lấy đơn hàng theo mã đơn hàng.
     *
     * @param code mã đơn hàng
     * @return đơn hàng
     */
    Optional<OrdersDTO> findByCode(String code);

    /**
     * Lấy lịch sử trạng thái đơn hàng.
     *
     * @param orderId ID đơn hàng
     * @return danh sách lịch sử trạng thái
     */
    List<com.lumiere.app.service.dto.OrderStatusHistoryDTO> getOrderStatusHistory(Long orderId);

    /**
     * Tạo review cho sản phẩm từ đơn hàng.
     *
     * @param orderId ID đơn hàng
     * @param orderItemId ID order item (sản phẩm trong đơn hàng)
     * @param rating điểm đánh giá
     * @param comment bình luận
     * @param author tên người đánh giá
     * @return review đã tạo
     */
    com.lumiere.app.service.dto.ProductReviewDTO createReviewForOrderItem(
        Long orderId,
        Long orderItemId,
        com.lumiere.app.domain.enumeration.RatingType rating,
        String comment,
        String author
    );

    /**
     * Lấy danh sách sản phẩm có thể review từ đơn hàng.
     * Chỉ trả về các sản phẩm trong đơn hàng đã được giao (DELIVERED hoặc COMPLETED).
     *
     * @param orderId ID đơn hàng
     * @return danh sách order items có thể review
     */
    List<com.lumiere.app.service.dto.OrderItemDTO> getReviewableProductsFromOrder(Long orderId);

    /**
     * Kiểm tra xem khách hàng đã review sản phẩm chưa.
     *
     * @param customerId ID khách hàng
     * @param productId ID sản phẩm
     * @return true nếu đã review, false nếu chưa
     */
    boolean hasCustomerReviewedProduct(Long customerId, Long productId);

    /**
     * Lấy tất cả review của các sản phẩm trong đơn hàng.
     *
     * @param orderId ID đơn hàng
     * @return danh sách review
     */
    List<com.lumiere.app.service.dto.ProductReviewDTO> getReviewsForOrder(Long orderId);

    /**
     * Tạo review cho toàn bộ đơn hàng.
     * Tạo review cho từng sản phẩm trong order và bắn Kafka để tính điểm.
     *
     * @param orderId ID đơn hàng
     * @param reviews danh sách review cho từng sản phẩm (orderItemId -> review info)
     * @return danh sách review đã tạo
     */
    List<com.lumiere.app.service.dto.ProductReviewDTO> createReviewsForOrder(
        Long orderId,
        List<com.lumiere.app.service.dto.CreateOrderReviewDTO> reviews
    );

    /**
     * Tính toán trạng thái đơn hàng tiếp theo dựa trên trạng thái hiện tại và trạng thái thanh toán.
     *
     * @param currentStatus trạng thái hiện tại
     * @param paymentStatus trạng thái thanh toán
     * @return trạng thái tiếp theo, hoặc null nếu không có trạng thái tiếp theo
     */
    com.lumiere.app.domain.enumeration.OrderStatus getNextOrderStatus(
        com.lumiere.app.domain.enumeration.OrderStatus currentStatus,
        com.lumiere.app.domain.enumeration.PaymentStatus paymentStatus
    );
}
