package com.lumiere.app.service.impl;

import com.lumiere.app.domain.ProductQuestion;
import com.lumiere.app.repository.ProductQuestionRepository;
import com.lumiere.app.service.ProductQuestionService;
import com.lumiere.app.service.dto.ProductQuestionDTO;
import com.lumiere.app.service.mapper.ProductQuestionMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.lumiere.app.domain.ProductQuestion}.
 */
@Service
@Transactional
public class ProductQuestionServiceImpl implements ProductQuestionService {

    private static final Logger LOG = LoggerFactory.getLogger(ProductQuestionServiceImpl.class);

    private final ProductQuestionRepository productQuestionRepository;

    private final ProductQuestionMapper productQuestionMapper;

    public ProductQuestionServiceImpl(ProductQuestionRepository productQuestionRepository, ProductQuestionMapper productQuestionMapper) {
        this.productQuestionRepository = productQuestionRepository;
        this.productQuestionMapper = productQuestionMapper;
    }

    @Override
    public ProductQuestionDTO save(ProductQuestionDTO productQuestionDTO) {
        LOG.debug("Request to save ProductQuestion : {}", productQuestionDTO);
        ProductQuestion productQuestion = productQuestionMapper.toEntity(productQuestionDTO);
        productQuestion = productQuestionRepository.save(productQuestion);
        return productQuestionMapper.toDto(productQuestion);
    }

    @Override
    public ProductQuestionDTO update(ProductQuestionDTO productQuestionDTO) {
        LOG.debug("Request to update ProductQuestion : {}", productQuestionDTO);
        ProductQuestion productQuestion = productQuestionMapper.toEntity(productQuestionDTO);
        productQuestion = productQuestionRepository.save(productQuestion);
        return productQuestionMapper.toDto(productQuestion);
    }

    @Override
    public Optional<ProductQuestionDTO> partialUpdate(ProductQuestionDTO productQuestionDTO) {
        LOG.debug("Request to partially update ProductQuestion : {}", productQuestionDTO);

        return productQuestionRepository
            .findById(productQuestionDTO.getId())
            .map(existingProductQuestion -> {
                productQuestionMapper.partialUpdate(existingProductQuestion, productQuestionDTO);

                return existingProductQuestion;
            })
            .map(productQuestionRepository::save)
            .map(productQuestionMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductQuestionDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all ProductQuestions");
        return productQuestionRepository.findAll(pageable).map(productQuestionMapper::toDto);
    }

    public Page<ProductQuestionDTO> findAllWithEagerRelationships(Pageable pageable) {
        return productQuestionRepository.findAllWithEagerRelationships(pageable).map(productQuestionMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ProductQuestionDTO> findOne(Long id) {
        LOG.debug("Request to get ProductQuestion : {}", id);
        return productQuestionRepository.findOneWithEagerRelationships(id).map(productQuestionMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete ProductQuestion : {}", id);
        productQuestionRepository.deleteById(id);
    }
}
