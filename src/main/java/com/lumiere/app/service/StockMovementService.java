package com.lumiere.app.service;

import com.lumiere.app.service.dto.StockMovementDTO;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.lumiere.app.domain.StockMovement}.
 */
public interface StockMovementService {
    /**
     * Save a stockMovement.
     *
     * @param stockMovementDTO the entity to save.
     * @return the persisted entity.
     */
    StockMovementDTO save(StockMovementDTO stockMovementDTO);

    /**
     * Updates a stockMovement.
     *
     * @param stockMovementDTO the entity to update.
     * @return the persisted entity.
     */
    StockMovementDTO update(StockMovementDTO stockMovementDTO);

    /**
     * Partially updates a stockMovement.
     *
     * @param stockMovementDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<StockMovementDTO> partialUpdate(StockMovementDTO stockMovementDTO);

    /**
     * Get all the stockMovements.
     *
     * @return the list of entities.
     */
    List<StockMovementDTO> findAll();

    /**
     * Get all the stockMovements with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<StockMovementDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" stockMovement.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<StockMovementDTO> findOne(Long id);

    /**
     * Delete the "id" stockMovement.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
