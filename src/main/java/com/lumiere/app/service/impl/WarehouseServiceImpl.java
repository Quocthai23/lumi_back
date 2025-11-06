package com.lumiere.app.service.impl;

import com.lumiere.app.domain.Warehouse;
import com.lumiere.app.repository.WarehouseRepository;
import com.lumiere.app.service.WarehouseService;
import com.lumiere.app.service.dto.WarehouseDTO;
import com.lumiere.app.service.mapper.WarehouseMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.lumiere.app.domain.Warehouse}.
 */
@Service
@Transactional
public class WarehouseServiceImpl implements WarehouseService {

    private static final Logger LOG = LoggerFactory.getLogger(WarehouseServiceImpl.class);

    private final WarehouseRepository warehouseRepository;

    private final WarehouseMapper warehouseMapper;

    public WarehouseServiceImpl(WarehouseRepository warehouseRepository, WarehouseMapper warehouseMapper) {
        this.warehouseRepository = warehouseRepository;
        this.warehouseMapper = warehouseMapper;
    }

    @Override
    public WarehouseDTO save(WarehouseDTO warehouseDTO) {
        LOG.debug("Request to save Warehouse : {}", warehouseDTO);
        Warehouse warehouse = warehouseMapper.toEntity(warehouseDTO);
        warehouse = warehouseRepository.save(warehouse);
        return warehouseMapper.toDto(warehouse);
    }

    @Override
    public WarehouseDTO update(WarehouseDTO warehouseDTO) {
        LOG.debug("Request to update Warehouse : {}", warehouseDTO);
        Warehouse warehouse = warehouseMapper.toEntity(warehouseDTO);
        warehouse = warehouseRepository.save(warehouse);
        return warehouseMapper.toDto(warehouse);
    }

    @Override
    public Optional<WarehouseDTO> partialUpdate(WarehouseDTO warehouseDTO) {
        LOG.debug("Request to partially update Warehouse : {}", warehouseDTO);

        return warehouseRepository
            .findById(warehouseDTO.getId())
            .map(existingWarehouse -> {
                warehouseMapper.partialUpdate(existingWarehouse, warehouseDTO);

                return existingWarehouse;
            })
            .map(warehouseRepository::save)
            .map(warehouseMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<WarehouseDTO> findOne(Long id) {
        LOG.debug("Request to get Warehouse : {}", id);
        return warehouseRepository.findById(id).map(warehouseMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Warehouse : {}", id);
        warehouseRepository.deleteById(id);
    }
}
