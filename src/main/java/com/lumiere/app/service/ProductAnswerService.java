package com.lumiere.app.service;

import com.lumiere.app.service.dto.ProductAnswerDTO;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.lumiere.app.domain.ProductAnswer}.
 */
public interface ProductAnswerService {
    /**
     * Save a productAnswer.
     *
     * @param productAnswerDTO the entity to save.
     * @return the persisted entity.
     */
    ProductAnswerDTO save(ProductAnswerDTO productAnswerDTO);

    /**
     * Updates a productAnswer.
     *
     * @param productAnswerDTO the entity to update.
     * @return the persisted entity.
     */
    ProductAnswerDTO update(ProductAnswerDTO productAnswerDTO);

    /**
     * Partially updates a productAnswer.
     *
     * @param productAnswerDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<ProductAnswerDTO> partialUpdate(ProductAnswerDTO productAnswerDTO);

    /**
     * Get all the productAnswers.
     *
     * @return the list of entities.
     */
    List<ProductAnswerDTO> findAll();

    /**
     * Get all the productAnswers with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ProductAnswerDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" productAnswer.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<ProductAnswerDTO> findOne(Long id);

    /**
     * Delete the "id" productAnswer.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
