package com.lumiere.app.service;

/**
 * Service Interface for managing quarterly voucher distribution.
 */
public interface QuarterlyVoucherService {
    /**
     * Tặng voucher đặc biệt cho tất cả khách hàng theo tier mỗi quý.
     * Chạy tự động vào đầu mỗi quý.
     */
    void distributeQuarterlyVouchers();
}

