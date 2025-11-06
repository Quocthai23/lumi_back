package com.lumiere.app.service.impl;

import com.lumiere.app.domain.ProductAnswer;
import com.lumiere.app.repository.ProductAnswerRepository;
import com.lumiere.app.service.ProductAnswerService;
import com.lumiere.app.service.dto.ProductAnswerDTO;
import com.lumiere.app.service.mapper.ProductAnswerMapper;
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
 * Service Implementation for managing {@link com.lumiere.app.domain.ProductAnswer}.
 */
@Service
@Transactional
public class ProductAnswerServiceImpl implements ProductAnswerService {

    private static final Logger LOG = LoggerFactory.getLogger(ProductAnswerServiceImpl.class);

    private final ProductAnswerRepository productAnswerRepository;

    private final ProductAnswerMapper productAnswerMapper;

    public ProductAnswerServiceImpl(ProductAnswerRepository productAnswerRepository, ProductAnswerMapper productAnswerMapper) {
        this.productAnswerRepository = productAnswerRepository;
        this.productAnswerMapper = productAnswerMapper;
    }

    @Override
    public ProductAnswerDTO save(ProductAnswerDTO productAnswerDTO) {
        LOG.debug("Request to save ProductAnswer : {}", productAnswerDTO);
        ProductAnswer productAnswer = productAnswerMapper.toEntity(productAnswerDTO);
        productAnswer = productAnswerRepository.save(productAnswer);
        return productAnswerMapper.toDto(productAnswer);
    }

    @Override
    public ProductAnswerDTO update(ProductAnswerDTO productAnswerDTO) {
        LOG.debug("Request to update ProductAnswer : {}", productAnswerDTO);
        ProductAnswer productAnswer = productAnswerMapper.toEntity(productAnswerDTO);
        productAnswer = productAnswerRepository.save(productAnswer);
        return productAnswerMapper.toDto(productAnswer);
    }

    @Override
    public Optional<ProductAnswerDTO> partialUpdate(ProductAnswerDTO productAnswerDTO) {
        LOG.debug("Request to partially update ProductAnswer : {}", productAnswerDTO);

        return productAnswerRepository
            .findById(productAnswerDTO.getId())
            .map(existingProductAnswer -> {
                productAnswerMapper.partialUpdate(existingProductAnswer, productAnswerDTO);

                return existingProductAnswer;
            })
            .map(productAnswerRepository::save)
            .map(productAnswerMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductAnswerDTO> findAll() {
        LOG.debug("Request to get all ProductAnswers");
        return productAnswerRepository.findAll().stream().map(productAnswerMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    public Page<ProductAnswerDTO> findAllWithEagerRelationships(Pageable pageable) {
        return productAnswerRepository.findAllWithEagerRelationships(pageable).map(productAnswerMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ProductAnswerDTO> findOne(Long id) {
        LOG.debug("Request to get ProductAnswer : {}", id);
        return productAnswerRepository.findOneWithEagerRelationships(id).map(productAnswerMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete ProductAnswer : {}", id);
        productAnswerRepository.deleteById(id);
    }
}
