package com.lumiere.app.web.rest;

import com.lumiere.app.repository.CustomerRepository;
import com.lumiere.app.repository.NotificationRepository;
import com.lumiere.app.security.SecurityUtils;
import com.lumiere.app.service.NotificationService;
import com.lumiere.app.service.dto.NotificationDTO;
import com.lumiere.app.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.lumiere.app.domain.Notification}.
 */
@RestController
@RequestMapping("/api/notifications")
public class NotificationResource {

    private static final Logger LOG = LoggerFactory.getLogger(NotificationResource.class);

    private static final String ENTITY_NAME = "notification";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final NotificationService notificationService;

    private final NotificationRepository notificationRepository;

    private final CustomerRepository customerRepository;

    public NotificationResource(
        NotificationService notificationService, 
        NotificationRepository notificationRepository,
        CustomerRepository customerRepository
    ) {
        this.notificationService = notificationService;
        this.notificationRepository = notificationRepository;
        this.customerRepository = customerRepository;
    }

    /**
     * {@code POST  /notifications} : Create a new notification.
     *
     * @param notificationDTO the notificationDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new notificationDTO, or with status {@code 400 (Bad Request)} if the notification has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<NotificationDTO> createNotification(@Valid @RequestBody NotificationDTO notificationDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save Notification : {}", notificationDTO);
        if (notificationDTO.getId() != null) {
            throw new BadRequestAlertException("A new notification cannot already have an ID", ENTITY_NAME, "idexists");
        }
        notificationDTO = notificationService.save(notificationDTO);
        return ResponseEntity.created(new URI("/api/notifications/" + notificationDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, notificationDTO.getId().toString()))
            .body(notificationDTO);
    }

    /**
     * {@code PUT  /notifications/:id} : Updates an existing notification.
     *
     * @param id the id of the notificationDTO to save.
     * @param notificationDTO the notificationDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated notificationDTO,
     * or with status {@code 400 (Bad Request)} if the notificationDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the notificationDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<NotificationDTO> updateNotification(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody NotificationDTO notificationDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Notification : {}, {}", id, notificationDTO);
        if (notificationDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, notificationDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!notificationRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        notificationDTO = notificationService.update(notificationDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, notificationDTO.getId().toString()))
            .body(notificationDTO);
    }

    /**
     * {@code PATCH  /notifications/:id} : Partial updates given fields of an existing notification, field will ignore if it is null
     *
     * @param id the id of the notificationDTO to save.
     * @param notificationDTO the notificationDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated notificationDTO,
     * or with status {@code 400 (Bad Request)} if the notificationDTO is not valid,
     * or with status {@code 404 (Not Found)} if the notificationDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the notificationDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<NotificationDTO> partialUpdateNotification(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody NotificationDTO notificationDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Notification partially : {}, {}", id, notificationDTO);
        if (notificationDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, notificationDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!notificationRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<NotificationDTO> result = notificationService.partialUpdate(notificationDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, notificationDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /notifications} : get all the notifications.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of notifications in body.
     */
    @GetMapping("")
    public List<NotificationDTO> getAllNotifications(
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get all Notifications");
        return notificationService.findAll();
    }

