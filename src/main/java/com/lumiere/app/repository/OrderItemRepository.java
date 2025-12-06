package com.lumiere.app.repository;

import com.lumiere.app.domain.OrderItem;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the OrderItem entity.
 */
@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    default Optional<OrderItem> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<OrderItem> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<OrderItem> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select orderItem from OrderItem orderItem left join fetch orderItem.order left join fetch orderItem.productVariant",
        countQuery = "select count(orderItem) from OrderItem orderItem"
    )
    Page<OrderItem> findAllWithToOneRelationships(Pageable pageable);

    @Query("select orderItem from OrderItem orderItem left join fetch orderItem.order left join fetch orderItem.productVariant")
    List<OrderItem> findAllWithToOneRelationships();

    @Query(
        "select orderItem from OrderItem orderItem left join fetch orderItem.order left join fetch orderItem.productVariant where orderItem.id =:id"
    )
    Optional<OrderItem> findOneWithToOneRelationships(@Param("id") Long id);

    List<OrderItem> findAllByOrderId(Long orderId);

    /**
     * Lấy tất cả orderItems của một đơn hàng với đầy đủ productVariant và product.
     */
    @Query(
        "select distinct orderItem from OrderItem orderItem " +
        "left join fetch orderItem.productVariant productVariant " +
        "left join fetch productVariant.product " +
        "where orderItem.order.id = :orderId"
    )
    List<OrderItem> findAllByOrderIdWithVariants(@Param("orderId") Long orderId);

    /**
     * Lấy top sản phẩm bán chạy nhất dựa trên số lượng bán được.
     */
    @Query(
        value = """
        SELECT p.name, SUM(oi.quantity) as total
        FROM OrderItem oi
        JOIN oi.productVariant pv
        JOIN pv.product p
        JOIN oi.order o
        WHERE o.status IN :statuses
        GROUP BY p.id, p.name
        ORDER BY total DESC
        """,
        countQuery = """
        SELECT COUNT(DISTINCT p.id)
        FROM OrderItem oi
        JOIN oi.productVariant pv
        JOIN pv.product p
        JOIN oi.order o
        WHERE o.status IN :statuses
        """
    )
    org.springframework.data.domain.Page<Object[]> getTopProductsByQuantity(
        @Param("statuses") java.util.List<com.lumiere.app.domain.enumeration.OrderStatus> statuses,
        org.springframework.data.domain.Pageable pageable
    );

    /**
     * Lấy top product IDs bán chạy nhất dựa trên số lượng bán được.
     */
    @Query(
        value = """
        SELECT p.id, SUM(oi.quantity) as totalQuantity
        FROM OrderItem oi
        JOIN oi.productVariant pv
        JOIN pv.product p
        JOIN oi.order o
        WHERE o.status IN :statuses
        AND p.status = :productStatus
        GROUP BY p.id
        ORDER BY totalQuantity DESC
        """
    )
    List<Object[]> getTopProductIdsByQuantity(
        @Param("statuses") java.util.List<com.lumiere.app.domain.enumeration.OrderStatus> statuses,
        @Param("productStatus") com.lumiere.app.domain.enumeration.ProductStatus productStatus,
        org.springframework.data.domain.Pageable pageable
    );
}
