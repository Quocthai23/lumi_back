package com.lumiere.app.service.impl;

import com.lumiere.app.domain.Inventory;
import com.lumiere.app.repository.InventoryRepository;
import com.lumiere.app.service.InventoryService;
import com.lumiere.app.service.ProductVariantService;
import com.lumiere.app.service.dto.InventoryDTO;
import com.lumiere.app.service.dto.ProductVariantDTO;
import com.lumiere.app.service.mapper.InventoryMapper;

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
 * Service Implementation for managing {@link com.lumiere.app.domain.Inventory}.
 */
@Service
@Transactional
public class InventoryServiceImpl implements InventoryService {

    private static final Logger LOG = LoggerFactory.getLogger(InventoryServiceImpl.class);

    private final InventoryRepository inventoryRepository;

    private final InventoryMapper inventoryMapper;
    private final ProductVariantService productVariantService;

    public InventoryServiceImpl(InventoryRepository inventoryRepository, InventoryMapper inventoryMapper, ProductVariantService productVariantService) {
        this.inventoryRepository = inventoryRepository;
        this.inventoryMapper = inventoryMapper;
        this.productVariantService = productVariantService;
    }

    @Override
    public InventoryDTO save(InventoryDTO inventoryDTO) {
        LOG.debug("Request to save Inventory : {}", inventoryDTO);
        Inventory inventory = inventoryMapper.toEntity(inventoryDTO);
        inventory = inventoryRepository.save(inventory);
        return inventoryMapper.toDto(inventory);
    }

    @Override
    public InventoryDTO update(InventoryDTO inventoryDTO) {
        LOG.debug("Request to update Inventory : {}", inventoryDTO);
        Inventory inventory = inventoryMapper.toEntity(inventoryDTO);
        inventory = inventoryRepository.save(inventory);
        return inventoryMapper.toDto(inventory);
    }

    @Override
    public Optional<InventoryDTO> partialUpdate(InventoryDTO inventoryDTO) {
        LOG.debug("Request to partially update Inventory : {}", inventoryDTO);

        return inventoryRepository
            .findById(inventoryDTO.getId())
            .map(existingInventory -> {
                inventoryMapper.partialUpdate(existingInventory, inventoryDTO);

                return existingInventory;
            })
            .map(inventoryRepository::save)
            .map(inventoryMapper::toDto);
    }

    public Page<InventoryDTO> findAllWithEagerRelationships(Pageable pageable) {
        return inventoryRepository.findAllWithEagerRelationships(pageable).map(inventoryMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<InventoryDTO> findOne(Long id) {
        LOG.debug("Request to get Inventory : {}", id);
        return inventoryRepository.findOneWithEagerRelationships(id).map(inventoryMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Inventory : {}", id);
        inventoryRepository.deleteById(id);
    }

    @Override
    public List<InventoryDTO> getInventoryByProductId(Long productId){
        List<Long> variantDTOS = productVariantService
            .findByProductId(productId).stream().map(ProductVariantDTO::getId).toList();

        List<Inventory> inventories = inventoryRepository.findAllByProductVariant_IdIn(variantDTOS);

        List<InventoryDTO> inventoryDTOS = inventoryMapper.toDto(inventories);

        return inventoryDTOS;
    }
}
