package com.lumiere.app.service;

import com.lumiere.app.service.dto.ProductVariantDTO;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.lumiere.app.domain.ProductVariant}.
 */
public interface ProductVariantService {
    /**
     * Save a productVariant.
     *
     * @param productVariantDTO the entity to save.
     * @return the persisted entity.
     */
    ProductVariantDTO save(ProductVariantDTO productVariantDTO);

    /**
     * Updates a productVariant.
     *
     * @param productVariantDTO the entity to update.
     * @return the persisted entity.
     */
    ProductVariantDTO update(ProductVariantDTO productVariantDTO);

    /**
     * Partially updates a productVariant.
     *
     * @param productVariantDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<ProductVariantDTO> partialUpdate(ProductVariantDTO productVariantDTO);

    /**
     * Get all the productVariants with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ProductVariantDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" productVariant.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<ProductVariantDTO> findOne(Long id);

    /**
     * Delete the "id" productVariant.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    List<ProductVariantDTO> findByProductId(Long productId);

    void setIsDefault(Long variantId, Long productId);
}
