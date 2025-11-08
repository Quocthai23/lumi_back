// src/main/java/com/lumi/app/domain/enumeration/AdjustmentType.java
package com.lumiere.app.domain.enumeration;

public enum AdjustmentType {
    INCREASE,     // Nhập kho / cộng thêm
    DECREASE,     // Xuất kho / trừ đi
    CORRECTION,   // Chốt số (điều chỉnh về đúng số thực tế)
    RESERVATION,  // Giữ hàng (đặt trước)
    RELEASE,      // Hủy giữ hàng
    RETURNED,     // Hàng trả lại
    DAMAGED       // Hàng hỏng
}
