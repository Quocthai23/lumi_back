package com.lumiere.app.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * DTO cho request tính tiền giảm giá từ voucher code.
 */
@Schema(description = "Request tính tiền giảm giá từ voucher code")
public class VoucherCalculateRequestDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "Mã voucher (có thể để trống nếu không sử dụng voucher)")
    private String voucherCode;

    @NotNull(message = "Tổng tiền đơn hàng không được để trống")
    @DecimalMin(value = "0", message = "Tổng tiền đơn hàng phải lớn hơn hoặc bằng 0")
    @Schema(description = "Tổng tiền đơn hàng")
    private BigDecimal orderAmount;

    public String getVoucherCode() {
        return voucherCode;
    }

    public void setVoucherCode(String voucherCode) {
        this.voucherCode = voucherCode;
    }

    public BigDecimal getOrderAmount() {
        return orderAmount;
    }

    public void setOrderAmount(BigDecimal orderAmount) {
        this.orderAmount = orderAmount;
    }

    @Override
    public String toString() {
        return "VoucherCalculateRequestDTO{" +
            "voucherCode='" + voucherCode + '\'' +
            ", orderAmount=" + orderAmount +
            '}';
    }
}

