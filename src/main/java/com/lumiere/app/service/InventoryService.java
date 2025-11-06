package com.lumiere.app.service;

import com.lumiere.app.service.dto.InventoryDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.lumiere.app.domain.Inventory}.
 */
public interface InventoryService {
    /**
     * Save a inventory.
     *
     * @param inventoryDTO the entity to save.
     * @return the persisted entity.
     */
    InventoryDTO save(InventoryDTO inventoryDTO);

    /**
     * Updates a inventory.
     *
     * @param inventoryDTO the entity to update.
     * @return the persisted entity.
     */
    InventoryDTO update(InventoryDTO inventoryDTO);

    /**
     * Partially updates a inventory.
     *
     * @param inventoryDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<InventoryDTO> partialUpdate(InventoryDTO inventoryDTO);

    /**
     * Get all the inventories with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<InventoryDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" inventory.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<InventoryDTO> findOne(Long id);

    /**
     * Delete the "id" inventory.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
