package com.lumiere.app.web.rest;

import com.lumiere.app.repository.CustomerInfoRepository;
import com.lumiere.app.service.CustomerInfoService;
import com.lumiere.app.service.dto.CustomerInfoDTO;
import com.lumiere.app.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * REST controller for managing {@link com.lumiere.app.domain.CustomerInfo}.
 */
@RestController
@RequestMapping("/api/customer-infos")
public class CustomerInfoResource {

    private static final Logger LOG = LoggerFactory.getLogger(CustomerInfoResource.class);

    private static final String ENTITY_NAME = "customerInfo";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CustomerInfoService customerInfoService;

    private final CustomerInfoRepository customerInfoRepository;

    public CustomerInfoResource(CustomerInfoService customerInfoService, CustomerInfoRepository customerInfoRepository) {
        this.customerInfoService = customerInfoService;
        this.customerInfoRepository = customerInfoRepository;
    }

    /**
     * {@code POST  /customer-infos} : Create a new customerInfo.
     *
     * @param customerInfoDTO the customerInfoDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new customerInfoDTO, or with status {@code 400 (Bad Request)} if the customerInfo has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<CustomerInfoDTO> createCustomerInfo(@Valid @RequestBody CustomerInfoDTO customerInfoDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save CustomerInfo : {}", customerInfoDTO);
        if (customerInfoDTO.getId() != null) {
            throw new BadRequestAlertException("A new customerInfo cannot already have an ID", ENTITY_NAME, "idexists");
        }
        CustomerInfoDTO result = customerInfoService.save(customerInfoDTO);
        return ResponseEntity
            .created(new URI("/api/customer-infos/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /customer-infos/:id} : Updates an existing customerInfo.
     *
     * @param id the id of the customerInfoDTO to save.
     * @param customerInfoDTO the customerInfoDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated customerInfoDTO,
     * or with status {@code 400 (Bad Request)} if the customerInfoDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the customerInfoDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<CustomerInfoDTO> updateCustomerInfo(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody CustomerInfoDTO customerInfoDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update CustomerInfo : {}, {}", id, customerInfoDTO);
        if (customerInfoDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, customerInfoDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!customerInfoRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        CustomerInfoDTO result = customerInfoService.save(customerInfoDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, customerInfoDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /customer-infos/:id} : Partial updates given fields of an existing customerInfo, field will be ignored if it is null
     *
     * @param id the id of the customerInfoDTO to save.
     * @param customerInfoDTO the customerInfoDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated customerInfoDTO,
     * or with status {@code 400 (Bad Request)} if the customerInfoDTO is not valid,
     * or with status {@code 404 (Not Found)} if the customerInfoDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the customerInfoDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<CustomerInfoDTO> partialUpdateCustomerInfo(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody CustomerInfoDTO customerInfoDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update CustomerInfo partially : {}, {}", id, customerInfoDTO);
        if (customerInfoDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, customerInfoDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!customerInfoRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<CustomerInfoDTO> result = Optional.of(customerInfoService.save(customerInfoDTO));

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, customerInfoDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /customer-infos} : get all the customerInfos.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of customerInfos in body.
     */
    @GetMapping("")
    public ResponseEntity<List<CustomerInfoDTO>> getAllCustomerInfos(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of CustomerInfos");
        Page<CustomerInfoDTO> page = customerInfoService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /customer-infos/:id} : get the "id" customerInfo.
     *
     * @param id the id of the customerInfoDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the customerInfoDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<CustomerInfoDTO> getCustomerInfo(@PathVariable Long id) {
        LOG.debug("REST request to get CustomerInfo : {}", id);
        Optional<CustomerInfoDTO> customerInfoDTO = customerInfoService.findOne(id);
        return ResponseUtil.wrapOrNotFound(customerInfoDTO);
    }

    /**
     * {@code DELETE  /customer-infos/:id} : delete the "id" customerInfo.
     *
     * @param id the id of the customerInfoDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomerInfo(@PathVariable Long id) {
        LOG.debug("REST request to delete CustomerInfo : {}", id);
        customerInfoService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code GET  /customer-infos/customer/:customerId} : get all customerInfos by customerId.
     *
     * @param customerId the id of the customer.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of customerInfos in body.
     */
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<CustomerInfoDTO>> getCustomerInfosByCustomerId(@PathVariable Long customerId) {
        LOG.debug("REST request to get CustomerInfos by customerId : {}", customerId);
        List<CustomerInfoDTO> customerInfoDTOs = customerInfoService.findAllByCustomerId(customerId);
        return ResponseEntity.ok().body(customerInfoDTOs);
    }

    /**
     * {@code PUT  /customer-infos/:id/set-default} : Set a customerInfo as default.
     *
     * @param id the id of the customerInfoDTO to set as default.
     * @param customerId the id of the customer.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated customerInfoDTO.
     */
    @PutMapping("/{id}/set-default")
    public ResponseEntity<CustomerInfoDTO> setCustomerInfoAsDefault(
        @PathVariable Long id,
        @RequestParam Long customerId
    ) {
        LOG.debug("REST request to set CustomerInfo {} as default for customer {}", id, customerId);
        CustomerInfoDTO result = customerInfoService.setAsDefault(id, customerId);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .body(result);
    }
}

