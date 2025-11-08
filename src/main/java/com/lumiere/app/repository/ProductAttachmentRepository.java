package com.lumiere.app.repository;

import com.lumiere.app.domain.ProductAttachment;
import com.lumiere.app.domain.ProductAttachmentId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@Repository
public interface ProductAttachmentRepository extends JpaRepository<ProductAttachment, ProductAttachmentId> {

    @Query("select pa from ProductAttachment pa where pa.id.productId = :productId")
    List<ProductAttachment> findAllByProductId(@Param("productId") Long productId);

    @Modifying
    @Query("delete from ProductAttachment pa where pa.id.productId = :productId and pa.id.attachmentId in :attachmentIds")
    void deleteByProductIdAndAttachmentIdIn(@Param("productId") Long productId, @Param("attachmentIds") Collection<Long> attachmentIds);

    @Modifying
    @Query("delete from ProductAttachment pa where pa.id.productId = :productId")
    void deleteByProductId(@Param("productId") Long productId);

    @Modifying
    @Query("DELETE FROM ProductAttachment pa WHERE pa.id.productId = :productId AND pa.id.attachmentId IN :attachmentIds")
    void deleteByProductIdAndAttachmentIds(@Param("productId") Long productId, @Param("attachmentIds") Set<Long> attachmentIds);

}
