package com.lumiere.app.service.impl;

import com.lumiere.app.domain.Orders;
import com.lumiere.app.repository.OrdersRepository;
import com.lumiere.app.service.OrdersService;
import com.lumiere.app.service.dto.OrdersDTO;
import com.lumiere.app.service.mapper.OrdersMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.lumiere.app.domain.Orders}.
 */
@Service
@Transactional
public class OrdersServiceImpl implements OrdersService {

    private static final Logger LOG = LoggerFactory.getLogger(OrdersServiceImpl.class);

    private final OrdersRepository ordersRepository;

    private final OrdersMapper ordersMapper;

    public OrdersServiceImpl(OrdersRepository ordersRepository, OrdersMapper ordersMapper) {
        this.ordersRepository = ordersRepository;
        this.ordersMapper = ordersMapper;
    }

    @Override
    public OrdersDTO save(OrdersDTO ordersDTO) {
        LOG.debug("Request to save Orders : {}", ordersDTO);
        Orders orders = ordersMapper.toEntity(ordersDTO);
        orders = ordersRepository.save(orders);
        return ordersMapper.toDto(orders);
    }

    @Override
    public OrdersDTO update(OrdersDTO ordersDTO) {
        LOG.debug("Request to update Orders : {}", ordersDTO);
        Orders orders = ordersMapper.toEntity(ordersDTO);
        orders = ordersRepository.save(orders);
        return ordersMapper.toDto(orders);
    }

    @Override
    public Optional<OrdersDTO> partialUpdate(OrdersDTO ordersDTO) {
        LOG.debug("Request to partially update Orders : {}", ordersDTO);

        return ordersRepository
            .findById(ordersDTO.getId())
            .map(existingOrders -> {
                ordersMapper.partialUpdate(existingOrders, ordersDTO);

                return existingOrders;
            })
            .map(ordersRepository::save)
            .map(ordersMapper::toDto);
    }

    public Page<OrdersDTO> findAllWithEagerRelationships(Pageable pageable) {
        return ordersRepository.findAllWithEagerRelationships(pageable).map(ordersMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<OrdersDTO> findOne(Long id) {
        LOG.debug("Request to get Orders : {}", id);
        return ordersRepository.findOneWithEagerRelationships(id).map(ordersMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Orders : {}", id);
        ordersRepository.deleteById(id);
    }
}
