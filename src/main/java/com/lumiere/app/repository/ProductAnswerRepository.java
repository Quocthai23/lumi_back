package com.lumiere.app.repository;

import com.lumiere.app.domain.ProductAnswer;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ProductAnswer entity.
 */
@Repository
public interface ProductAnswerRepository extends JpaRepository<ProductAnswer, Long> {
    default Optional<ProductAnswer> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<ProductAnswer> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<ProductAnswer> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select productAnswer from ProductAnswer productAnswer left join fetch productAnswer.question",
        countQuery = "select count(productAnswer) from ProductAnswer productAnswer"
    )
    Page<ProductAnswer> findAllWithToOneRelationships(Pageable pageable);

    @Query("select productAnswer from ProductAnswer productAnswer left join fetch productAnswer.question")
    List<ProductAnswer> findAllWithToOneRelationships();

    @Query("select productAnswer from ProductAnswer productAnswer left join fetch productAnswer.question where productAnswer.id =:id")
    Optional<ProductAnswer> findOneWithToOneRelationships(@Param("id") Long id);
}
