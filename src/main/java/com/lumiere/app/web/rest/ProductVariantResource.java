package com.lumiere.app.web.rest;

import com.lumiere.app.repository.ProductVariantRepository;
import com.lumiere.app.service.FlashSaleProductService;
import com.lumiere.app.service.ProductVariantQueryService;
import com.lumiere.app.service.ProductVariantService;
import com.lumiere.app.service.criteria.ProductVariantCriteria;
import com.lumiere.app.service.dto.ProductVariantDTO;
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
 * REST controller for managing {@link com.lumiere.app.domain.ProductVariant}.
 */
@RestController
@RequestMapping("/api/product-variants")
public class ProductVariantResource {

    private static final Logger LOG = LoggerFactory.getLogger(ProductVariantResource.class);

    private static final String ENTITY_NAME = "productVariant";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ProductVariantService productVariantService;

    private final ProductVariantRepository productVariantRepository;

    private final ProductVariantQueryService productVariantQueryService;

    private final FlashSaleProductService flashSaleProductService;

    public ProductVariantResource(
        ProductVariantService productVariantService,
        ProductVariantRepository productVariantRepository,
        ProductVariantQueryService productVariantQueryService,
        FlashSaleProductService flashSaleProductService
    ) {
        this.productVariantService = productVariantService;
        this.productVariantRepository = productVariantRepository;
        this.productVariantQueryService = productVariantQueryService;
        this.flashSaleProductService = flashSaleProductService;
    }

    /**
     * {@code POST  /product-variants} : Create a new productVariant.
     *
     * @param productVariantDTO the productVariantDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new productVariantDTO, or with status {@code 400 (Bad Request)} if the productVariant has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ProductVariantDTO> createProductVariant(@Valid @RequestBody ProductVariantDTO productVariantDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save ProductVariant : {}", productVariantDTO);
        if (productVariantDTO.getId() != null) {
            throw new BadRequestAlertException("A new productVariant cannot already have an ID", ENTITY_NAME, "idexists");
        }
        productVariantDTO = productVariantService.save(productVariantDTO);
        return ResponseEntity.created(new URI("/api/product-variants/" + productVariantDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, productVariantDTO.getId().toString()))
            .body(productVariantDTO);
    }

    /**
     * {@code PUT  /product-variants/:id} : Updates an existing productVariant.
     *
     * @param id the id of the productVariantDTO to save.
     * @param productVariantDTO the productVariantDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated productVariantDTO,
     * or with status {@code 400 (Bad Request)} if the productVariantDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the productVariantDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ProductVariantDTO> updateProductVariant(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ProductVariantDTO productVariantDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update ProductVariant : {}, {}", id, productVariantDTO);
        if (productVariantDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, productVariantDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!productVariantRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        productVariantDTO = productVariantService.partialUpdate(productVariantDTO).get();
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, productVariantDTO.getId().toString()))
            .body(productVariantDTO);
    }

    /**
     * {@code PATCH  /product-variants/:id} : Partial updates given fields of an existing productVariant, field will ignore if it is null
     *
     * @param id the id of the productVariantDTO to save.
     * @param productVariantDTO the productVariantDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated productVariantDTO,
     * or with status {@code 400 (Bad Request)} if the productVariantDTO is not valid,
     * or with status {@code 404 (Not Found)} if the productVariantDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the productVariantDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ProductVariantDTO> partialUpdateProductVariant(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ProductVariantDTO productVariantDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update ProductVariant partially : {}, {}", id, productVariantDTO);
        if (productVariantDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, productVariantDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!productVariantRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ProductVariantDTO> result = productVariantService.partialUpdate(productVariantDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, productVariantDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /product-variants} : get all the productVariants.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of productVariants in body.
     */
    @GetMapping("")
    public ResponseEntity<List<ProductVariantDTO>> getAllProductVariants(ProductVariantCriteria criteria) {
        LOG.debug("REST request to get ProductVariants by criteria: {}", criteria);

        List<ProductVariantDTO> entityList = productVariantQueryService.findByCriteria(criteria);
        return ResponseEntity.ok().body(entityList);
    }

    /**
     * {@code GET  /product-variants/count} : count all the productVariants.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countProductVariants(ProductVariantCriteria criteria) {
        LOG.debug("REST request to count ProductVariants by criteria: {}", criteria);
        return ResponseEntity.ok().body(productVariantQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /product-variants/:id} : get the "id" productVariant.
     *
     * @param id the id of the productVariantDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the productVariantDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProductVariantDTO> getProductVariant(@PathVariable("id") Long id) {
        LOG.debug("REST request to get ProductVariant : {}", id);
        Optional<ProductVariantDTO> productVariantDTO = productVariantService.findOne(id);
        return ResponseUtil.wrapOrNotFound(productVariantDTO);
    }

    /**
     * {@code DELETE  /product-variants/:id} : delete the "id" productVariant.
     *
     * @param id the id of the productVariantDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProductVariant(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete ProductVariant : {}", id);
        productVariantService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    @PutMapping("/{productId}/default/{variantId}")
    public ResponseEntity<Void> setDefaultVariant(
        @PathVariable @NotNull Long productId,
        @PathVariable @NotNull Long variantId
    ) {
        productVariantService.setIsDefault(variantId, productId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/by-product-ids")
    public ResponseEntity<List<ProductVariantDTO>> setDefaultVariantByProductIds(
        @RequestParam @NotNull List<Long> productIds
    ) {
        List<ProductVariantDTO> rs = productIds.stream().map(id -> {
            ProductVariantDTO variant = productVariantService.findByProductId(id).stream().filter(ProductVariantDTO::getIsDefault).findFirst().orElse(null);
            if (variant != null && variant.getId() != null) {
                // Kiểm tra flash sale và set promotionPrice
                flashSaleProductService.findActiveByProductVariantId(variant.getId())
                    .ifPresent(flashSaleProduct -> {
                        variant.setPromotionPrice(flashSaleProduct.getSalePrice());
                    });
            }
            return variant;
        }).toList();
        return ResponseEntity.ok().body(rs);
    }




}
