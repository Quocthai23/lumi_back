package com.lumiere.app.repository;

import com.lumiere.app.domain.FlashSaleProduct;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the FlashSaleProduct entity.
 */
@Repository
public interface FlashSaleProductRepository extends JpaRepository<FlashSaleProduct, Long> {
    default Optional<FlashSaleProduct> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<FlashSaleProduct> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<FlashSaleProduct> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select flashSaleProduct from FlashSaleProduct flashSaleProduct left join fetch flashSaleProduct.flashSale left join fetch flashSaleProduct.product",
        countQuery = "select count(flashSaleProduct) from FlashSaleProduct flashSaleProduct"
    )
    Page<FlashSaleProduct> findAllWithToOneRelationships(Pageable pageable);

    @Query(
        "select flashSaleProduct from FlashSaleProduct flashSaleProduct left join fetch flashSaleProduct.flashSale left join fetch flashSaleProduct.product"
    )
    List<FlashSaleProduct> findAllWithToOneRelationships();

    @Query(
        "select flashSaleProduct from FlashSaleProduct flashSaleProduct left join fetch flashSaleProduct.flashSale left join fetch flashSaleProduct.product where flashSaleProduct.id =:id"
    )
    Optional<FlashSaleProduct> findOneWithToOneRelationships(@Param("id") Long id);
}