    /**
     * {@code GET  /notifications/:id} : get the "id" notification.
     *
     * @param id the id of the notificationDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the notificationDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<NotificationDTO> getNotification(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Notification : {}", id);
        Optional<NotificationDTO> notificationDTO = notificationService.findOne(id);
        return ResponseUtil.wrapOrNotFound(notificationDTO);
    }

    /**
     * {@code DELETE  /notifications/:id} : delete the "id" notification.
     *
     * @param id the id of the notificationDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotification(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Notification : {}", id);
        notificationService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /** GET /admin/notifications : phân trang chuẩn (page, size, sort) */
    @GetMapping("/admin/notifications")
    public ResponseEntity<List<NotificationDTO>> getAdminNotifications(Pageable pageable) {
        Page<NotificationDTO> page = notificationService.getAdminNotifications(pageable);
        // JHipster helper để nhúng Link header (first, prev, next, last)
        HttpHeaders headers = PaginationUtil
            .generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);

        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * GET /admin/notifications/scroll : infinite scroll (keyset)
     * @param lastId id cuối cùng của trang trước (null nếu là trang đầu)
     * @param size số bản ghi lấy mỗi lần (mặc định 20)
     *
     * Response gói thêm hasNext và nextCursor để client gọi tiếp
     */
    @GetMapping("/admin/notifications/scroll")
    public ResponseEntity<Map<String, Object>> scrollAdminNotifications(
        @RequestParam(required = false) Long lastId,
        @RequestParam(defaultValue = "20") int size
    ) {
        Slice<NotificationDTO> slice = notificationService.scrollAdminNotifications(lastId, size);

        Long nextCursor = null;
        if (!slice.isEmpty()) {
            NotificationDTO last = slice.getContent().get(slice.getNumberOfElements() - 1);
            nextCursor = last.getId();
        }

        Map<String, Object> payload = new HashMap<>();
        payload.put("items", slice.getContent());
        payload.put("hasNext", slice.hasNext());
        payload.put("nextCursor", slice.hasNext() ? nextCursor : null);

        return ResponseEntity.ok(payload);
    }

    /**
     * GET /notifications/customer : Lấy notifications của customer (phân trang)
     */
    @GetMapping("/customer")
    public ResponseEntity<List<NotificationDTO>> getCustomerNotifications(Pageable pageable) {
        // Lấy customerId từ SecurityContext
        Long customerId = getCurrentCustomerId();
        if (customerId == null) {
            return ResponseEntity.badRequest().build();
        }

        Page<NotificationDTO> page = notificationService.getCustomerNotifications(customerId, pageable);
        HttpHeaders headers = PaginationUtil
            .generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);

        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * GET /notifications/customer/scroll : Infinite scroll cho customer notifications
     */
    @GetMapping("/customer/scroll")
    public ResponseEntity<Map<String, Object>> scrollCustomerNotifications(
        @RequestParam(required = false) Long lastId,
        @RequestParam(defaultValue = "20") int size
    ) {
        Long customerId = getCurrentCustomerId();
        if (customerId == null) {
            return ResponseEntity.badRequest().build();
        }

        Slice<NotificationDTO> slice = notificationService.scrollCustomerNotifications(customerId, lastId, size);

        Long nextCursor = null;
        if (!slice.isEmpty()) {
            NotificationDTO last = slice.getContent().get(slice.getNumberOfElements() - 1);
            nextCursor = last.getId();
        }

        Map<String, Object> payload = new HashMap<>();
        payload.put("items", slice.getContent());
        payload.put("hasNext", slice.hasNext());
        payload.put("nextCursor", slice.hasNext() ? nextCursor : null);

        return ResponseEntity.ok(payload);
    }

    /**
     * GET /notifications/unread-count : Lấy số lượng notifications chưa đọc
     */
    @GetMapping("/unread-count")
    public ResponseEntity<Map<String, Long>> getUnreadCount() {
        Long customerId = getCurrentCustomerId();
        if (customerId == null) {
            return ResponseEntity.badRequest().build();
        }

        long count = notificationService.getUnreadCount(customerId);
        Map<String, Long> response = new HashMap<>();
        response.put("count", count);

        return ResponseEntity.ok(response);
    }

    /**
     * PUT /notifications/{id}/read : Đánh dấu notification là đã đọc
     */
    @PutMapping("/{id}/read")
    public ResponseEntity<NotificationDTO> markAsRead(@PathVariable Long id) {
        NotificationDTO notificationDTO = notificationService.markAsRead(id);
        return ResponseEntity.ok(notificationDTO);
    }

    /**
     * Lấy customerId từ SecurityContext
     */
    private Long getCurrentCustomerId() {
        try {
            return SecurityUtils.getCurrentUserId()
                .flatMap(userId -> customerRepository.findByUserId(userId))
                .map(customer -> customer.getId())
                .orElse(null);
        } catch (Exception e) {
            LOG.error("Error getting current customer ID", e);
            return null;
        }
    }
}
