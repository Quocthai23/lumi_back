package com.lumiere.app.service;

import com.lumiere.app.domain.*; // for static metamodels
import com.lumiere.app.domain.ProductVariant;
import com.lumiere.app.repository.ProductVariantRepository;
import com.lumiere.app.service.criteria.ProductVariantCriteria;
import com.lumiere.app.service.dto.ProductVariantDTO;
import com.lumiere.app.service.mapper.ProductVariantMapper;
import jakarta.persistence.criteria.JoinType;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link ProductVariant} entities in the database.
 * The main input is a {@link ProductVariantCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link ProductVariantDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ProductVariantQueryService extends QueryService<ProductVariant> {

    private static final Logger LOG = LoggerFactory.getLogger(ProductVariantQueryService.class);

    private final ProductVariantRepository productVariantRepository;

    private final ProductVariantMapper productVariantMapper;

    public ProductVariantQueryService(ProductVariantRepository productVariantRepository, ProductVariantMapper productVariantMapper) {
        this.productVariantRepository = productVariantRepository;
        this.productVariantMapper = productVariantMapper;
    }

    /**
     * Return a {@link List} of {@link ProductVariantDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<ProductVariantDTO> findByCriteria(ProductVariantCriteria criteria) {
        LOG.debug("find by criteria : {}", criteria);
        final Specification<ProductVariant> specification = createSpecification(criteria);
        return productVariantMapper.toDto(productVariantRepository.findAll(specification));
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ProductVariantCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<ProductVariant> specification = createSpecification(criteria);
        return productVariantRepository.count(specification);
    }

    /**
     * Function to convert {@link ProductVariantCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<ProductVariant> createSpecification(ProductVariantCriteria criteria) {
        Specification<ProductVariant> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildRangeSpecification(criteria.getId(), ProductVariant_.id),
                buildStringSpecification(criteria.getSku(), ProductVariant_.sku),
                buildStringSpecification(criteria.getName(), ProductVariant_.name),
                buildRangeSpecification(criteria.getPrice(), ProductVariant_.price),
                buildRangeSpecification(criteria.getCompareAtPrice(), ProductVariant_.compareAtPrice),
                buildStringSpecification(criteria.getCurrency(), ProductVariant_.currency),
                buildRangeSpecification(criteria.getStockQuantity(), ProductVariant_.stockQuantity),
                buildSpecification(criteria.getIsDefault(), ProductVariant_.isDefault),
                buildStringSpecification(criteria.getColor(), ProductVariant_.color),
                buildStringSpecification(criteria.getSize(), ProductVariant_.size),
                buildSpecification(criteria.getProductId(), root -> root.join(ProductVariant_.product, JoinType.LEFT).get(Product_.id))
            );
        }
        return specification;
    }
}
