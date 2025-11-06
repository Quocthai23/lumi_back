package com.lumiere.app.service.impl;

import com.lumiere.app.domain.StockNotification;
import com.lumiere.app.repository.StockNotificationRepository;
import com.lumiere.app.service.StockNotificationService;
import com.lumiere.app.service.dto.StockNotificationDTO;
import com.lumiere.app.service.mapper.StockNotificationMapper;
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
 * Service Implementation for managing {@link com.lumiere.app.domain.StockNotification}.
 */
@Service
@Transactional
public class StockNotificationServiceImpl implements StockNotificationService {

    private static final Logger LOG = LoggerFactory.getLogger(StockNotificationServiceImpl.class);

    private final StockNotificationRepository stockNotificationRepository;

    private final StockNotificationMapper stockNotificationMapper;

    public StockNotificationServiceImpl(
        StockNotificationRepository stockNotificationRepository,
        StockNotificationMapper stockNotificationMapper
    ) {
        this.stockNotificationRepository = stockNotificationRepository;
        this.stockNotificationMapper = stockNotificationMapper;
    }

    @Override
    public StockNotificationDTO save(StockNotificationDTO stockNotificationDTO) {
        LOG.debug("Request to save StockNotification : {}", stockNotificationDTO);
        StockNotification stockNotification = stockNotificationMapper.toEntity(stockNotificationDTO);
        stockNotification = stockNotificationRepository.save(stockNotification);
        return stockNotificationMapper.toDto(stockNotification);
    }

    @Override
    public StockNotificationDTO update(StockNotificationDTO stockNotificationDTO) {
        LOG.debug("Request to update StockNotification : {}", stockNotificationDTO);
        StockNotification stockNotification = stockNotificationMapper.toEntity(stockNotificationDTO);
        stockNotification = stockNotificationRepository.save(stockNotification);
        return stockNotificationMapper.toDto(stockNotification);
    }

    @Override
    public Optional<StockNotificationDTO> partialUpdate(StockNotificationDTO stockNotificationDTO) {
        LOG.debug("Request to partially update StockNotification : {}", stockNotificationDTO);

        return stockNotificationRepository
            .findById(stockNotificationDTO.getId())
            .map(existingStockNotification -> {
                stockNotificationMapper.partialUpdate(existingStockNotification, stockNotificationDTO);

                return existingStockNotification;
            })
            .map(stockNotificationRepository::save)
            .map(stockNotificationMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StockNotificationDTO> findAll() {
        LOG.debug("Request to get all StockNotifications");
        return stockNotificationRepository
            .findAll()
            .stream()
            .map(stockNotificationMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    public Page<StockNotificationDTO> findAllWithEagerRelationships(Pageable pageable) {
        return stockNotificationRepository.findAllWithEagerRelationships(pageable).map(stockNotificationMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<StockNotificationDTO> findOne(Long id) {
        LOG.debug("Request to get StockNotification : {}", id);
        return stockNotificationRepository.findOneWithEagerRelationships(id).map(stockNotificationMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete StockNotification : {}", id);
        stockNotificationRepository.deleteById(id);
    }
}
