package com.lumiere.app.service;

import com.lumiere.app.domain.*; // for static metamodels
import com.lumiere.app.domain.Product;
import com.lumiere.app.repository.ProductRepository;
import com.lumiere.app.service.criteria.ProductCriteria;
import com.lumiere.app.service.dto.ProductDTO;
import com.lumiere.app.service.mapper.ProductMapper;
import jakarta.persistence.criteria.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Product} entities in the database.
 * The main input is a {@link ProductCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link ProductDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ProductQueryService extends QueryService<Product> {

    private static final Logger LOG = LoggerFactory.getLogger(ProductQueryService.class);

    private final ProductRepository productRepository;

    private final ProductMapper productMapper;

    public ProductQueryService(ProductRepository productRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }

    /**
     * Return a {@link Page} of {@link ProductDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<ProductDTO> findByCriteria(ProductCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Product> specification = createSpecification(criteria);
        return productRepository.findAll(specification, page).map(productMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ProductCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<Product> specification = createSpecification(criteria);
        return productRepository.count(specification);
    }

    /**
     * Function to convert {@link ProductCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Product> createSpecification(ProductCriteria criteria) {
        Specification<Product> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildRangeSpecification(criteria.getId(), Product_.id),
                buildStringSpecification(criteria.getCode(), Product_.code),
                buildStringSpecification(criteria.getName(), Product_.name),
                buildStringSpecification(criteria.getSlug(), Product_.slug),
                buildSpecification(criteria.getStatus(), Product_.status),
                buildStringSpecification(criteria.getCategory(), Product_.category),
                buildStringSpecification(criteria.getMaterial(), Product_.material),
                buildRangeSpecification(criteria.getAverageRating(), Product_.averageRating),
                buildRangeSpecification(criteria.getReviewCount(), Product_.reviewCount),
                buildRangeSpecification(criteria.getCreatedAt(), Product_.createdAt),
                buildRangeSpecification(criteria.getUpdatedAt(), Product_.updatedAt),
                buildSpecification(criteria.getVariantsId(), root -> root.join(Product_.variants, JoinType.LEFT).get(ProductVariant_.id)),
                buildSpecification(criteria.getReviewsId(), root -> root.join(Product_.reviews, JoinType.LEFT).get(ProductReview_.id)),
                buildSpecification(criteria.getQuestionsId(), root -> root.join(Product_.questions, JoinType.LEFT).get(ProductQuestion_.id)
                ),
                buildSpecification(criteria.getCollectionsId(), root -> root.join(Product_.collections, JoinType.LEFT).get(Collection_.id)),
                buildSpecification(criteria.getWishlistedById(), root -> root.join(Product_.wishlistedBies, JoinType.LEFT).get(Customer_.id)
                )
            );
        }
        return specification;
    }
}
