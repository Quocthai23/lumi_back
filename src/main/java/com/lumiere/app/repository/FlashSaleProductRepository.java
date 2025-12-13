package com.lumiere.app.repository;

import com.lumiere.app.domain.FlashSaleProduct;
import java.time.Instant;
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
        value = "select flashSaleProduct from FlashSaleProduct flashSaleProduct left join fetch flashSaleProduct.flashSale left join fetch flashSaleProduct.productVariant",
        countQuery = "select count(flashSaleProduct) from FlashSaleProduct flashSaleProduct"
    )
    Page<FlashSaleProduct> findAllWithToOneRelationships(Pageable pageable);

    @Query(
        "select flashSaleProduct from FlashSaleProduct flashSaleProduct left join fetch flashSaleProduct.flashSale left join fetch flashSaleProduct.productVariant"
    )
    List<FlashSaleProduct> findAllWithToOneRelationships();

    @Query(
        "select flashSaleProduct from FlashSaleProduct flashSaleProduct left join fetch flashSaleProduct.flashSale left join fetch flashSaleProduct.productVariant where flashSaleProduct.id =:id"
    )
    Optional<FlashSaleProduct> findOneWithToOneRelationships(@Param("id") Long id);

    /**
     * Tìm tất cả flash sale products theo flash sale id
     */
    @Query(
        "select flashSaleProduct from FlashSaleProduct flashSaleProduct left join fetch flashSaleProduct.flashSale left join fetch flashSaleProduct.productVariant where flashSaleProduct.flashSale.id = :flashSaleId"
    )
    List<FlashSaleProduct> findByFlashSaleId(@Param("flashSaleId") Long flashSaleId);

    /**
     * Tìm flash sale product theo product variant id và flash sale đang active
     */
    @Query(
        "select flashSaleProduct from FlashSaleProduct flashSaleProduct left join fetch flashSaleProduct.flashSale left join fetch flashSaleProduct.productVariant " +
        "where flashSaleProduct.productVariant.id = :productVariantId " +
        "and flashSaleProduct.flashSale.startTime <= :now " +
        "and flashSaleProduct.flashSale.endTime >= :now"
    )
    Optional<FlashSaleProduct> findActiveByProductVariantId(@Param("productVariantId") Long productVariantId, @Param("now") java.time.Instant now);

    /**
     * Tìm flash sale product theo product id và flash sale đang active (tìm qua product variant)
     */
    @Query(
        "select flashSaleProduct from FlashSaleProduct flashSaleProduct left join fetch flashSaleProduct.flashSale left join fetch flashSaleProduct.productVariant " +
        "where flashSaleProduct.productVariant.product.id = :productId " +
        "and flashSaleProduct.flashSale.startTime <= :now " +
        "and flashSaleProduct.flashSale.endTime >= :now"
    )
    Optional<FlashSaleProduct> findActiveByProductId(@Param("productId") Long productId, @Param("now") java.time.Instant now);

    /**
     * Tìm tất cả flash sale products theo product variant id
     */
    @Query(
        "select flashSaleProduct from FlashSaleProduct flashSaleProduct left join fetch flashSaleProduct.flashSale left join fetch flashSaleProduct.productVariant where flashSaleProduct.productVariant.id = :productVariantId"
    )
    List<FlashSaleProduct> findByProductVariantId(@Param("productVariantId") Long productVariantId);

    /**
     * Tìm tất cả flash sale products theo product id (tìm qua product variant)
     */
    @Query(
        "select flashSaleProduct from FlashSaleProduct flashSaleProduct left join fetch flashSaleProduct.flashSale left join fetch flashSaleProduct.productVariant where flashSaleProduct.productVariant.product.id = :productId"
    )
    List<FlashSaleProduct> findByProductId(@Param("productId") Long productId);

    /**
     * Tìm các flash sale products còn hàng (quantity > sold)
     */
    @Query(
        "select flashSaleProduct from FlashSaleProduct flashSaleProduct left join fetch flashSaleProduct.flashSale left join fetch flashSaleProduct.productVariant " +
        "where flashSaleProduct.quantity > flashSaleProduct.sold"
    )
    List<FlashSaleProduct> findAvailableProducts();

    /**
     * Lấy giá flashsale rẻ nhất của mỗi product (chỉ flashsale đang active)
     */
    @Query("""
        SELECT fsp.productVariant.product.id, MIN(fsp.salePrice)
        FROM FlashSaleProduct fsp
        WHERE fsp.productVariant.product.id IN :productIds
          AND fsp.flashSale.startTime <= :now
          AND fsp.flashSale.endTime >= :now
          AND fsp.quantity > fsp.sold
        GROUP BY fsp.productVariant.product.id
        """)
    List<Object[]> findMinFlashSalePriceByProductIds(
        @Param("productIds") List<Long> productIds,
        @Param("now") Instant now
    );
}
