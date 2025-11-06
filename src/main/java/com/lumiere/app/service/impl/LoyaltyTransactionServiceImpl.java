package com.lumiere.app.service.impl;

import com.lumiere.app.domain.LoyaltyTransaction;
import com.lumiere.app.repository.LoyaltyTransactionRepository;
import com.lumiere.app.service.LoyaltyTransactionService;
import com.lumiere.app.service.dto.LoyaltyTransactionDTO;
import com.lumiere.app.service.mapper.LoyaltyTransactionMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.lumiere.app.domain.LoyaltyTransaction}.
 */
@Service
@Transactional
public class LoyaltyTransactionServiceImpl implements LoyaltyTransactionService {

    private static final Logger LOG = LoggerFactory.getLogger(LoyaltyTransactionServiceImpl.class);

    private final LoyaltyTransactionRepository loyaltyTransactionRepository;

    private final LoyaltyTransactionMapper loyaltyTransactionMapper;

    public LoyaltyTransactionServiceImpl(
        LoyaltyTransactionRepository loyaltyTransactionRepository,
        LoyaltyTransactionMapper loyaltyTransactionMapper
    ) {
        this.loyaltyTransactionRepository = loyaltyTransactionRepository;
        this.loyaltyTransactionMapper = loyaltyTransactionMapper;
    }

    @Override
    public LoyaltyTransactionDTO save(LoyaltyTransactionDTO loyaltyTransactionDTO) {
        LOG.debug("Request to save LoyaltyTransaction : {}", loyaltyTransactionDTO);
        LoyaltyTransaction loyaltyTransaction = loyaltyTransactionMapper.toEntity(loyaltyTransactionDTO);
        loyaltyTransaction = loyaltyTransactionRepository.save(loyaltyTransaction);
        return loyaltyTransactionMapper.toDto(loyaltyTransaction);
    }

    @Override
    public LoyaltyTransactionDTO update(LoyaltyTransactionDTO loyaltyTransactionDTO) {
        LOG.debug("Request to update LoyaltyTransaction : {}", loyaltyTransactionDTO);
        LoyaltyTransaction loyaltyTransaction = loyaltyTransactionMapper.toEntity(loyaltyTransactionDTO);
        loyaltyTransaction = loyaltyTransactionRepository.save(loyaltyTransaction);
        return loyaltyTransactionMapper.toDto(loyaltyTransaction);
    }

    @Override
    public Optional<LoyaltyTransactionDTO> partialUpdate(LoyaltyTransactionDTO loyaltyTransactionDTO) {
        LOG.debug("Request to partially update LoyaltyTransaction : {}", loyaltyTransactionDTO);

        return loyaltyTransactionRepository
            .findById(loyaltyTransactionDTO.getId())
            .map(existingLoyaltyTransaction -> {
                loyaltyTransactionMapper.partialUpdate(existingLoyaltyTransaction, loyaltyTransactionDTO);

                return existingLoyaltyTransaction;
            })
            .map(loyaltyTransactionRepository::save)
            .map(loyaltyTransactionMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LoyaltyTransactionDTO> findAll() {
        LOG.debug("Request to get all LoyaltyTransactions");
        return loyaltyTransactionRepository
            .findAll()
            .stream()
            .map(loyaltyTransactionMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    public Page<LoyaltyTransactionDTO> findAllWithEagerRelationships(Pageable pageable) {
        return loyaltyTransactionRepository.findAllWithEagerRelationships(pageable).map(loyaltyTransactionMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<LoyaltyTransactionDTO> findOne(Long id) {
        LOG.debug("Request to get LoyaltyTransaction : {}", id);
        return loyaltyTransactionRepository.findOneWithEagerRelationships(id).map(loyaltyTransactionMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete LoyaltyTransaction : {}", id);
        loyaltyTransactionRepository.deleteById(id);
    }
}
