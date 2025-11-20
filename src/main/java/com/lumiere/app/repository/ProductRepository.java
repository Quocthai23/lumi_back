package com.lumiere.app.repository;

import com.lumiere.app.domain.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
 * Spring Data JPA repository for the Product entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
    @Query(
        value = """
            SELECT p.id
            FROM Product p
            WHERE (:categoryIds IS NULL OR p.categoryId IN :categoryIds)
              AND (
                :minPrice IS NULL OR
                (SELECT MIN(v.price) FROM ProductVariant v WHERE v.product = p) >= :minPrice
              )
              AND (
                :maxPrice IS NULL OR
                (SELECT MIN(v.price) FROM ProductVariant v WHERE v.product = p) <= :maxPrice
              )
        """,
        countQuery = """
            SELECT COUNT(p)
            FROM Product p
            WHERE (:categoryIds IS NULL OR p.categoryId IN :categoryIds)
              AND (
                :minPrice IS NULL OR
                (SELECT MIN(v.price) FROM ProductVariant v WHERE v.product = p) >= :minPrice
              )
              AND (
                :maxPrice IS NULL OR
                (SELECT MIN(v.price) FROM ProductVariant v WHERE v.product = p) <= :maxPrice
              )
        """
    )
    Page<Long> searchProductIdsByCategoryAndCheapestVariantPrice(
        @Param("categoryIds") List<Long> categoryIds,
        @Param("minPrice") BigDecimal minPrice,
        @Param("maxPrice") BigDecimal maxPrice,
        Pageable pageable
    );

    @Query("""
    SELECT DISTINCT p
    FROM Product p
    LEFT JOIN FETCH p.productAttachments pa
    LEFT JOIN FETCH pa.attachment a
    WHERE p.id IN :ids
    """)
    List<Product> findWithAttachmentsByIdIn(@Param("ids") List<Long> ids);
}
