package com.lumiere.app.service.impl;

import com.lumiere.app.domain.FlashSale;
import com.lumiere.app.repository.FlashSaleRepository;
import com.lumiere.app.service.FlashSaleService;
import com.lumiere.app.service.dto.FlashSaleDTO;
import com.lumiere.app.service.mapper.FlashSaleMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.lumiere.app.domain.FlashSale}.
 */
@Service
@Transactional
public class FlashSaleServiceImpl implements FlashSaleService {

    private static final Logger LOG = LoggerFactory.getLogger(FlashSaleServiceImpl.class);

    private final FlashSaleRepository flashSaleRepository;

    private final FlashSaleMapper flashSaleMapper;

    public FlashSaleServiceImpl(FlashSaleRepository flashSaleRepository, FlashSaleMapper flashSaleMapper) {
        this.flashSaleRepository = flashSaleRepository;
        this.flashSaleMapper = flashSaleMapper;
    }

    @Override
    public FlashSaleDTO save(FlashSaleDTO flashSaleDTO) {
        LOG.debug("Request to save FlashSale : {}", flashSaleDTO);
        FlashSale flashSale = flashSaleMapper.toEntity(flashSaleDTO);
        flashSale = flashSaleRepository.save(flashSale);
        return flashSaleMapper.toDto(flashSale);
    }

    @Override
    public FlashSaleDTO update(FlashSaleDTO flashSaleDTO) {
        LOG.debug("Request to update FlashSale : {}", flashSaleDTO);
        FlashSale flashSale = flashSaleMapper.toEntity(flashSaleDTO);
        flashSale = flashSaleRepository.save(flashSale);
        return flashSaleMapper.toDto(flashSale);
    }

    @Override
    public Optional<FlashSaleDTO> partialUpdate(FlashSaleDTO flashSaleDTO) {
        LOG.debug("Request to partially update FlashSale : {}", flashSaleDTO);

        return flashSaleRepository
            .findById(flashSaleDTO.getId())
            .map(existingFlashSale -> {
                flashSaleMapper.partialUpdate(existingFlashSale, flashSaleDTO);

                return existingFlashSale;
            })
            .map(flashSaleRepository::save)
            .map(flashSaleMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FlashSaleDTO> findAll() {
        LOG.debug("Request to get all FlashSales");
        return flashSaleRepository.findAll().stream().map(flashSaleMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<FlashSaleDTO> findOne(Long id) {
        LOG.debug("Request to get FlashSale : {}", id);
        return flashSaleRepository.findById(id).map(flashSaleMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete FlashSale : {}", id);
        flashSaleRepository.deleteById(id);
    }
}
