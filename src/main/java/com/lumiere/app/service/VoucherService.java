package com.lumiere.app.service;

import com.lumiere.app.domain.Voucher;
import com.lumiere.app.service.dto.VoucherCalculateRequestDTO;
import com.lumiere.app.service.dto.VoucherCalculateResponseDTO;
import com.lumiere.app.service.dto.VoucherDTO;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.lumiere.app.domain.Voucher}.
 */
public interface VoucherService {
    /**
     * Save a voucher.
     *
     * @param voucherDTO the entity to save.
     * @return the persisted entity.
     */
    VoucherDTO save(VoucherDTO voucherDTO);

    /**
     * Updates a voucher.
     *
     * @param voucherDTO the entity to update.
     * @return the persisted entity.
     */
    VoucherDTO update(VoucherDTO voucherDTO);

    /**
     * Partially updates a voucher.
     *
     * @param voucherDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<VoucherDTO> partialUpdate(VoucherDTO voucherDTO);

    /**
     * Get all the vouchers.
     *
     * @return the list of entities.
     */
    List<VoucherDTO> findAll();

    /**
     * Get the "id" voucher.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<VoucherDTO> findOne(Long id);

    /**
     * Delete the "id" voucher.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Tìm voucher theo code.
     *
     * @param code mã voucher
     * @return voucher nếu tìm thấy
     */
    Optional<VoucherDTO> findByCode(String code);

    /**
     * Kiểm tra và validate voucher có thể sử dụng.
     *
     * @param voucherCode mã voucher
     * @param orderAmount tổng tiền đơn hàng
     * @return voucher nếu hợp lệ
     * @throws IllegalArgumentException nếu voucher không hợp lệ
     */
    Voucher validateVoucher(String voucherCode, BigDecimal orderAmount);

    /**
     * Tính số tiền giảm giá từ voucher.
     *
     * @param voucher voucher
     * @param orderAmount tổng tiền đơn hàng
     * @return số tiền giảm giá
     */
    BigDecimal calculateDiscountAmount(Voucher voucher, BigDecimal orderAmount);

    /**
     * Áp dụng voucher và tăng usage count.
     *
     * @param voucher voucher
     */
    void applyVoucher(Voucher voucher);

    /**
     * Tính tiền giảm giá từ voucher code với kiểm tra tư cách sử dụng.
     *
     * @param request request chứa voucher code và order amount
     * @param customerId ID của khách hàng
     * @return response chứa discount amount và thông tin voucher
     * @throws IllegalArgumentException nếu voucher không hợp lệ hoặc khách hàng không có quyền sử dụng
     */
    VoucherCalculateResponseDTO calculateDiscount(VoucherCalculateRequestDTO request, Long customerId);
}
