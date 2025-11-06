package com.lumiere.app.service;

import com.lumiere.app.service.dto.LoyaltyTransactionDTO;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.lumiere.app.domain.LoyaltyTransaction}.
 */
public interface LoyaltyTransactionService {
    /**
     * Save a loyaltyTransaction.
     *
     * @param loyaltyTransactionDTO the entity to save.
     * @return the persisted entity.
     */
    LoyaltyTransactionDTO save(LoyaltyTransactionDTO loyaltyTransactionDTO);

    /**
     * Updates a loyaltyTransaction.
     *
     * @param loyaltyTransactionDTO the entity to update.
     * @return the persisted entity.
     */
    LoyaltyTransactionDTO update(LoyaltyTransactionDTO loyaltyTransactionDTO);

    /**
     * Partially updates a loyaltyTransaction.
     *
     * @param loyaltyTransactionDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<LoyaltyTransactionDTO> partialUpdate(LoyaltyTransactionDTO loyaltyTransactionDTO);

    /**
     * Get all the loyaltyTransactions.
     *
     * @return the list of entities.
     */
    List<LoyaltyTransactionDTO> findAll();

    /**
     * Get all the loyaltyTransactions with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<LoyaltyTransactionDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" loyaltyTransaction.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<LoyaltyTransactionDTO> findOne(Long id);

    /**
     * Delete the "id" loyaltyTransaction.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
