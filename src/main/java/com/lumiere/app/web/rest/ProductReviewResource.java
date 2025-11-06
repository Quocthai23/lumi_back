package com.lumiere.app.web.rest;

import com.lumiere.app.repository.ProductReviewRepository;
import com.lumiere.app.service.ProductReviewService;
import com.lumiere.app.service.dto.ProductReviewDTO;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.lumiere.app.domain.ProductReview}.
 */
@RestController
@RequestMapping("/api/product-reviews")
public class ProductReviewResource {

    private static final Logger LOG = LoggerFactory.getLogger(ProductReviewResource.class);

    private static final String ENTITY_NAME = "productReview";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ProductReviewService productReviewService;

    private final ProductReviewRepository productReviewRepository;

    public ProductReviewResource(ProductReviewService productReviewService, ProductReviewRepository productReviewRepository) {
        this.productReviewService = productReviewService;
        this.productReviewRepository = productReviewRepository;
    }

    /**
     * {@code POST  /product-reviews} : Create a new productReview.
     *
     * @param productReviewDTO the productReviewDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new productReviewDTO, or with status {@code 400 (Bad Request)} if the productReview has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ProductReviewDTO> createProductReview(@Valid @RequestBody ProductReviewDTO productReviewDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save ProductReview : {}", productReviewDTO);
        if (productReviewDTO.getId() != null) {
            throw new BadRequestAlertException("A new productReview cannot already have an ID", ENTITY_NAME, "idexists");
        }
        productReviewDTO = productReviewService.save(productReviewDTO);
        return ResponseEntity.created(new URI("/api/product-reviews/" + productReviewDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, productReviewDTO.getId().toString()))
            .body(productReviewDTO);
    }

    /**
     * {@code PUT  /product-reviews/:id} : Updates an existing productReview.
     *
     * @param id the id of the productReviewDTO to save.
     * @param productReviewDTO the productReviewDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated productReviewDTO,
     * or with status {@code 400 (Bad Request)} if the productReviewDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the productReviewDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ProductReviewDTO> updateProductReview(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ProductReviewDTO productReviewDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update ProductReview : {}, {}", id, productReviewDTO);
        if (productReviewDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, productReviewDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!productReviewRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        productReviewDTO = productReviewService.update(productReviewDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, productReviewDTO.getId().toString()))
            .body(productReviewDTO);
    }

    /**
     * {@code PATCH  /product-reviews/:id} : Partial updates given fields of an existing productReview, field will ignore if it is null
     *
     * @param id the id of the productReviewDTO to save.
     * @param productReviewDTO the productReviewDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated productReviewDTO,
     * or with status {@code 400 (Bad Request)} if the productReviewDTO is not valid,
     * or with status {@code 404 (Not Found)} if the productReviewDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the productReviewDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ProductReviewDTO> partialUpdateProductReview(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ProductReviewDTO productReviewDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update ProductReview partially : {}, {}", id, productReviewDTO);
        if (productReviewDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, productReviewDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!productReviewRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ProductReviewDTO> result = productReviewService.partialUpdate(productReviewDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, productReviewDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /product-reviews} : get all the productReviews.
     *
     * @param pageable the pagination information.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of productReviews in body.
     */
    @GetMapping("")
    public ResponseEntity<List<ProductReviewDTO>> getAllProductReviews(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get a page of ProductReviews");
        Page<ProductReviewDTO> page;
        if (eagerload) {
            page = productReviewService.findAllWithEagerRelationships(pageable);
        } else {
            page = productReviewService.findAll(pageable);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /product-reviews/:id} : get the "id" productReview.
     *
     * @param id the id of the productReviewDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the productReviewDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProductReviewDTO> getProductReview(@PathVariable("id") Long id) {
        LOG.debug("REST request to get ProductReview : {}", id);
        Optional<ProductReviewDTO> productReviewDTO = productReviewService.findOne(id);
        return ResponseUtil.wrapOrNotFound(productReviewDTO);
    }

    /**
     * {@code DELETE  /product-reviews/:id} : delete the "id" productReview.
     *
     * @param id the id of the productReviewDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProductReview(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete ProductReview : {}", id);
        productReviewService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
