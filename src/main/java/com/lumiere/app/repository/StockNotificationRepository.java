package com.lumiere.app.repository;

import com.lumiere.app.domain.StockNotification;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the StockNotification entity.
 */
@Repository
public interface StockNotificationRepository extends JpaRepository<StockNotification, Long> {
    default Optional<StockNotification> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<StockNotification> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<StockNotification> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select stockNotification from StockNotification stockNotification left join fetch stockNotification.productVariant",
        countQuery = "select count(stockNotification) from StockNotification stockNotification"
    )
    Page<StockNotification> findAllWithToOneRelationships(Pageable pageable);

    @Query("select stockNotification from StockNotification stockNotification left join fetch stockNotification.productVariant")
    List<StockNotification> findAllWithToOneRelationships();

    @Query(
        "select stockNotification from StockNotification stockNotification left join fetch stockNotification.productVariant where stockNotification.id =:id"
    )
    Optional<StockNotification> findOneWithToOneRelationships(@Param("id") Long id);
}
