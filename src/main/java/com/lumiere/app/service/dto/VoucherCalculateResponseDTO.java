package com.lumiere.app.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * DTO cho response tính tiền giảm giá từ voucher code.
 */
@Schema(description = "Response tính tiền giảm giá từ voucher code")
public class VoucherCalculateResponseDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "Số tiền giảm giá")
    private BigDecimal discountAmount;

    @Schema(description = "Tổng tiền đơn hàng sau khi giảm giá")
    private BigDecimal finalAmount;

    @Schema(description = "Thông tin voucher")
    private VoucherDTO voucher;

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    public BigDecimal getFinalAmount() {
        return finalAmount;
    }

    public void setFinalAmount(BigDecimal finalAmount) {
        this.finalAmount = finalAmount;
    }

    public VoucherDTO getVoucher() {
        return voucher;
    }

    public void setVoucher(VoucherDTO voucher) {
        this.voucher = voucher;
    }

    @Override
    public String toString() {
        return "VoucherCalculateResponseDTO{" +
            "discountAmount=" + discountAmount +
            ", finalAmount=" + finalAmount +
            ", voucher=" + voucher +
            '}';
    }
}

