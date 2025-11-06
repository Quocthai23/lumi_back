package com.lumiere.app.service;

import com.lumiere.app.service.dto.StockNotificationDTO;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.lumiere.app.domain.StockNotification}.
 */
public interface StockNotificationService {
    /**
     * Save a stockNotification.
     *
     * @param stockNotificationDTO the entity to save.
     * @return the persisted entity.
     */
    StockNotificationDTO save(StockNotificationDTO stockNotificationDTO);

    /**
     * Updates a stockNotification.
     *
     * @param stockNotificationDTO the entity to update.
     * @return the persisted entity.
     */
    StockNotificationDTO update(StockNotificationDTO stockNotificationDTO);

    /**
     * Partially updates a stockNotification.
     *
     * @param stockNotificationDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<StockNotificationDTO> partialUpdate(StockNotificationDTO stockNotificationDTO);

    /**
     * Get all the stockNotifications.
     *
     * @return the list of entities.
     */
    List<StockNotificationDTO> findAll();

    /**
     * Get all the stockNotifications with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<StockNotificationDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" stockNotification.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<StockNotificationDTO> findOne(Long id);

    /**
     * Delete the "id" stockNotification.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
