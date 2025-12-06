package com.lumiere.app.repository;

import com.lumiere.app.domain.OptionVariant;
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
        value = "select productVariant from ProductVariant productVariant left join fetch productVariant.product",
        countQuery = "select count(productVariant) from ProductVariant productVariant"
    )
    Page<ProductVariant> findAllWithToOneRelationships(Pageable pageable);

    @Query("select productVariant from ProductVariant productVariant left join fetch productVariant.product")
    List<ProductVariant> findAllWithToOneRelationships();

    List<ProductVariant> findByProduct_Id(Long productId);

    @Query("select productVariant from ProductVariant productVariant left join fetch productVariant.product where productVariant.id =:id")
    Optional<ProductVariant> findOneWithToOneRelationships(@Param("id") Long id);

    List<ProductVariant> findAllByProduct_Id(Long productId);
    void deleteByIdIn(Collection<Long> ids);

    List<ProductVariant> findAllByIdIn(Collection<Long> ids);
}
