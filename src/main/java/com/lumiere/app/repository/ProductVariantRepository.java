package com.lumiere.app.repository;

import com.lumiere.app.domain.ProductVariant;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ProductVariant entity.
 */
@Repository
public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long>, JpaSpecificationExecutor<ProductVariant> {
    default Optional<ProductVariant> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<ProductVariant> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<ProductVariant> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select productVariant from ProductVariant productVariant",
        countQuery = "select count(productVariant) from ProductVariant productVariant"
    )
    Page<ProductVariant> findAllWithToOneRelationships(Pageable pageable);

    @Query("select productVariant from ProductVariant productVariant")
    List<ProductVariant> findAllWithToOneRelationships();

    List<ProductVariant> findByProductId(Long productId);

    @Query("select productVariant from ProductVariant productVariant where productVariant.id =:id")
    Optional<ProductVariant> findOneWithToOneRelationships(@Param("id") Long id);

    List<ProductVariant> findAllByProductId(Long productId);
    void deleteByIdIn(Collection<Long> ids);

    List<ProductVariant> findAllByIdIn(Collection<Long> ids);

    /**
     * Lấy giá rẻ nhất của mỗi product (chỉ variant có giá > 0)
     */
    @Query("""
        SELECT v.product.id, MIN(v.price)
        FROM ProductVariant v
        WHERE v.product.id IN :productIds
          AND v.price > 0
        GROUP BY v.product.id
        """)
    List<Object[]> findMinPriceByProductIds(@Param("productIds") List<Long> productIds);
}
