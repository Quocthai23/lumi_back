package com.lumiere.app.service;

import com.lumiere.app.service.dto.CollectionDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.lumiere.app.domain.Collection}.
 */
public interface CollectionService {
    /**
     * Save a collection.
     *
     * @param collectionDTO the entity to save.
     * @return the persisted entity.
     */
    CollectionDTO save(CollectionDTO collectionDTO);

    /**
     * Updates a collection.
     *
     * @param collectionDTO the entity to update.
     * @return the persisted entity.
     */
    CollectionDTO update(CollectionDTO collectionDTO);

    /**
     * Partially updates a collection.
     *
     * @param collectionDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<CollectionDTO> partialUpdate(CollectionDTO collectionDTO);

    /**
     * Get all the collections.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<CollectionDTO> findAll(Pageable pageable);

    /**
     * Get all the collections with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<CollectionDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" collection.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<CollectionDTO> findOne(Long id);

    /**
     * Delete the "id" collection.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
