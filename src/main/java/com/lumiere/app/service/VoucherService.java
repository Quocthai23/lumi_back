package com.lumiere.app.service;

import com.lumiere.app.service.dto.VoucherDTO;
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
}
