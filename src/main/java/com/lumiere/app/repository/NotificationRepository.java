package com.lumiere.app.repository;

import com.lumiere.app.domain.Notification;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Notification entity.
 */
@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    default Optional<Notification> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Notification> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Notification> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select notification from Notification notification left join fetch notification.customer",
        countQuery = "select count(notification) from Notification notification"
    )
    Page<Notification> findAllWithToOneRelationships(Pageable pageable);

    @Query("select notification from Notification notification left join fetch notification.customer")
    List<Notification> findAllWithToOneRelationships();

    @Query("select notification from Notification notification left join fetch notification.customer where notification.id =:id")
    Optional<Notification> findOneWithToOneRelationships(@Param("id") Long id);

    // Phân trang chuẩn
    Page<Notification> findAllByCustomerIdIsNull(Pageable pageable);

    // Keyset cho infinite scroll (nhanh, ổn định khi dữ liệu thay đổi)
    Slice<Notification> findAllByCustomerIdIsNullAndIdLessThanOrderByIdDesc(Long lastId, Pageable pageable);

    // Lấy “trang đầu” cho infinite scroll (khi chưa có lastId)
    Slice<Notification> findAllByCustomerIdIsNullOrderByIdDesc(Pageable pageable);
}
