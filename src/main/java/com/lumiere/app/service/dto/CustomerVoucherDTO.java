package com.lumiere.app.service.dto;

import java.io.Serializable;
import java.time.Instant;

/**
 * DTO cho CustomerVoucher.
 */
public class CustomerVoucherDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private VoucherDTO voucher;
    private Instant giftedAt;
    private String quarter;
    private Boolean used;

    public CustomerVoucherDTO() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public VoucherDTO getVoucher() {
        return voucher;
    }

    public void setVoucher(VoucherDTO voucher) {
        this.voucher = voucher;
    }

    public Instant getGiftedAt() {
        return giftedAt;
    }

    public void setGiftedAt(Instant giftedAt) {
        this.giftedAt = giftedAt;
    }

    public String getQuarter() {
        return quarter;
    }

    public void setQuarter(String quarter) {
        this.quarter = quarter;
    }

    public Boolean getUsed() {
        return used;
    }

    public void setUsed(Boolean used) {
        this.used = used;
    }
}

