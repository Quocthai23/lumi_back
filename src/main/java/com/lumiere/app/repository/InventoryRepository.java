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
}
