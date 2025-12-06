package com.lumiere.app.repository;

import com.lumiere.app.domain.Orders;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Orders entity.
 */
@Repository
public interface OrdersRepository extends JpaRepository<Orders, Long>, JpaSpecificationExecutor<Orders> {
    default Optional<Orders> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Orders> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Orders> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select orders from Orders orders left join fetch orders.customer",
        countQuery = "select count(orders) from Orders orders"
    )
    Page<Orders> findAllWithToOneRelationships(Pageable pageable);

    @Query("select orders from Orders orders left join fetch orders.customer")
    List<Orders> findAllWithToOneRelationships();

    @Query("select orders from Orders orders left join fetch orders.customer where orders.id =:id")
    Optional<Orders> findOneWithToOneRelationships(@Param("id") Long id);

    /**
     * Lấy đơn hàng với tất cả orderItems và productVariant.
     */
    @Query(
        "select distinct orders from Orders orders " +
        "left join fetch orders.customer " +
        "left join fetch orders.orderItems orderItems " +
        "left join fetch orderItems.productVariant productVariant " +
        "left join fetch productVariant.product " +
        "where orders.id =:id"
    )
    Optional<Orders> findOneWithOrderItemsAndVariants(@Param("id") Long id);

    /**
     * Tìm đơn hàng theo mã đơn hàng.
     */
    Optional<Orders> findByCode(String code);

    /**
     * Tìm tất cả đơn hàng của khách hàng.
     */
    Page<Orders> findByCustomerId(Long customerId, Pageable pageable);

    /**
     * Tính tổng doanh thu từ các đơn hàng đã hoàn thành trong khoảng thời gian.
     */
    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Orders o WHERE o.status IN :statuses AND o.placedAt >= :startDate AND o.placedAt < :endDate")
    java.math.BigDecimal sumTotalAmountByStatusAndDateRange(
        @Param("statuses") java.util.List<com.lumiere.app.domain.enumeration.OrderStatus> statuses,
        @Param("startDate") java.time.Instant startDate,
        @Param("endDate") java.time.Instant endDate
    );

    /**
     * Đếm số đơn hàng theo trạng thái trong khoảng thời gian.
     */
    @Query("SELECT COUNT(o) FROM Orders o WHERE o.status IN :statuses AND o.placedAt >= :startDate AND o.placedAt < :endDate")
    Long countByStatusAndDateRange(
        @Param("statuses") java.util.List<com.lumiere.app.domain.enumeration.OrderStatus> statuses,
        @Param("startDate") java.time.Instant startDate,
        @Param("endDate") java.time.Instant endDate
    );

    /**
     * Lấy doanh thu theo tháng trong năm.
     */
    @Query("""
        SELECT FUNCTION('YEAR', o.placedAt) as year, FUNCTION('MONTH', o.placedAt) as month, COALESCE(SUM(o.totalAmount), 0) as total
        FROM Orders o
        WHERE o.status IN :statuses
          AND FUNCTION('YEAR', o.placedAt) = :year
        GROUP BY FUNCTION('YEAR', o.placedAt), FUNCTION('MONTH', o.placedAt)
        ORDER BY month
        """)
    List<Object[]> getRevenueByMonth(
        @Param("statuses") java.util.List<com.lumiere.app.domain.enumeration.OrderStatus> statuses,
        @Param("year") int year
    );

    /**
     * Lấy các đơn hàng gần đây nhất.
     */
    @Query("SELECT o FROM Orders o LEFT JOIN FETCH o.customer c LEFT JOIN FETCH c.user WHERE o.status IN :statuses ORDER BY o.placedAt DESC")
    List<Orders> findRecentOrders(
        @Param("statuses") java.util.List<com.lumiere.app.domain.enumeration.OrderStatus> statuses,
        org.springframework.data.domain.Pageable pageable
    );

}
