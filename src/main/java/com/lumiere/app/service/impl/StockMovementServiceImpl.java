package com.lumiere.app.service.impl;

import com.lumiere.app.domain.StockMovement;
import com.lumiere.app.repository.StockMovementRepository;
import com.lumiere.app.service.StockMovementService;
import com.lumiere.app.service.dto.StockMovementDTO;
import com.lumiere.app.service.mapper.StockMovementMapper;
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
 * Service Implementation for managing {@link com.lumiere.app.domain.StockMovement}.
 */
@Service
@Transactional
public class StockMovementServiceImpl implements StockMovementService {

    private static final Logger LOG = LoggerFactory.getLogger(StockMovementServiceImpl.class);

    private final StockMovementRepository stockMovementRepository;

    private final StockMovementMapper stockMovementMapper;

    public StockMovementServiceImpl(StockMovementRepository stockMovementRepository, StockMovementMapper stockMovementMapper) {
        this.stockMovementRepository = stockMovementRepository;
        this.stockMovementMapper = stockMovementMapper;
    }

    @Override
    public StockMovementDTO save(StockMovementDTO stockMovementDTO) {
        LOG.debug("Request to save StockMovement : {}", stockMovementDTO);
        StockMovement stockMovement = stockMovementMapper.toEntity(stockMovementDTO);
        stockMovement = stockMovementRepository.save(stockMovement);
        return stockMovementMapper.toDto(stockMovement);
    }

    @Override
    public StockMovementDTO update(StockMovementDTO stockMovementDTO) {
        LOG.debug("Request to update StockMovement : {}", stockMovementDTO);
        StockMovement stockMovement = stockMovementMapper.toEntity(stockMovementDTO);
        stockMovement = stockMovementRepository.save(stockMovement);
        return stockMovementMapper.toDto(stockMovement);
    }

    @Override
    public Optional<StockMovementDTO> partialUpdate(StockMovementDTO stockMovementDTO) {
        LOG.debug("Request to partially update StockMovement : {}", stockMovementDTO);

        return stockMovementRepository
            .findById(stockMovementDTO.getId())
            .map(existingStockMovement -> {
                stockMovementMapper.partialUpdate(existingStockMovement, stockMovementDTO);

                return existingStockMovement;
            })
            .map(stockMovementRepository::save)
            .map(stockMovementMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StockMovementDTO> findAll() {
        LOG.debug("Request to get all StockMovements");
        return stockMovementRepository.findAll().stream().map(stockMovementMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    public Page<StockMovementDTO> findAllWithEagerRelationships(Pageable pageable) {
        return stockMovementRepository.findAllWithEagerRelationships(pageable).map(stockMovementMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<StockMovementDTO> findOne(Long id) {
        LOG.debug("Request to get StockMovement : {}", id);
        return stockMovementRepository.findOneWithEagerRelationships(id).map(stockMovementMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete StockMovement : {}", id);
        stockMovementRepository.deleteById(id);
    }
}
