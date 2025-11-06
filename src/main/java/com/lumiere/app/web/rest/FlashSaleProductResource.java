package com.lumiere.app.web.rest;

import com.lumiere.app.repository.FlashSaleProductRepository;
import com.lumiere.app.service.FlashSaleProductService;
import com.lumiere.app.service.dto.FlashSaleProductDTO;
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
 * REST controller for managing {@link com.lumiere.app.domain.FlashSaleProduct}.
 */
@RestController
@RequestMapping("/api/flash-sale-products")
public class FlashSaleProductResource {

    private static final Logger LOG = LoggerFactory.getLogger(FlashSaleProductResource.class);

    private static final String ENTITY_NAME = "flashSaleProduct";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final FlashSaleProductService flashSaleProductService;

    private final FlashSaleProductRepository flashSaleProductRepository;

    public FlashSaleProductResource(
        FlashSaleProductService flashSaleProductService,
        FlashSaleProductRepository flashSaleProductRepository
    ) {
        this.flashSaleProductService = flashSaleProductService;
        this.flashSaleProductRepository = flashSaleProductRepository;
    }

    /**
     * {@code POST  /flash-sale-products} : Create a new flashSaleProduct.
     *
     * @param flashSaleProductDTO the flashSaleProductDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new flashSaleProductDTO, or with status {@code 400 (Bad Request)} if the flashSaleProduct has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<FlashSaleProductDTO> createFlashSaleProduct(@Valid @RequestBody FlashSaleProductDTO flashSaleProductDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save FlashSaleProduct : {}", flashSaleProductDTO);
        if (flashSaleProductDTO.getId() != null) {
            throw new BadRequestAlertException("A new flashSaleProduct cannot already have an ID", ENTITY_NAME, "idexists");
        }
        flashSaleProductDTO = flashSaleProductService.save(flashSaleProductDTO);
        return ResponseEntity.created(new URI("/api/flash-sale-products/" + flashSaleProductDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, flashSaleProductDTO.getId().toString()))
            .body(flashSaleProductDTO);
    }

    /**
     * {@code PUT  /flash-sale-products/:id} : Updates an existing flashSaleProduct.
     *
     * @param id the id of the flashSaleProductDTO to save.
     * @param flashSaleProductDTO the flashSaleProductDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated flashSaleProductDTO,
     * or with status {@code 400 (Bad Request)} if the flashSaleProductDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the flashSaleProductDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<FlashSaleProductDTO> updateFlashSaleProduct(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody FlashSaleProductDTO flashSaleProductDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update FlashSaleProduct : {}, {}", id, flashSaleProductDTO);
        if (flashSaleProductDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, flashSaleProductDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!flashSaleProductRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        flashSaleProductDTO = flashSaleProductService.update(flashSaleProductDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, flashSaleProductDTO.getId().toString()))
            .body(flashSaleProductDTO);
    }

    /**
     * {@code PATCH  /flash-sale-products/:id} : Partial updates given fields of an existing flashSaleProduct, field will ignore if it is null
     *
     * @param id the id of the flashSaleProductDTO to save.
     * @param flashSaleProductDTO the flashSaleProductDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated flashSaleProductDTO,
     * or with status {@code 400 (Bad Request)} if the flashSaleProductDTO is not valid,
     * or with status {@code 404 (Not Found)} if the flashSaleProductDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the flashSaleProductDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<FlashSaleProductDTO> partialUpdateFlashSaleProduct(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody FlashSaleProductDTO flashSaleProductDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update FlashSaleProduct partially : {}, {}", id, flashSaleProductDTO);
        if (flashSaleProductDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, flashSaleProductDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!flashSaleProductRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<FlashSaleProductDTO> result = flashSaleProductService.partialUpdate(flashSaleProductDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, flashSaleProductDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /flash-sale-products} : get all the flashSaleProducts.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of flashSaleProducts in body.
     */
    @GetMapping("")
    public List<FlashSaleProductDTO> getAllFlashSaleProducts(
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get all FlashSaleProducts");
        return flashSaleProductService.findAll();
    }

    /**
     * {@code GET  /flash-sale-products/:id} : get the "id" flashSaleProduct.
     *
     * @param id the id of the flashSaleProductDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the flashSaleProductDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<FlashSaleProductDTO> getFlashSaleProduct(@PathVariable("id") Long id) {
        LOG.debug("REST request to get FlashSaleProduct : {}", id);
        Optional<FlashSaleProductDTO> flashSaleProductDTO = flashSaleProductService.findOne(id);
        return ResponseUtil.wrapOrNotFound(flashSaleProductDTO);
    }

    /**
     * {@code DELETE  /flash-sale-products/:id} : delete the "id" flashSaleProduct.
     *
     * @param id the id of the flashSaleProductDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFlashSaleProduct(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete FlashSaleProduct : {}", id);
        flashSaleProductService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
