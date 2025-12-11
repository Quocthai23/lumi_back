package com.lumiere.app.web.rest;

import com.lumiere.app.repository.CustomerRepository;
import com.lumiere.app.repository.VoucherRepository;
import com.lumiere.app.security.SecurityUtils;
import com.lumiere.app.service.CustomerVoucherService;
import com.lumiere.app.service.VoucherService;
import com.lumiere.app.service.dto.CustomerVoucherDTO;
import com.lumiere.app.service.dto.VoucherCalculateRequestDTO;
import com.lumiere.app.service.dto.VoucherCalculateResponseDTO;
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

    private final CustomerRepository customerRepository;

    private final CustomerVoucherService customerVoucherService;

    public VoucherResource(
        VoucherService voucherService,
        VoucherRepository voucherRepository,
        CustomerRepository customerRepository,
        CustomerVoucherService customerVoucherService
    ) {
        this.voucherService = voucherService;
        this.voucherRepository = voucherRepository;
        this.customerRepository = customerRepository;
        this.customerVoucherService = customerVoucherService;
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
     * @param pageable the pagination information.
     * @param availableOnly if true, only return available vouchers (ACTIVE and not expired).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of vouchers in body.
     */
    @GetMapping("")
    public ResponseEntity<List<VoucherDTO>> getAllVouchers(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        @RequestParam(name = "availableOnly", required = false, defaultValue = "false") boolean availableOnly
    ) {
        LOG.debug("REST request to get Vouchers with pagination, availableOnly: {}", availableOnly);
        
        if (availableOnly) {
            Page<VoucherDTO> page = voucherService.findAllAvailable(pageable);
            HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
            return ResponseEntity.ok().headers(headers).body(page.getContent());
        } else {
            // For admin, return all vouchers without pagination (backward compatibility)
            List<VoucherDTO> vouchers = voucherService.findAll();
            return ResponseEntity.ok().body(vouchers);
        }
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

    /**
     * {@code POST  /vouchers/calculate} : Tính tiền giảm giá từ voucher code.
     *
     * @param request request chứa voucher code và order amount
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the VoucherCalculateResponseDTO,
     * or with status {@code 400 (Bad Request)} if the request is invalid.
     */
    @PostMapping("/calculate")
    public ResponseEntity<VoucherCalculateResponseDTO> calculateDiscount(@Valid @RequestBody VoucherCalculateRequestDTO request) {
        LOG.debug("REST request to calculate discount for voucher: {}", request.getVoucherCode());

        // Lấy customerId từ user hiện tại
        Long customerId = null;
        Optional<Long> userIdOpt = SecurityUtils.getCurrentUserId();
        if (userIdOpt.isPresent()) {
            Long userId = userIdOpt.get();
            customerId = customerRepository
                .findByUserId(userId)
                .map(customer -> customer.getId())
                .orElse(null);
        }

        if (customerId == null) {
            throw new BadRequestAlertException(
                "Không tìm thấy thông tin khách hàng. Vui lòng đăng nhập lại.",
                ENTITY_NAME,
                "customernotfound"
            );
        }

        try {
            VoucherCalculateResponseDTO response = voucherService.calculateDiscount(request, customerId);
            return ResponseEntity.ok().body(response);
        } catch (IllegalArgumentException e) {
            throw new BadRequestAlertException(e.getMessage(), ENTITY_NAME, "voucherinvalid");
        }
    }

    /**
     * {@code POST  /vouchers/:id/claim} : Claim (lấy) một voucher cho khách hàng hiện tại.
     *
     * @param id the id of the voucher to claim.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the CustomerVoucherDTO,
     * or with status {@code 400 (Bad Request)} if the request is invalid.
     */
    @PostMapping("/{id}/claim")
    public ResponseEntity<CustomerVoucherDTO> claimVoucher(@PathVariable("id") Long id) {
        LOG.debug("REST request to claim Voucher : {}", id);

        // Lấy userId từ user hiện tại
        Long userId = SecurityUtils
            .getCurrentUserId()
            .orElseThrow(() -> new BadRequestAlertException(
                "Không tìm thấy thông tin người dùng. Vui lòng đăng nhập lại.",
                ENTITY_NAME,
                "usernotfound"
            ));

        try {
            CustomerVoucherDTO customerVoucherDTO = customerVoucherService.claimVoucher(userId, id);
            return ResponseEntity.ok().body(customerVoucherDTO);
        } catch (IllegalArgumentException e) {
            throw new BadRequestAlertException(e.getMessage(), ENTITY_NAME, "voucherclaimfailed");
        }
    }
}
