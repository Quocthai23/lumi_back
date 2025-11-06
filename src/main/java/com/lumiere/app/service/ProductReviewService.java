package com.lumiere.app.service;

import com.lumiere.app.service.dto.ProductReviewDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.lumiere.app.domain.ProductReview}.
 */
public interface ProductReviewService {
    /**
     * Save a productReview.
     *
     * @param productReviewDTO the entity to save.
     * @return the persisted entity.
     */
    ProductReviewDTO save(ProductReviewDTO productReviewDTO);

    /**
     * Updates a productReview.
     *
     * @param productReviewDTO the entity to update.
     * @return the persisted entity.
     */
    ProductReviewDTO update(ProductReviewDTO productReviewDTO);

    /**
     * Partially updates a productReview.
     *
     * @param productReviewDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<ProductReviewDTO> partialUpdate(ProductReviewDTO productReviewDTO);

    /**
     * Get all the productReviews.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ProductReviewDTO> findAll(Pageable pageable);

    /**
     * Get all the productReviews with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ProductReviewDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" productReview.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<ProductReviewDTO> findOne(Long id);

    /**
     * Delete the "id" productReview.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
