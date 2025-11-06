package com.lumiere.app.web.rest;

import com.lumiere.app.repository.VoucherRepository;
import com.lumiere.app.service.VoucherService;
import com.lumiere.app.service.dto.VoucherDTO;
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
 * REST controller for managing {@link com.lumiere.app.domain.Voucher}.
 */
@RestController
@RequestMapping("/api/vouchers")
public class VoucherResource {

    private static final Logger LOG = LoggerFactory.getLogger(VoucherResource.class);

    private static final String ENTITY_NAME = "voucher";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final VoucherService voucherService;

    private final VoucherRepository voucherRepository;

    public VoucherResource(VoucherService voucherService, VoucherRepository voucherRepository) {
        this.voucherService = voucherService;
        this.voucherRepository = voucherRepository;
    }

    /**
     * {@code POST  /vouchers} : Create a new voucher.
     *
     * @param voucherDTO the voucherDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new voucherDTO, or with status {@code 400 (Bad Request)} if the voucher has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<VoucherDTO> createVoucher(@Valid @RequestBody VoucherDTO voucherDTO) throws URISyntaxException {
        LOG.debug("REST request to save Voucher : {}", voucherDTO);
        if (voucherDTO.getId() != null) {
            throw new BadRequestAlertException("A new voucher cannot already have an ID", ENTITY_NAME, "idexists");
        }
        voucherDTO = voucherService.save(voucherDTO);
        return ResponseEntity.created(new URI("/api/vouchers/" + voucherDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, voucherDTO.getId().toString()))
            .body(voucherDTO);
    }

    /**
     * {@code PUT  /vouchers/:id} : Updates an existing voucher.
     *
     * @param id the id of the voucherDTO to save.
     * @param voucherDTO the voucherDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated voucherDTO,
     * or with status {@code 400 (Bad Request)} if the voucherDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the voucherDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<VoucherDTO> updateVoucher(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody VoucherDTO voucherDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Voucher : {}, {}", id, voucherDTO);
        if (voucherDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, voucherDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!voucherRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        voucherDTO = voucherService.update(voucherDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, voucherDTO.getId().toString()))
            .body(voucherDTO);
    }

    /**
     * {@code PATCH  /vouchers/:id} : Partial updates given fields of an existing voucher, field will ignore if it is null
     *
     * @param id the id of the voucherDTO to save.
     * @param voucherDTO the voucherDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated voucherDTO,
     * or with status {@code 400 (Bad Request)} if the voucherDTO is not valid,
     * or with status {@code 404 (Not Found)} if the voucherDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the voucherDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<VoucherDTO> partialUpdateVoucher(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody VoucherDTO voucherDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Voucher partially : {}, {}", id, voucherDTO);
        if (voucherDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, voucherDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!voucherRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<VoucherDTO> result = voucherService.partialUpdate(voucherDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, voucherDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /vouchers} : get all the vouchers.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of vouchers in body.
     */
    @GetMapping("")
    public List<VoucherDTO> getAllVouchers() {
        LOG.debug("REST request to get all Vouchers");
        return voucherService.findAll();
    }

    /**
     * {@code GET  /vouchers/:id} : get the "id" voucher.
     *
     * @param id the id of the voucherDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the voucherDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<VoucherDTO> getVoucher(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Voucher : {}", id);
        Optional<VoucherDTO> voucherDTO = voucherService.findOne(id);
        return ResponseUtil.wrapOrNotFound(voucherDTO);
    }

    /**
     * {@code DELETE  /vouchers/:id} : delete the "id" voucher.
     *
     * @param id the id of the voucherDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVoucher(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Voucher : {}", id);
        voucherService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
