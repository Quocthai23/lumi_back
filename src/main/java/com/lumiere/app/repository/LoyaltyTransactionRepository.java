package com.lumiere.app.repository;

import com.lumiere.app.domain.LoyaltyTransaction;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the LoyaltyTransaction entity.
 */
@Repository
public interface LoyaltyTransactionRepository extends JpaRepository<LoyaltyTransaction, Long> {
    default Optional<LoyaltyTransaction> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<LoyaltyTransaction> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<LoyaltyTransaction> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select loyaltyTransaction from LoyaltyTransaction loyaltyTransaction left join fetch loyaltyTransaction.customer",
        countQuery = "select count(loyaltyTransaction) from LoyaltyTransaction loyaltyTransaction"
    )
    Page<LoyaltyTransaction> findAllWithToOneRelationships(Pageable pageable);

    @Query("select loyaltyTransaction from LoyaltyTransaction loyaltyTransaction left join fetch loyaltyTransaction.customer")
    List<LoyaltyTransaction> findAllWithToOneRelationships();

    @Query(
        "select loyaltyTransaction from LoyaltyTransaction loyaltyTransaction left join fetch loyaltyTransaction.customer where loyaltyTransaction.id =:id"
    )
    Optional<LoyaltyTransaction> findOneWithToOneRelationships(@Param("id") Long id);
}
