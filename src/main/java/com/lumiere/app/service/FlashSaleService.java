package com.lumiere.app.service;

import com.lumiere.app.service.dto.FlashSaleDTO;
import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.lumiere.app.domain.FlashSale}.
 */
public interface FlashSaleService {
    /**
     * Save a flashSale.
     *
     * @param flashSaleDTO the entity to save.
     * @return the persisted entity.
     */
    FlashSaleDTO save(FlashSaleDTO flashSaleDTO);

    /**
     * Updates a flashSale.
     *
     * @param flashSaleDTO the entity to update.
     * @return the persisted entity.
     */
    FlashSaleDTO update(FlashSaleDTO flashSaleDTO);

    /**
     * Partially updates a flashSale.
     *
     * @param flashSaleDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<FlashSaleDTO> partialUpdate(FlashSaleDTO flashSaleDTO);

    /**
     * Get all the flashSales.
     *
     * @return the list of entities.
     */
    List<FlashSaleDTO> findAll();

    /**
     * Get the "id" flashSale.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<FlashSaleDTO> findOne(Long id);

    /**
     * Delete the "id" flashSale.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Get all active flash sales (currently running).
     *
     * @return the list of active flash sales.
     */
    List<FlashSaleDTO> findActiveFlashSales();

    /**
     * Get all upcoming flash sales.
     *
     * @return the list of upcoming flash sales.
     */
    List<FlashSaleDTO> findUpcomingFlashSales();

    /**
     * Get all ended flash sales.
     *
     * @return the list of ended flash sales.
     */
    List<FlashSaleDTO> findEndedFlashSales();

    /**
     * Get current active flash sale (first one if multiple).
     *
     * @return the current flash sale, or empty if none.
     */
    Optional<FlashSaleDTO> findCurrentFlashSale();
}
