package com.lumiere.app.web.rest;

import com.lumiere.app.repository.OrdersRepository;
import com.lumiere.app.service.OrdersQueryService;
import com.lumiere.app.service.OrdersService;
import com.lumiere.app.service.criteria.OrdersCriteria;
import com.lumiere.app.service.dto.OrdersDTO;
import com.lumiere.app.web.rest.errors.BadRequestAlertException;
import jakarta.servlet.http.HttpServletResponse;
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
 * REST controller for managing {@link com.lumiere.app.domain.Orders}.
 */
@RestController
@RequestMapping("/api/orders")
public class OrdersResource {

    private static final Logger LOG = LoggerFactory.getLogger(OrdersResource.class);

    private static final String ENTITY_NAME = "orders";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final OrdersService ordersService;

    private final OrdersRepository ordersRepository;

    private final OrdersQueryService ordersQueryService;

    public OrdersResource(OrdersService ordersService, OrdersRepository ordersRepository, OrdersQueryService ordersQueryService) {
        this.ordersService = ordersService;
        this.ordersRepository = ordersRepository;
        this.ordersQueryService = ordersQueryService;
    }

    /**
     * {@code POST  /orders} : Create a new orders.
     *
     * @param ordersDTO the ordersDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new ordersDTO, or with status {@code 400 (Bad Request)} if the orders has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<OrdersDTO> createOrders(@Valid @RequestBody OrdersDTO ordersDTO) throws URISyntaxException {
        LOG.debug("REST request to save Orders : {}", ordersDTO);
        if (ordersDTO.getId() != null) {
            throw new BadRequestAlertException("A new orders cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ordersDTO = ordersService.save(ordersDTO);
        return ResponseEntity.created(new URI("/api/orders/" + ordersDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, ordersDTO.getId().toString()))
            .body(ordersDTO);
    }

    /**
     * {@code PUT  /orders/:id} : Updates an existing orders.
     *
     * @param id the id of the ordersDTO to save.
     * @param ordersDTO the ordersDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated ordersDTO,
     * or with status {@code 400 (Bad Request)} if the ordersDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the ordersDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<OrdersDTO> updateOrders(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody OrdersDTO ordersDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Orders : {}, {}", id, ordersDTO);
        if (ordersDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, ordersDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!ordersRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        ordersDTO = ordersService.update(ordersDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, ordersDTO.getId().toString()))
            .body(ordersDTO);
    }

    /**
     * {@code PATCH  /orders/:id} : Partial updates given fields of an existing orders, field will ignore if it is null
     *
     * @param id the id of the ordersDTO to save.
     * @param ordersDTO the ordersDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated ordersDTO,
     * or with status {@code 400 (Bad Request)} if the ordersDTO is not valid,
     * or with status {@code 404 (Not Found)} if the ordersDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the ordersDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<OrdersDTO> partialUpdateOrders(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody OrdersDTO ordersDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Orders partially : {}, {}", id, ordersDTO);
        if (ordersDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, ordersDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!ordersRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<OrdersDTO> result = ordersService.partialUpdate(ordersDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, ordersDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /orders} : get all the orders.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of orders in body.
     */
    @GetMapping("")
    public ResponseEntity<List<OrdersDTO>> getAllOrders(
        OrdersCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get Orders by criteria: {}", criteria);

        Page<OrdersDTO> page = ordersQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /orders/count} : count all the orders.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countOrders(OrdersCriteria criteria) {
        LOG.debug("REST request to count Orders by criteria: {}", criteria);
        return ResponseEntity.ok().body(ordersQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /orders/:id} : get the "id" orders.
     *
     * @param id the id of the ordersDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the ordersDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<OrdersDTO> getOrders(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Orders : {}", id);
        Optional<OrdersDTO> ordersDTO = ordersService.findOne(id);
        return ResponseUtil.wrapOrNotFound(ordersDTO);
    }

    /**
     * {@code DELETE  /orders/:id} : delete the "id" orders.
     *
     * @param id the id of the ordersDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrders(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Orders : {}", id);
        ordersService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * Xuất Excel hóa đơn của đơn hàng.
     * Ví dụ: GET /api/orders/123/invoice.xlsx
     */
    @GetMapping(
        value = "/{orderId}/invoice",
        produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
    )
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')") // chỉnh theo hệ thống phân quyền của bạn
    public void exportInvoiceXlsx(
        @PathVariable Long orderId,
        HttpServletResponse response
    ) {
        ordersService.writeOrderInvoiceExcel(orderId, response);
    }
}
