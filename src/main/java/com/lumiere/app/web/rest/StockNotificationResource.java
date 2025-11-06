package com.lumiere.app.web.rest;

import com.lumiere.app.repository.StockNotificationRepository;
import com.lumiere.app.service.StockNotificationService;
import com.lumiere.app.service.dto.StockNotificationDTO;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.lumiere.app.domain.StockNotification}.
 */
@RestController
@RequestMapping("/api/stock-notifications")
public class StockNotificationResource {

    private static final Logger LOG = LoggerFactory.getLogger(StockNotificationResource.class);

    private static final String ENTITY_NAME = "stockNotification";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final StockNotificationService stockNotificationService;

    private final StockNotificationRepository stockNotificationRepository;

    public StockNotificationResource(
        StockNotificationService stockNotificationService,
        StockNotificationRepository stockNotificationRepository
    ) {
        this.stockNotificationService = stockNotificationService;
        this.stockNotificationRepository = stockNotificationRepository;
    }

    /**
     * {@code POST  /stock-notifications} : Create a new stockNotification.
     *
     * @param stockNotificationDTO the stockNotificationDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new stockNotificationDTO, or with status {@code 400 (Bad Request)} if the stockNotification has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<StockNotificationDTO> createStockNotification(@Valid @RequestBody StockNotificationDTO stockNotificationDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save StockNotification : {}", stockNotificationDTO);
        if (stockNotificationDTO.getId() != null) {
            throw new BadRequestAlertException("A new stockNotification cannot already have an ID", ENTITY_NAME, "idexists");
        }
        stockNotificationDTO = stockNotificationService.save(stockNotificationDTO);
        return ResponseEntity.created(new URI("/api/stock-notifications/" + stockNotificationDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, stockNotificationDTO.getId().toString()))
            .body(stockNotificationDTO);
    }

    /**
     * {@code PUT  /stock-notifications/:id} : Updates an existing stockNotification.
     *
     * @param id the id of the stockNotificationDTO to save.
     * @param stockNotificationDTO the stockNotificationDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated stockNotificationDTO,
     * or with status {@code 400 (Bad Request)} if the stockNotificationDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the stockNotificationDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<StockNotificationDTO> updateStockNotification(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody StockNotificationDTO stockNotificationDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update StockNotification : {}, {}", id, stockNotificationDTO);
        if (stockNotificationDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, stockNotificationDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!stockNotificationRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        stockNotificationDTO = stockNotificationService.update(stockNotificationDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, stockNotificationDTO.getId().toString()))
            .body(stockNotificationDTO);
    }

    /**
     * {@code PATCH  /stock-notifications/:id} : Partial updates given fields of an existing stockNotification, field will ignore if it is null
     *
     * @param id the id of the stockNotificationDTO to save.
     * @param stockNotificationDTO the stockNotificationDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated stockNotificationDTO,
     * or with status {@code 400 (Bad Request)} if the stockNotificationDTO is not valid,
     * or with status {@code 404 (Not Found)} if the stockNotificationDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the stockNotificationDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<StockNotificationDTO> partialUpdateStockNotification(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody StockNotificationDTO stockNotificationDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update StockNotification partially : {}, {}", id, stockNotificationDTO);
        if (stockNotificationDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, stockNotificationDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!stockNotificationRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<StockNotificationDTO> result = stockNotificationService.partialUpdate(stockNotificationDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, stockNotificationDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /stock-notifications} : get all the stockNotifications.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of stockNotifications in body.
     */
    @GetMapping("")
    public List<StockNotificationDTO> getAllStockNotifications(
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get all StockNotifications");
        return stockNotificationService.findAll();
    }

    /**
     * {@code GET  /stock-notifications/:id} : get the "id" stockNotification.
     *
     * @param id the id of the stockNotificationDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the stockNotificationDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<StockNotificationDTO> getStockNotification(@PathVariable("id") Long id) {
        LOG.debug("REST request to get StockNotification : {}", id);
        Optional<StockNotificationDTO> stockNotificationDTO = stockNotificationService.findOne(id);
        return ResponseUtil.wrapOrNotFound(stockNotificationDTO);
    }

    /**
     * {@code DELETE  /stock-notifications/:id} : delete the "id" stockNotification.
     *
     * @param id the id of the stockNotificationDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStockNotification(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete StockNotification : {}", id);
        stockNotificationService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
