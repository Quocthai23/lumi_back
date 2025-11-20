package com.lumiere.app.service.impl;

import com.lumiere.app.domain.ProductVariant;
import com.lumiere.app.repository.ProductVariantRepository;
import com.lumiere.app.service.ProductVariantService;
import com.lumiere.app.service.dto.ProductVariantDTO;
import com.lumiere.app.service.mapper.ProductVariantMapper;

import java.util.List;
import java.util.Optional;

import com.lumiere.app.utils.MergeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.lumiere.app.domain.ProductVariant}.
 */
@Service
@Transactional
public class ProductVariantServiceImpl implements ProductVariantService {

    private static final Logger LOG = LoggerFactory.getLogger(ProductVariantServiceImpl.class);

    private final ProductVariantRepository productVariantRepository;

    private final ProductVariantMapper productVariantMapper;

    public ProductVariantServiceImpl(ProductVariantRepository productVariantRepository, ProductVariantMapper productVariantMapper) {
        this.productVariantRepository = productVariantRepository;
        this.productVariantMapper = productVariantMapper;
    }

    @Override
    public ProductVariantDTO save(ProductVariantDTO productVariantDTO) {
        LOG.debug("Request to save ProductVariant : {}", productVariantDTO);
        ProductVariant productVariant = productVariantMapper.toEntity(productVariantDTO);
        productVariant = productVariantRepository.save(productVariant);
        return productVariantMapper.toDto(productVariant);
    }

    @Override
    public ProductVariantDTO update(ProductVariantDTO productVariantDTO) {
        LOG.debug("Request to update ProductVariant : {}", productVariantDTO);
        ProductVariant productVariant = productVariantMapper.toEntity(productVariantDTO);
        productVariant = productVariantRepository.save(productVariant);
        return productVariantMapper.toDto(productVariant);
    }

    @Override
    public Optional<ProductVariantDTO> partialUpdate(ProductVariantDTO dto) {

        return productVariantRepository
            .findById(dto.getId())
            .map(existing -> {
                ProductVariant incoming = productVariantMapper.toEntity(dto);

                MergeUtils.Options opts = new MergeUtils.Options()
                    .overwriteNulls(false)
                    .replaceCollections(false);

                MergeUtils.merge(incoming, existing, opts);

                return existing;
            })
            .map(productVariantRepository::save)
            .map(productVariantMapper::toDto);
    }

    public Page<ProductVariantDTO> findAllWithEagerRelationships(Pageable pageable) {
        return productVariantRepository.findAllWithEagerRelationships(pageable).map(productVariantMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ProductVariantDTO> findOne(Long id) {
        LOG.debug("Request to get ProductVariant : {}", id);
        return productVariantRepository.findOneWithEagerRelationships(id).map(productVariantMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete ProductVariant : {}", id);
        productVariantRepository.deleteById(id);
    }

    @Override
    public List<ProductVariantDTO> findByProductId(Long productId){
        return productVariantMapper.toDto(productVariantRepository.findByProduct_Id(productId));
    }

    @Override
    public void setIsDefault(Long variantId, Long productId){
               List<ProductVariantDTO> variants = this.findByProductId(productId);
               variants.forEach(v ->  {
                   if(!v.getId().equals(variantId)){
                       v.setIsDefault(false);
                   }else{
                       v.setIsDefault(true);
                   }
               });
               productVariantRepository.saveAll(variants.stream().map(productVariantMapper::toEntity).toList());
    }


}
