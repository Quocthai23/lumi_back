package com.lumiere.app.repository;

import com.lumiere.app.domain.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for the Attachment entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AttachmentRepository extends JpaRepository<Attachment, Long> {
    @Query("select a.id from Attachment a where a.url = :url")
    Optional<Long> findIdByUrl(@Param("url") String url);

    @Query("select a from Attachment a where a.id in :ids")
    List<Attachment> findAllByIds(@Param("ids") Collection<Long> ids);
}
