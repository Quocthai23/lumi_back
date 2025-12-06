package com.lumiere.app.service;

import com.lumiere.app.domain.enumeration.ContactStatus;
import com.lumiere.app.service.dto.ContactMessageDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.lumiere.app.domain.ContactMessage}.
 */
public interface ContactMessageService {
    /**
     * Save a contactMessage.
     *
     * @param contactMessageDTO the entity to save.
     * @return the persisted entity.
     */
    ContactMessageDTO save(ContactMessageDTO contactMessageDTO);

    /**
     * Updates a contactMessage.
     *
     * @param contactMessageDTO the entity to update.
     * @return the persisted entity.
     */
    ContactMessageDTO update(ContactMessageDTO contactMessageDTO);

    /**
     * Partially updates a contactMessage.
     *
     * @param contactMessageDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<ContactMessageDTO> partialUpdate(ContactMessageDTO contactMessageDTO);

    /**
     * Get all the contactMessages.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ContactMessageDTO> findAll(Pageable pageable);

    /**
     * Get all contactMessages by status.
     *
     * @param status the status to filter by.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ContactMessageDTO> findByStatus(ContactStatus status, Pageable pageable);

    /**
     * Get the "id" contactMessage.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<ContactMessageDTO> findOne(Long id);

    /**
     * Delete the "id" contactMessage.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Submit a contact message from public form.
     *
     * @param contactMessageDTO the contact message to submit.
     * @return the persisted entity.
     */
    ContactMessageDTO submitContactMessage(ContactMessageDTO contactMessageDTO);

    /**
     * Mark a contact message as read.
     *
     * @param id the id of the contact message.
     * @return the updated entity.
     */
    ContactMessageDTO markAsRead(Long id);

    /**
     * Mark a contact message as replied.
     *
     * @param id the id of the contact message.
     * @param adminNote optional admin note.
     * @return the updated entity.
     */
    ContactMessageDTO markAsReplied(Long id, String adminNote);
}

