package com.lumiere.app.repository;

import com.lumiere.app.domain.Inventory;
import java.util.List;
import java.util.Optional;

import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Spring Data JPA repository for the Inventory entity.
 */
@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long>, JpaSpecificationExecutor<Inventory> {
    default Optional<Inventory> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Inventory> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Inventory> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select distinct inventory " +
            "from Inventory inventory " +
            "left join fetch inventory.productVariant " +
            "left join fetch inventory.warehouse",
        countQuery = "select count(distinct inventory) from Inventory inventory"
    )
    Page<Inventory> findAllWithToOneRelationships(Pageable pageable);

    @Query("select inventory from Inventory inventory left join fetch inventory.productVariant left join fetch inventory.warehouse")
    List<Inventory> findAllWithToOneRelationships();

    @Query(
        "select inventory from Inventory inventory left join fetch inventory.productVariant left join fetch inventory.warehouse where inventory.id =:id"
    )
    Optional<Inventory> findOneWithToOneRelationships(@Param("id") Long id);

    List<Inventory> findAllByProductVariant_IdIn(List<Long> productVariantIds);

    @Query("select i from Inventory i where i.id in :ids")
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Inventory> findAllByIdForUpdate(@Param("ids") List<Long> ids);

    /**
     * Tìm inventory theo productVariant với pessimistic lock để đảm bảo atomic operations.
     * Lấy tất cả inventory của productVariant từ các warehouse active.
     *
     * @param productVariantId ID của product variant
     * @return danh sách inventory
     */
    @Query(
        "select i from Inventory i " +
        "left join fetch i.warehouse w " +
        "where i.productVariant.id = :productVariantId " +
        "and (w.isActive = true or w.isActive is null) " +
        "order by i.stockQuantity desc"
    )
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Inventory> findByProductVariantIdForUpdate(@Param("productVariantId") Long productVariantId);

    /**
     * Trừ stock quantity một cách atomic.
     * Chỉ trừ khi stockQuantity >= quantity.
     *
     * @param inventoryId ID của inventory
     * @param quantity số lượng cần trừ
     * @return số dòng được cập nhật (1 nếu thành công, 0 nếu không đủ hàng)
     */
    @Modifying
    @Transactional
    @Query(
        "update Inventory i set i.stockQuantity = i.stockQuantity - :quantity " +
        "where i.id = :inventoryId and i.stockQuantity >= :quantity"
    )
    int deductStockQuantity(@Param("inventoryId") Long inventoryId, @Param("quantity") Long quantity);
}
