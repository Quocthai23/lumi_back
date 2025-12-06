package com.lumiere.app.repository;

import com.lumiere.app.domain.ProductReview;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ProductReview entity.
 */
@Repository
public interface ProductReviewRepository extends JpaRepository<ProductReview, Long> {
    default Optional<ProductReview> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<ProductReview> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<ProductReview> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select productReview from ProductReview productReview left join fetch productReview.product",
        countQuery = "select count(productReview) from ProductReview productReview"
    )
    Page<ProductReview> findAllWithToOneRelationships(Pageable pageable);

    @Query("select productReview from ProductReview productReview left join fetch productReview.product")
    List<ProductReview> findAllWithToOneRelationships();

    @Query("select productReview from ProductReview productReview left join fetch productReview.product where productReview.id =:id")
    Optional<ProductReview> findOneWithToOneRelationships(@Param("id") Long id);

    /**
     * Tìm tất cả reviews đã được approved của một sản phẩm.
     */
    @Query(
        "select productReview from ProductReview productReview " +
        "left join fetch productReview.product " +
        "where productReview.product.id = :productId " +
        "and productReview.status = :status"
    )
    List<ProductReview> findByProductIdAndStatus(
        @Param("productId") Long productId,
        @Param("status") com.lumiere.app.domain.enumeration.ReviewStatus status
    );
}
