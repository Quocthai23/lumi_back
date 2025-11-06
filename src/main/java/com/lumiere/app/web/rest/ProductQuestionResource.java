package com.lumiere.app.web.rest;

import com.lumiere.app.repository.ProductQuestionRepository;
import com.lumiere.app.service.ProductQuestionService;
import com.lumiere.app.service.dto.ProductQuestionDTO;
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
 * REST controller for managing {@link com.lumiere.app.domain.ProductQuestion}.
 */
@RestController
@RequestMapping("/api/product-questions")
public class ProductQuestionResource {

    private static final Logger LOG = LoggerFactory.getLogger(ProductQuestionResource.class);

    private static final String ENTITY_NAME = "productQuestion";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ProductQuestionService productQuestionService;

    private final ProductQuestionRepository productQuestionRepository;

    public ProductQuestionResource(ProductQuestionService productQuestionService, ProductQuestionRepository productQuestionRepository) {
        this.productQuestionService = productQuestionService;
        this.productQuestionRepository = productQuestionRepository;
    }

    /**
     * {@code POST  /product-questions} : Create a new productQuestion.
     *
     * @param productQuestionDTO the productQuestionDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new productQuestionDTO, or with status {@code 400 (Bad Request)} if the productQuestion has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ProductQuestionDTO> createProductQuestion(@Valid @RequestBody ProductQuestionDTO productQuestionDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save ProductQuestion : {}", productQuestionDTO);
        if (productQuestionDTO.getId() != null) {
            throw new BadRequestAlertException("A new productQuestion cannot already have an ID", ENTITY_NAME, "idexists");
        }
        productQuestionDTO = productQuestionService.save(productQuestionDTO);
        return ResponseEntity.created(new URI("/api/product-questions/" + productQuestionDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, productQuestionDTO.getId().toString()))
            .body(productQuestionDTO);
    }

    /**
     * {@code PUT  /product-questions/:id} : Updates an existing productQuestion.
     *
     * @param id the id of the productQuestionDTO to save.
     * @param productQuestionDTO the productQuestionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated productQuestionDTO,
     * or with status {@code 400 (Bad Request)} if the productQuestionDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the productQuestionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ProductQuestionDTO> updateProductQuestion(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ProductQuestionDTO productQuestionDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update ProductQuestion : {}, {}", id, productQuestionDTO);
        if (productQuestionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, productQuestionDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!productQuestionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        productQuestionDTO = productQuestionService.update(productQuestionDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, productQuestionDTO.getId().toString()))
            .body(productQuestionDTO);
    }

    /**
     * {@code PATCH  /product-questions/:id} : Partial updates given fields of an existing productQuestion, field will ignore if it is null
     *
     * @param id the id of the productQuestionDTO to save.
     * @param productQuestionDTO the productQuestionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated productQuestionDTO,
     * or with status {@code 400 (Bad Request)} if the productQuestionDTO is not valid,
     * or with status {@code 404 (Not Found)} if the productQuestionDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the productQuestionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ProductQuestionDTO> partialUpdateProductQuestion(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ProductQuestionDTO productQuestionDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update ProductQuestion partially : {}, {}", id, productQuestionDTO);
        if (productQuestionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, productQuestionDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!productQuestionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ProductQuestionDTO> result = productQuestionService.partialUpdate(productQuestionDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, productQuestionDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /product-questions} : get all the productQuestions.
     *
     * @param pageable the pagination information.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of productQuestions in body.
     */
    @GetMapping("")
    public ResponseEntity<List<ProductQuestionDTO>> getAllProductQuestions(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get a page of ProductQuestions");
        Page<ProductQuestionDTO> page;
        if (eagerload) {
            page = productQuestionService.findAllWithEagerRelationships(pageable);
        } else {
            page = productQuestionService.findAll(pageable);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /product-questions/:id} : get the "id" productQuestion.
     *
     * @param id the id of the productQuestionDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the productQuestionDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProductQuestionDTO> getProductQuestion(@PathVariable("id") Long id) {
        LOG.debug("REST request to get ProductQuestion : {}", id);
        Optional<ProductQuestionDTO> productQuestionDTO = productQuestionService.findOne(id);
        return ResponseUtil.wrapOrNotFound(productQuestionDTO);
    }

    /**
     * {@code DELETE  /product-questions/:id} : delete the "id" productQuestion.
     *
     * @param id the id of the productQuestionDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProductQuestion(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete ProductQuestion : {}", id);
        productQuestionService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
