package com.lumiere.app.service.impl;

import com.lumiere.app.domain.OrderStatusHistory;
import com.lumiere.app.repository.OrderStatusHistoryRepository;
import com.lumiere.app.service.OrderStatusHistoryService;
import com.lumiere.app.service.dto.OrderStatusHistoryDTO;
import com.lumiere.app.service.mapper.OrderStatusHistoryMapper;
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
 * Service Implementation for managing {@link com.lumiere.app.domain.OrderStatusHistory}.
 */
@Service
@Transactional
public class OrderStatusHistoryServiceImpl implements OrderStatusHistoryService {

    private static final Logger LOG = LoggerFactory.getLogger(OrderStatusHistoryServiceImpl.class);

    private final OrderStatusHistoryRepository orderStatusHistoryRepository;

    private final OrderStatusHistoryMapper orderStatusHistoryMapper;

    public OrderStatusHistoryServiceImpl(
        OrderStatusHistoryRepository orderStatusHistoryRepository,
        OrderStatusHistoryMapper orderStatusHistoryMapper
    ) {
        this.orderStatusHistoryRepository = orderStatusHistoryRepository;
        this.orderStatusHistoryMapper = orderStatusHistoryMapper;
    }

    @Override
    public OrderStatusHistoryDTO save(OrderStatusHistoryDTO orderStatusHistoryDTO) {
        LOG.debug("Request to save OrderStatusHistory : {}", orderStatusHistoryDTO);
        OrderStatusHistory orderStatusHistory = orderStatusHistoryMapper.toEntity(orderStatusHistoryDTO);
        orderStatusHistory = orderStatusHistoryRepository.save(orderStatusHistory);
        return orderStatusHistoryMapper.toDto(orderStatusHistory);
    }

    @Override
    public OrderStatusHistoryDTO update(OrderStatusHistoryDTO orderStatusHistoryDTO) {
        LOG.debug("Request to update OrderStatusHistory : {}", orderStatusHistoryDTO);
        OrderStatusHistory orderStatusHistory = orderStatusHistoryMapper.toEntity(orderStatusHistoryDTO);
        orderStatusHistory = orderStatusHistoryRepository.save(orderStatusHistory);
        return orderStatusHistoryMapper.toDto(orderStatusHistory);
    }

    @Override
    public Optional<OrderStatusHistoryDTO> partialUpdate(OrderStatusHistoryDTO orderStatusHistoryDTO) {
        LOG.debug("Request to partially update OrderStatusHistory : {}", orderStatusHistoryDTO);

        return orderStatusHistoryRepository
            .findById(orderStatusHistoryDTO.getId())
            .map(existingOrderStatusHistory -> {
                orderStatusHistoryMapper.partialUpdate(existingOrderStatusHistory, orderStatusHistoryDTO);

                return existingOrderStatusHistory;
            })
            .map(orderStatusHistoryRepository::save)
            .map(orderStatusHistoryMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderStatusHistoryDTO> findAll() {
        LOG.debug("Request to get all OrderStatusHistories");
        return orderStatusHistoryRepository
            .findAll()
            .stream()
            .map(orderStatusHistoryMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    public Page<OrderStatusHistoryDTO> findAllWithEagerRelationships(Pageable pageable) {
        return orderStatusHistoryRepository.findAllWithEagerRelationships(pageable).map(orderStatusHistoryMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<OrderStatusHistoryDTO> findOne(Long id) {
        LOG.debug("Request to get OrderStatusHistory : {}", id);
        return orderStatusHistoryRepository.findOneWithEagerRelationships(id).map(orderStatusHistoryMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete OrderStatusHistory : {}", id);
        orderStatusHistoryRepository.deleteById(id);
    }
}
