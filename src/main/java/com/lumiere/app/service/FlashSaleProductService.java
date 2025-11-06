package com.lumiere.app.service;

import com.lumiere.app.service.dto.FlashSaleProductDTO;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.lumiere.app.domain.FlashSaleProduct}.
 */
public interface FlashSaleProductService {
    /**
     * Save a flashSaleProduct.
     *
     * @param flashSaleProductDTO the entity to save.
     * @return the persisted entity.
     */
    FlashSaleProductDTO save(FlashSaleProductDTO flashSaleProductDTO);

    /**
     * Updates a flashSaleProduct.
     *
     * @param flashSaleProductDTO the entity to update.
     * @return the persisted entity.
     */
    FlashSaleProductDTO update(FlashSaleProductDTO flashSaleProductDTO);

    /**
     * Partially updates a flashSaleProduct.
     *
     * @param flashSaleProductDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<FlashSaleProductDTO> partialUpdate(FlashSaleProductDTO flashSaleProductDTO);

    /**
     * Get all the flashSaleProducts.
     *
     * @return the list of entities.
     */
    List<FlashSaleProductDTO> findAll();

    /**
     * Get all the flashSaleProducts with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<FlashSaleProductDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" flashSaleProduct.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<FlashSaleProductDTO> findOne(Long id);

    /**
     * Delete the "id" flashSaleProduct.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
