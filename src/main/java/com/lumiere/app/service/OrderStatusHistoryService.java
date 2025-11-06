package com.lumiere.app.service;

import com.lumiere.app.service.dto.OrderStatusHistoryDTO;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.lumiere.app.domain.OrderStatusHistory}.
 */
public interface OrderStatusHistoryService {
    /**
     * Save a orderStatusHistory.
     *
     * @param orderStatusHistoryDTO the entity to save.
     * @return the persisted entity.
     */
    OrderStatusHistoryDTO save(OrderStatusHistoryDTO orderStatusHistoryDTO);

    /**
     * Updates a orderStatusHistory.
     *
     * @param orderStatusHistoryDTO the entity to update.
     * @return the persisted entity.
     */
    OrderStatusHistoryDTO update(OrderStatusHistoryDTO orderStatusHistoryDTO);

    /**
     * Partially updates a orderStatusHistory.
     *
     * @param orderStatusHistoryDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<OrderStatusHistoryDTO> partialUpdate(OrderStatusHistoryDTO orderStatusHistoryDTO);

    /**
     * Get all the orderStatusHistories.
     *
     * @return the list of entities.
     */
    List<OrderStatusHistoryDTO> findAll();

    /**
     * Get all the orderStatusHistories with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<OrderStatusHistoryDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" orderStatusHistory.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<OrderStatusHistoryDTO> findOne(Long id);

    /**
     * Delete the "id" orderStatusHistory.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
