package com.lumiere.app.repository;

import com.lumiere.app.domain.OrderStatusHistory;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the OrderStatusHistory entity.
 */
@Repository
public interface OrderStatusHistoryRepository extends JpaRepository<OrderStatusHistory, Long> {
    default Optional<OrderStatusHistory> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<OrderStatusHistory> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<OrderStatusHistory> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select orderStatusHistory from OrderStatusHistory orderStatusHistory left join fetch orderStatusHistory.order",
        countQuery = "select count(orderStatusHistory) from OrderStatusHistory orderStatusHistory"
    )
    Page<OrderStatusHistory> findAllWithToOneRelationships(Pageable pageable);

    @Query("select orderStatusHistory from OrderStatusHistory orderStatusHistory left join fetch orderStatusHistory.order")
    List<OrderStatusHistory> findAllWithToOneRelationships();

    @Query(
        "select orderStatusHistory from OrderStatusHistory orderStatusHistory left join fetch orderStatusHistory.order where orderStatusHistory.id =:id"
    )
    Optional<OrderStatusHistory> findOneWithToOneRelationships(@Param("id") Long id);

    /**
     * Tìm lịch sử trạng thái đơn hàng theo ID đơn hàng, sắp xếp theo thời gian giảm dần.
     */
    @Query("select h from OrderStatusHistory h where h.order.id = :orderId order by h.timestamp desc")
    List<OrderStatusHistory> findByOrderIdOrderByTimestampDesc(@Param("orderId") Long orderId);
}
