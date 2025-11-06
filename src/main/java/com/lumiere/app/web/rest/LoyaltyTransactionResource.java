package com.lumiere.app.web.rest;

import com.lumiere.app.repository.LoyaltyTransactionRepository;
import com.lumiere.app.service.LoyaltyTransactionService;
import com.lumiere.app.service.dto.LoyaltyTransactionDTO;
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
 * REST controller for managing {@link com.lumiere.app.domain.LoyaltyTransaction}.
 */
@RestController
@RequestMapping("/api/loyalty-transactions")
public class LoyaltyTransactionResource {

    private static final Logger LOG = LoggerFactory.getLogger(LoyaltyTransactionResource.class);

    private static final String ENTITY_NAME = "loyaltyTransaction";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final LoyaltyTransactionService loyaltyTransactionService;

    private final LoyaltyTransactionRepository loyaltyTransactionRepository;

    public LoyaltyTransactionResource(
        LoyaltyTransactionService loyaltyTransactionService,
        LoyaltyTransactionRepository loyaltyTransactionRepository
    ) {
        this.loyaltyTransactionService = loyaltyTransactionService;
        this.loyaltyTransactionRepository = loyaltyTransactionRepository;
    }

    /**
     * {@code POST  /loyalty-transactions} : Create a new loyaltyTransaction.
     *
     * @param loyaltyTransactionDTO the loyaltyTransactionDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new loyaltyTransactionDTO, or with status {@code 400 (Bad Request)} if the loyaltyTransaction has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<LoyaltyTransactionDTO> createLoyaltyTransaction(@Valid @RequestBody LoyaltyTransactionDTO loyaltyTransactionDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save LoyaltyTransaction : {}", loyaltyTransactionDTO);
        if (loyaltyTransactionDTO.getId() != null) {
            throw new BadRequestAlertException("A new loyaltyTransaction cannot already have an ID", ENTITY_NAME, "idexists");
        }
        loyaltyTransactionDTO = loyaltyTransactionService.save(loyaltyTransactionDTO);
        return ResponseEntity.created(new URI("/api/loyalty-transactions/" + loyaltyTransactionDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, loyaltyTransactionDTO.getId().toString()))
            .body(loyaltyTransactionDTO);
    }

    /**
     * {@code PUT  /loyalty-transactions/:id} : Updates an existing loyaltyTransaction.
     *
     * @param id the id of the loyaltyTransactionDTO to save.
     * @param loyaltyTransactionDTO the loyaltyTransactionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated loyaltyTransactionDTO,
     * or with status {@code 400 (Bad Request)} if the loyaltyTransactionDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the loyaltyTransactionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<LoyaltyTransactionDTO> updateLoyaltyTransaction(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody LoyaltyTransactionDTO loyaltyTransactionDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update LoyaltyTransaction : {}, {}", id, loyaltyTransactionDTO);
        if (loyaltyTransactionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, loyaltyTransactionDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!loyaltyTransactionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        loyaltyTransactionDTO = loyaltyTransactionService.update(loyaltyTransactionDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, loyaltyTransactionDTO.getId().toString()))
            .body(loyaltyTransactionDTO);
    }

    /**
     * {@code PATCH  /loyalty-transactions/:id} : Partial updates given fields of an existing loyaltyTransaction, field will ignore if it is null
     *
     * @param id the id of the loyaltyTransactionDTO to save.
     * @param loyaltyTransactionDTO the loyaltyTransactionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated loyaltyTransactionDTO,
     * or with status {@code 400 (Bad Request)} if the loyaltyTransactionDTO is not valid,
     * or with status {@code 404 (Not Found)} if the loyaltyTransactionDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the loyaltyTransactionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<LoyaltyTransactionDTO> partialUpdateLoyaltyTransaction(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody LoyaltyTransactionDTO loyaltyTransactionDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update LoyaltyTransaction partially : {}, {}", id, loyaltyTransactionDTO);
        if (loyaltyTransactionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, loyaltyTransactionDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!loyaltyTransactionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<LoyaltyTransactionDTO> result = loyaltyTransactionService.partialUpdate(loyaltyTransactionDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, loyaltyTransactionDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /loyalty-transactions} : get all the loyaltyTransactions.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of loyaltyTransactions in body.
     */
    @GetMapping("")
    public List<LoyaltyTransactionDTO> getAllLoyaltyTransactions(
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get all LoyaltyTransactions");
        return loyaltyTransactionService.findAll();
    }

    /**
     * {@code GET  /loyalty-transactions/:id} : get the "id" loyaltyTransaction.
     *
     * @param id the id of the loyaltyTransactionDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the loyaltyTransactionDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<LoyaltyTransactionDTO> getLoyaltyTransaction(@PathVariable("id") Long id) {
        LOG.debug("REST request to get LoyaltyTransaction : {}", id);
        Optional<LoyaltyTransactionDTO> loyaltyTransactionDTO = loyaltyTransactionService.findOne(id);
        return ResponseUtil.wrapOrNotFound(loyaltyTransactionDTO);
    }

    /**
     * {@code DELETE  /loyalty-transactions/:id} : delete the "id" loyaltyTransaction.
     *
     * @param id the id of the loyaltyTransactionDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLoyaltyTransaction(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete LoyaltyTransaction : {}", id);
        loyaltyTransactionService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
