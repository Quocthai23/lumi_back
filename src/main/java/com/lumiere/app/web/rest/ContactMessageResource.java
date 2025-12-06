package com.lumiere.app.web.rest;

import com.lumiere.app.domain.enumeration.ContactStatus;
import com.lumiere.app.repository.ContactMessageRepository;
import com.lumiere.app.service.ContactMessageService;
import com.lumiere.app.service.dto.ContactMessageDTO;
import com.lumiere.app.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.lumiere.app.domain.ContactMessage}.
 */
@RestController
@RequestMapping("/api/contact-messages")
public class ContactMessageResource {

    private static final Logger LOG = LoggerFactory.getLogger(ContactMessageResource.class);

    private static final String ENTITY_NAME = "contactMessage";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ContactMessageService contactMessageService;

    private final ContactMessageRepository contactMessageRepository;

    public ContactMessageResource(
        ContactMessageService contactMessageService,
        ContactMessageRepository contactMessageRepository
    ) {
        this.contactMessageService = contactMessageService;
        this.contactMessageRepository = contactMessageRepository;
    }

    /**
     * {@code POST  /contact-messages} : Create a new contactMessage.
     *
     * @param contactMessageDTO the contactMessageDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new contactMessageDTO, or with status {@code 400 (Bad Request)} if the contactMessage has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<ContactMessageDTO> createContactMessage(@Valid @RequestBody ContactMessageDTO contactMessageDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save ContactMessage : {}", contactMessageDTO);
        if (contactMessageDTO.getId() != null) {
            throw new BadRequestAlertException("A new contactMessage cannot already have an ID", ENTITY_NAME, "idexists");
        }
        contactMessageDTO = contactMessageService.save(contactMessageDTO);
        return ResponseEntity.created(new URI("/api/contact-messages/" + contactMessageDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, contactMessageDTO.getId().toString()))
            .body(contactMessageDTO);
    }

    /**
     * {@code POST  /contact-messages/submit} : Submit a contact message from public form.
     *
     * @param contactMessageDTO the contactMessageDTO to submit.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new contactMessageDTO.
     */
    @PostMapping("/submit")
    public ResponseEntity<ContactMessageDTO> submitContactMessage(@Valid @RequestBody ContactMessageDTO contactMessageDTO) {
        LOG.debug("REST request to submit ContactMessage : {}", contactMessageDTO);
        if (contactMessageDTO.getId() != null) {
            throw new BadRequestAlertException("A new contactMessage cannot already have an ID", ENTITY_NAME, "idexists");
        }
        contactMessageDTO = contactMessageService.submitContactMessage(contactMessageDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, contactMessageDTO.getId().toString()))
            .body(contactMessageDTO);
    }

    /**
     * {@code PUT  /contact-messages/:id} : Updates an existing contactMessage.
     *
     * @param id the id of the contactMessageDTO to save.
     * @param contactMessageDTO the contactMessageDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated contactMessageDTO,
     * or with status {@code 400 (Bad Request)} if the contactMessageDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the contactMessageDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<ContactMessageDTO> updateContactMessage(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ContactMessageDTO contactMessageDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update ContactMessage : {}, {}", id, contactMessageDTO);
        if (contactMessageDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, contactMessageDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!contactMessageRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        contactMessageDTO = contactMessageService.update(contactMessageDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, contactMessageDTO.getId().toString()))
            .body(contactMessageDTO);
    }

    /**
     * {@code PATCH  /contact-messages/:id} : Partial updates given fields of an existing contactMessage, field will ignore if it is null
     *
     * @param id the id of the contactMessageDTO to save.
     * @param contactMessageDTO the contactMessageDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated contactMessageDTO,
     * or with status {@code 400 (Bad Request)} if the contactMessageDTO is not valid,
     * or with status {@code 404 (Not Found)} if the contactMessageDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the contactMessageDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<ContactMessageDTO> partialUpdateContactMessage(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ContactMessageDTO contactMessageDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update ContactMessage partially : {}, {}", id, contactMessageDTO);
        if (contactMessageDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, contactMessageDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!contactMessageRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ContactMessageDTO> result = contactMessageService.partialUpdate(contactMessageDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, contactMessageDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /contact-messages} : get all the contactMessages.
     *
     * @param pageable the pagination information.
     * @param status optional status filter.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of contactMessages in body.
     */
    @GetMapping("")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<List<ContactMessageDTO>> getAllContactMessages(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        @RequestParam(required = false) ContactStatus status
    ) {
        LOG.debug("REST request to get a page of ContactMessages");
        Page<ContactMessageDTO> page;
        if (status != null) {
            page = contactMessageService.findByStatus(status, pageable);
        } else {
            page = contactMessageService.findAll(pageable);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /contact-messages/:id} : get the "id" contactMessage.
     *
     * @param id the id of the contactMessageDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the contactMessageDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<ContactMessageDTO> getContactMessage(@PathVariable("id") Long id) {
        LOG.debug("REST request to get ContactMessage : {}", id);
        Optional<ContactMessageDTO> contactMessageDTO = contactMessageService.findOne(id);
        return ResponseUtil.wrapOrNotFound(contactMessageDTO);
    }

    /**
     * {@code DELETE  /contact-messages/:id} : delete the "id" contactMessage.
     *
     * @param id the id of the contactMessageDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteContactMessage(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete ContactMessage : {}", id);
        contactMessageService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code PUT  /contact-messages/:id/mark-read} : Mark a contact message as read.
     *
     * @param id the id of the contactMessage to mark as read.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated contactMessageDTO.
     */
    @PutMapping("/{id}/mark-read")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<ContactMessageDTO> markAsRead(@PathVariable Long id) {
        LOG.debug("REST request to mark ContactMessage as read: {}", id);
        ContactMessageDTO contactMessageDTO = contactMessageService.markAsRead(id);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .body(contactMessageDTO);
    }

    /**
     * {@code PUT  /contact-messages/:id/mark-replied} : Mark a contact message as replied.
     *
     * @param id the id of the contactMessage to mark as replied.
     * @param request the request containing admin note.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated contactMessageDTO.
     */
    @PutMapping("/{id}/mark-replied")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<ContactMessageDTO> markAsReplied(
        @PathVariable Long id,
        @RequestBody(required = false) MarkRepliedRequest request
    ) {
        LOG.debug("REST request to mark ContactMessage as replied: {}", id);
        String adminNote = request != null ? request.getAdminNote() : null;
        ContactMessageDTO contactMessageDTO = contactMessageService.markAsReplied(id, adminNote);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .body(contactMessageDTO);
    }

    /**
     * Request DTO for marking as replied.
     */
    public static class MarkRepliedRequest {
        private String adminNote;

        public String getAdminNote() {
            return adminNote;
        }

        public void setAdminNote(String adminNote) {
            this.adminNote = adminNote;
        }
    }
}

