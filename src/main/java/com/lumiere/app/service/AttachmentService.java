package com.lumiere.app.service;

import com.google.cloud.storage.Blob;
import com.lumiere.app.service.dto.AttachmentDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 */
public interface AttachmentService {
    /**
     * Save a attachment.
     *
     * @param attachmentDTO the entity to save.
     * @return the persisted entity.
     */
    AttachmentDTO save(AttachmentDTO attachmentDTO);

    /**
     * Updates a attachment.
     *
     * @param attachmentDTO the entity to update.
     * @return the persisted entity.
     */
    AttachmentDTO update(AttachmentDTO attachmentDTO);

    /**
     * Partially updates a attachment.
     *
     * @param attachmentDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<AttachmentDTO> partialUpdate(AttachmentDTO attachmentDTO);

    /**
     * Get all the attachments.
     *
     * @return the list of entities.
     */
    List<AttachmentDTO> findAll();

    /**
     * Get the "id" attachment.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<AttachmentDTO> findOne(Long id);

    /**
     * Delete the "id" attachment.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    Blob uploadAttachment(MultipartFile file) throws IOException;
}
