package com.lumiere.app.service.impl;

import com.lumiere.app.domain.FlashSaleProduct;
import com.lumiere.app.repository.FlashSaleProductRepository;
import com.lumiere.app.service.FlashSaleProductService;
import com.lumiere.app.service.dto.FlashSaleProductDTO;
import com.lumiere.app.service.mapper.FlashSaleProductMapper;
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
 * Service Implementation for managing {@link com.lumiere.app.domain.FlashSaleProduct}.
 */
@Service
@Transactional
public class FlashSaleProductServiceImpl implements FlashSaleProductService {

    private static final Logger LOG = LoggerFactory.getLogger(FlashSaleProductServiceImpl.class);

    private final FlashSaleProductRepository flashSaleProductRepository;

    private final FlashSaleProductMapper flashSaleProductMapper;

    public FlashSaleProductServiceImpl(
        FlashSaleProductRepository flashSaleProductRepository,
        FlashSaleProductMapper flashSaleProductMapper
    ) {
        this.flashSaleProductRepository = flashSaleProductRepository;
        this.flashSaleProductMapper = flashSaleProductMapper;
    }

    @Override
    public FlashSaleProductDTO save(FlashSaleProductDTO flashSaleProductDTO) {
        LOG.debug("Request to save FlashSaleProduct : {}", flashSaleProductDTO);
        FlashSaleProduct flashSaleProduct = flashSaleProductMapper.toEntity(flashSaleProductDTO);
        flashSaleProduct = flashSaleProductRepository.save(flashSaleProduct);
        return flashSaleProductMapper.toDto(flashSaleProduct);
    }

    @Override
    public FlashSaleProductDTO update(FlashSaleProductDTO flashSaleProductDTO) {
        LOG.debug("Request to update FlashSaleProduct : {}", flashSaleProductDTO);
        FlashSaleProduct flashSaleProduct = flashSaleProductMapper.toEntity(flashSaleProductDTO);
        flashSaleProduct = flashSaleProductRepository.save(flashSaleProduct);
        return flashSaleProductMapper.toDto(flashSaleProduct);
    }

    @Override
    public Optional<FlashSaleProductDTO> partialUpdate(FlashSaleProductDTO flashSaleProductDTO) {
        LOG.debug("Request to partially update FlashSaleProduct : {}", flashSaleProductDTO);

        return flashSaleProductRepository
            .findById(flashSaleProductDTO.getId())
            .map(existingFlashSaleProduct -> {
                flashSaleProductMapper.partialUpdate(existingFlashSaleProduct, flashSaleProductDTO);

                return existingFlashSaleProduct;
            })
            .map(flashSaleProductRepository::save)
            .map(flashSaleProductMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FlashSaleProductDTO> findAll() {
        LOG.debug("Request to get all FlashSaleProducts");
        return flashSaleProductRepository
            .findAll()
            .stream()
            .map(flashSaleProductMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    public Page<FlashSaleProductDTO> findAllWithEagerRelationships(Pageable pageable) {
        return flashSaleProductRepository.findAllWithEagerRelationships(pageable).map(flashSaleProductMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<FlashSaleProductDTO> findOne(Long id) {
        LOG.debug("Request to get FlashSaleProduct : {}", id);
        return flashSaleProductRepository.findOneWithEagerRelationships(id).map(flashSaleProductMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete FlashSaleProduct : {}", id);
        flashSaleProductRepository.deleteById(id);
    }
}
