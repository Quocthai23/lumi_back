package com.lumiere.app.service.impl;

import com.lumiere.app.domain.ProductReview;
import com.lumiere.app.repository.ProductReviewRepository;
import com.lumiere.app.service.ProductReviewService;
import com.lumiere.app.service.dto.ProductReviewDTO;
import com.lumiere.app.service.mapper.ProductReviewMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.lumiere.app.domain.ProductReview}.
 */
@Service
@Transactional
public class ProductReviewServiceImpl implements ProductReviewService {

    private static final Logger LOG = LoggerFactory.getLogger(ProductReviewServiceImpl.class);

    private final ProductReviewRepository productReviewRepository;

    private final ProductReviewMapper productReviewMapper;

    public ProductReviewServiceImpl(ProductReviewRepository productReviewRepository, ProductReviewMapper productReviewMapper) {
        this.productReviewRepository = productReviewRepository;
        this.productReviewMapper = productReviewMapper;
    }

    @Override
    public ProductReviewDTO save(ProductReviewDTO productReviewDTO) {
        LOG.debug("Request to save ProductReview : {}", productReviewDTO);
        ProductReview productReview = productReviewMapper.toEntity(productReviewDTO);
        productReview = productReviewRepository.save(productReview);
        return productReviewMapper.toDto(productReview);
    }

    @Override
    public ProductReviewDTO update(ProductReviewDTO productReviewDTO) {
        LOG.debug("Request to update ProductReview : {}", productReviewDTO);
        ProductReview productReview = productReviewMapper.toEntity(productReviewDTO);
        productReview = productReviewRepository.save(productReview);
        return productReviewMapper.toDto(productReview);
    }

    @Override
    public Optional<ProductReviewDTO> partialUpdate(ProductReviewDTO productReviewDTO) {
        LOG.debug("Request to partially update ProductReview : {}", productReviewDTO);

        return productReviewRepository
            .findById(productReviewDTO.getId())
            .map(existingProductReview -> {
                productReviewMapper.partialUpdate(existingProductReview, productReviewDTO);

                return existingProductReview;
            })
            .map(productReviewRepository::save)
            .map(productReviewMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductReviewDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all ProductReviews");
        return productReviewRepository.findAll(pageable).map(productReviewMapper::toDto);
    }

    public Page<ProductReviewDTO> findAllWithEagerRelationships(Pageable pageable) {
        return productReviewRepository.findAllWithEagerRelationships(pageable).map(productReviewMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ProductReviewDTO> findOne(Long id) {
        LOG.debug("Request to get ProductReview : {}", id);
        return productReviewRepository.findOneWithEagerRelationships(id).map(productReviewMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete ProductReview : {}", id);
        productReviewRepository.deleteById(id);
    }
}
