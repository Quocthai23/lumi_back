package com.lumiere.app.repository;

import com.lumiere.app.domain.Address;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Address entity.
 */
@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
    default Optional<Address> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Address> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Address> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select address from Address address left join fetch address.customer",
        countQuery = "select count(address) from Address address"
    )
    Page<Address> findAllWithToOneRelationships(Pageable pageable);

    @Query("select address from Address address left join fetch address.customer")
    List<Address> findAllWithToOneRelationships();

    @Query("select address from Address address left join fetch address.customer where address.id =:id")
    Optional<Address> findOneWithToOneRelationships(@Param("id") Long id);
}
