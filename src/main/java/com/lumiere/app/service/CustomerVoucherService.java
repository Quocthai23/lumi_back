package com.lumiere.app.service;

import com.lumiere.app.service.dto.CustomerVoucherDTO;
import java.util.List;

/**
 * Service Interface for managing CustomerVoucher.
 */
public interface CustomerVoucherService {
    /**
     * Lấy tất cả voucher đã được tặng cho khách hàng theo userId.
     *
     * @param userId ID của user
     * @return danh sách CustomerVoucherDTO
     */
    List<CustomerVoucherDTO> getVouchersByUserId(Long userId);

    /**
     * Claim (lấy) một voucher cho khách hàng.
     *
     * @param userId ID của user
     * @param voucherId ID của voucher
     * @return CustomerVoucherDTO đã được tạo
     */
    CustomerVoucherDTO claimVoucher(Long userId, Long voucherId);
}

