package com.lumiere.app.repository;

import com.lumiere.app.domain.ProductQuestion;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ProductQuestion entity.
 */
@Repository
public interface ProductQuestionRepository extends JpaRepository<ProductQuestion, Long> {
    default Optional<ProductQuestion> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<ProductQuestion> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<ProductQuestion> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select productQuestion from ProductQuestion productQuestion left join fetch productQuestion.product",
        countQuery = "select count(productQuestion) from ProductQuestion productQuestion"
    )
    Page<ProductQuestion> findAllWithToOneRelationships(Pageable pageable);

    @Query("select productQuestion from ProductQuestion productQuestion left join fetch productQuestion.product")
    List<ProductQuestion> findAllWithToOneRelationships();

    @Query(
        "select productQuestion from ProductQuestion productQuestion left join fetch productQuestion.product where productQuestion.id =:id"
    )
    Optional<ProductQuestion> findOneWithToOneRelationships(@Param("id") Long id);
}
