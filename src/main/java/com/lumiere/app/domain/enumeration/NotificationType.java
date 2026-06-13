package com.lumiere.app.domain.enumeration;

/**
 * The NotificationType enumeration.
 */
public enum NotificationType {
    // Notifications cho Admin
    NEW_ORDER,              // Có khách đặt đơn
    NEW_REVIEW,             // Có khách review sản phẩm
    LOW_STOCK,              // Có sản phẩm gần hết
    VOUCHER_EXPIRING,       // Có mã giảm giá sắp hết thời gian
    ORDER_CANCELLED,        // Có người dùng hủy đơn
    NEW_CONTACT,            // Có người dùng tạo liên hệ
    
    // Notifications cho Customer
    ORDER_UPDATE,           // Đơn hàng được cập nhật trạng thái
    NEW_FLASHSALE,          // Có flashsale mới
    NEW_VOUCHER,            // Có voucher mới
    BIRTHDAY,               // Thông báo chúc mừng sinh nhật
    CONTACT_REPLY,          // Khi có người rep contact message
    
    // Legacy types (giữ lại để tương thích)
    NEW_ANSWER,
    PROMOTION,
    NEW_CUSTOMER,
    NEW_QA,
}
