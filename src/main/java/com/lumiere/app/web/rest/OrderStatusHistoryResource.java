package com.lumiere.app.web.rest;

import com.lumiere.app.repository.OrderStatusHistoryRepository;
import com.lumiere.app.service.OrderStatusHistoryService;
import com.lumiere.app.service.dto.OrderStatusHistoryDTO;
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
 * REST controller for managing {@link com.lumiere.app.domain.OrderStatusHistory}.
 */
@RestController
@RequestMapping("/api/order-status-histories")
public class OrderStatusHistoryResource {

    private static final Logger LOG = LoggerFactory.getLogger(OrderStatusHistoryResource.class);

    private static final String ENTITY_NAME = "orderStatusHistory";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final OrderStatusHistoryService orderStatusHistoryService;

    private final OrderStatusHistoryRepository orderStatusHistoryRepository;

    public OrderStatusHistoryResource(
        OrderStatusHistoryService orderStatusHistoryService,
        OrderStatusHistoryRepository orderStatusHistoryRepository
    ) {
        this.orderStatusHistoryService = orderStatusHistoryService;
        this.orderStatusHistoryRepository = orderStatusHistoryRepository;
    }

    /**
     * {@code POST  /order-status-histories} : Create a new orderStatusHistory.
     *
     * @param orderStatusHistoryDTO the orderStatusHistoryDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new orderStatusHistoryDTO, or with status {@code 400 (Bad Request)} if the orderStatusHistory has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<OrderStatusHistoryDTO> createOrderStatusHistory(@Valid @RequestBody OrderStatusHistoryDTO orderStatusHistoryDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save OrderStatusHistory : {}", orderStatusHistoryDTO);
        if (orderStatusHistoryDTO.getId() != null) {
            throw new BadRequestAlertException("A new orderStatusHistory cannot already have an ID", ENTITY_NAME, "idexists");
        }
        orderStatusHistoryDTO = orderStatusHistoryService.save(orderStatusHistoryDTO);
        return ResponseEntity.created(new URI("/api/order-status-histories/" + orderStatusHistoryDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, orderStatusHistoryDTO.getId().toString()))
            .body(orderStatusHistoryDTO);
    }

    /**
     * {@code PUT  /order-status-histories/:id} : Updates an existing orderStatusHistory.
     *
     * @param id the id of the orderStatusHistoryDTO to save.
     * @param orderStatusHistoryDTO the orderStatusHistoryDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated orderStatusHistoryDTO,
     * or with status {@code 400 (Bad Request)} if the orderStatusHistoryDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the orderStatusHistoryDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<OrderStatusHistoryDTO> updateOrderStatusHistory(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody OrderStatusHistoryDTO orderStatusHistoryDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update OrderStatusHistory : {}, {}", id, orderStatusHistoryDTO);
        if (orderStatusHistoryDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, orderStatusHistoryDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!orderStatusHistoryRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        orderStatusHistoryDTO = orderStatusHistoryService.update(orderStatusHistoryDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, orderStatusHistoryDTO.getId().toString()))
            .body(orderStatusHistoryDTO);
    }

    /**
     * {@code PATCH  /order-status-histories/:id} : Partial updates given fields of an existing orderStatusHistory, field will ignore if it is null
     *
     * @param id the id of the orderStatusHistoryDTO to save.
     * @param orderStatusHistoryDTO the orderStatusHistoryDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated orderStatusHistoryDTO,
     * or with status {@code 400 (Bad Request)} if the orderStatusHistoryDTO is not valid,
     * or with status {@code 404 (Not Found)} if the orderStatusHistoryDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the orderStatusHistoryDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<OrderStatusHistoryDTO> partialUpdateOrderStatusHistory(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody OrderStatusHistoryDTO orderStatusHistoryDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update OrderStatusHistory partially : {}, {}", id, orderStatusHistoryDTO);
        if (orderStatusHistoryDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, orderStatusHistoryDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!orderStatusHistoryRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<OrderStatusHistoryDTO> result = orderStatusHistoryService.partialUpdate(orderStatusHistoryDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, orderStatusHistoryDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /order-status-histories} : get all the orderStatusHistories.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of orderStatusHistories in body.
     */
    @GetMapping("")
    public List<OrderStatusHistoryDTO> getAllOrderStatusHistories(
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get all OrderStatusHistories");
        return orderStatusHistoryService.findAll();
    }

    /**
     * {@code GET  /order-status-histories/:id} : get the "id" orderStatusHistory.
     *
     * @param id the id of the orderStatusHistoryDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the orderStatusHistoryDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<OrderStatusHistoryDTO> getOrderStatusHistory(@PathVariable("id") Long id) {
        LOG.debug("REST request to get OrderStatusHistory : {}", id);
        Optional<OrderStatusHistoryDTO> orderStatusHistoryDTO = orderStatusHistoryService.findOne(id);
        return ResponseUtil.wrapOrNotFound(orderStatusHistoryDTO);
    }

    /**
     * {@code DELETE  /order-status-histories/:id} : delete the "id" orderStatusHistory.
     *
     * @param id the id of the orderStatusHistoryDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrderStatusHistory(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete OrderStatusHistory : {}", id);
        orderStatusHistoryService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
