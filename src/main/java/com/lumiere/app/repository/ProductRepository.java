package com.lumiere.app.repository;

import com.lumiere.app.domain.Product;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Product entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
    boolean existsBySlug(String slug);

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

    @Query(
        """
        SELECT DISTINCT p
        FROM Product p
        LEFT JOIN FETCH p.productAttachments pa
        LEFT JOIN FETCH pa.attachment a
        WHERE p.id IN :ids
        """
    )
    List<Product> findWithAttachmentsByIdIn(@Param("ids") List<Long> ids);

    @Modifying
    @Query(value = "DELETE FROM rel_collection__products WHERE products_id = :id", nativeQuery = true)
    void deleteCollectionRelations(@Param("id") Long id);

    @Modifying
    @Query(value = "DELETE FROM rel_customer__wishlist WHERE wishlist_id = :id", nativeQuery = true)
    void deleteWishlistRelations(@Param("id") Long id);

    @Modifying
    @Query(value = "DELETE FROM cart_item WHERE product_id = :id", nativeQuery = true)
    void deleteCartItemsByProductId(@Param("id") Long id);

    @Modifying
    @Query(
        value = "UPDATE order_item SET product_variant_id = NULL WHERE product_variant_id IN (SELECT id FROM product_variant WHERE product_id = :id)",
        nativeQuery = true
    )
    void unlinkOrderItemsByProductId(@Param("id") Long id);

    @Modifying
    @Query(
        value = "DELETE FROM inventory WHERE product_variant_id IN (SELECT id FROM product_variant WHERE product_id = :id)",
        nativeQuery = true
    )
    void deleteInventoryByProductId(@Param("id") Long id);

    @Modifying
    @Query(
        value = "DELETE FROM stock_movement WHERE product_variant_id IN (SELECT id FROM product_variant WHERE product_id = :id)",
        nativeQuery = true
    )
    void deleteStockMovementByProductId(@Param("id") Long id);

    @Modifying
    @Query(
        value = "DELETE FROM stock_notification WHERE product_variant_id IN (SELECT id FROM product_variant WHERE product_id = :id)",
        nativeQuery = true
    )
    void deleteStockNotificationByProductId(@Param("id") Long id);

    @Modifying
    @Query(
        value = "DELETE FROM flash_sale_product WHERE product_variant_id IN (SELECT id FROM product_variant WHERE product_id = :id)",
        nativeQuery = true
    )
    void deleteFlashSaleProductByProductId(@Param("id") Long id);
}
