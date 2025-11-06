package com.lumiere.app.service;

import com.lumiere.app.service.dto.ProductQuestionDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.lumiere.app.domain.ProductQuestion}.
 */
public interface ProductQuestionService {
    /**
     * Save a productQuestion.
     *
     * @param productQuestionDTO the entity to save.
     * @return the persisted entity.
     */
    ProductQuestionDTO save(ProductQuestionDTO productQuestionDTO);

    /**
     * Updates a productQuestion.
     *
     * @param productQuestionDTO the entity to update.
     * @return the persisted entity.
     */
    ProductQuestionDTO update(ProductQuestionDTO productQuestionDTO);

    /**
     * Partially updates a productQuestion.
     *
     * @param productQuestionDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<ProductQuestionDTO> partialUpdate(ProductQuestionDTO productQuestionDTO);

    /**
     * Get all the productQuestions.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ProductQuestionDTO> findAll(Pageable pageable);

    /**
     * Get all the productQuestions with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ProductQuestionDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" productQuestion.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<ProductQuestionDTO> findOne(Long id);

    /**
     * Delete the "id" productQuestion.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
