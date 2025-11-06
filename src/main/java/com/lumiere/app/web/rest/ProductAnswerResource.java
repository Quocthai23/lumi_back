package com.lumiere.app.web.rest;

import com.lumiere.app.repository.ProductAnswerRepository;
import com.lumiere.app.service.ProductAnswerService;
import com.lumiere.app.service.dto.ProductAnswerDTO;
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
 * REST controller for managing {@link com.lumiere.app.domain.ProductAnswer}.
 */
@RestController
@RequestMapping("/api/product-answers")
public class ProductAnswerResource {

    private static final Logger LOG = LoggerFactory.getLogger(ProductAnswerResource.class);

    private static final String ENTITY_NAME = "productAnswer";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ProductAnswerService productAnswerService;

    private final ProductAnswerRepository productAnswerRepository;

    public ProductAnswerResource(ProductAnswerService productAnswerService, ProductAnswerRepository productAnswerRepository) {
        this.productAnswerService = productAnswerService;
        this.productAnswerRepository = productAnswerRepository;
    }

    /**
     * {@code POST  /product-answers} : Create a new productAnswer.
     *
     * @param productAnswerDTO the productAnswerDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new productAnswerDTO, or with status {@code 400 (Bad Request)} if the productAnswer has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ProductAnswerDTO> createProductAnswer(@Valid @RequestBody ProductAnswerDTO productAnswerDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save ProductAnswer : {}", productAnswerDTO);
        if (productAnswerDTO.getId() != null) {
            throw new BadRequestAlertException("A new productAnswer cannot already have an ID", ENTITY_NAME, "idexists");
        }
        productAnswerDTO = productAnswerService.save(productAnswerDTO);
        return ResponseEntity.created(new URI("/api/product-answers/" + productAnswerDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, productAnswerDTO.getId().toString()))
            .body(productAnswerDTO);
    }

    /**
     * {@code PUT  /product-answers/:id} : Updates an existing productAnswer.
     *
     * @param id the id of the productAnswerDTO to save.
     * @param productAnswerDTO the productAnswerDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated productAnswerDTO,
     * or with status {@code 400 (Bad Request)} if the productAnswerDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the productAnswerDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ProductAnswerDTO> updateProductAnswer(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ProductAnswerDTO productAnswerDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update ProductAnswer : {}, {}", id, productAnswerDTO);
        if (productAnswerDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, productAnswerDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!productAnswerRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        productAnswerDTO = productAnswerService.update(productAnswerDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, productAnswerDTO.getId().toString()))
            .body(productAnswerDTO);
    }

    /**
     * {@code PATCH  /product-answers/:id} : Partial updates given fields of an existing productAnswer, field will ignore if it is null
     *
     * @param id the id of the productAnswerDTO to save.
     * @param productAnswerDTO the productAnswerDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated productAnswerDTO,
     * or with status {@code 400 (Bad Request)} if the productAnswerDTO is not valid,
     * or with status {@code 404 (Not Found)} if the productAnswerDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the productAnswerDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ProductAnswerDTO> partialUpdateProductAnswer(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ProductAnswerDTO productAnswerDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update ProductAnswer partially : {}, {}", id, productAnswerDTO);
        if (productAnswerDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, productAnswerDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!productAnswerRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ProductAnswerDTO> result = productAnswerService.partialUpdate(productAnswerDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, productAnswerDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /product-answers} : get all the productAnswers.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of productAnswers in body.
     */
    @GetMapping("")
    public List<ProductAnswerDTO> getAllProductAnswers(
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get all ProductAnswers");
        return productAnswerService.findAll();
    }

    /**
     * {@code GET  /product-answers/:id} : get the "id" productAnswer.
     *
     * @param id the id of the productAnswerDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the productAnswerDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProductAnswerDTO> getProductAnswer(@PathVariable("id") Long id) {
        LOG.debug("REST request to get ProductAnswer : {}", id);
        Optional<ProductAnswerDTO> productAnswerDTO = productAnswerService.findOne(id);
        return ResponseUtil.wrapOrNotFound(productAnswerDTO);
    }

    /**
     * {@code DELETE  /product-answers/:id} : delete the "id" productAnswer.
     *
     * @param id the id of the productAnswerDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProductAnswer(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete ProductAnswer : {}", id);
        productAnswerService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
