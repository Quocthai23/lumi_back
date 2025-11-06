package com.lumiere.app.web.rest;

import static com.lumiere.app.domain.VoucherAsserts.*;
import static com.lumiere.app.web.rest.TestUtil.createUpdateProxyForBean;
import static com.lumiere.app.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lumiere.app.IntegrationTest;
import com.lumiere.app.domain.Voucher;
import com.lumiere.app.domain.enumeration.VoucherStatus;
import com.lumiere.app.domain.enumeration.VoucherType;
import com.lumiere.app.repository.VoucherRepository;
import com.lumiere.app.service.dto.VoucherDTO;
import com.lumiere.app.service.mapper.VoucherMapper;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link VoucherResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class VoucherResourceIT {

    private static final String DEFAULT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CODE = "BBBBBBBBBB";

    private static final VoucherType DEFAULT_TYPE = VoucherType.PERCENTAGE;
    private static final VoucherType UPDATED_TYPE = VoucherType.FIXED_AMOUNT;

    private static final BigDecimal DEFAULT_VALUE = new BigDecimal(0);
    private static final BigDecimal UPDATED_VALUE = new BigDecimal(1);

    private static final VoucherStatus DEFAULT_STATUS = VoucherStatus.ACTIVE;
    private static final VoucherStatus UPDATED_STATUS = VoucherStatus.INACTIVE;

    private static final Instant DEFAULT_START_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_START_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_END_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_END_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Integer DEFAULT_USAGE_LIMIT = 1;
    private static final Integer UPDATED_USAGE_LIMIT = 2;

    private static final Integer DEFAULT_USAGE_COUNT = 1;
    private static final Integer UPDATED_USAGE_COUNT = 2;

    private static final String ENTITY_API_URL = "/api/vouchers";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private VoucherRepository voucherRepository;

    @Autowired
    private VoucherMapper voucherMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restVoucherMockMvc;

    private Voucher voucher;

    private Voucher insertedVoucher;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Voucher createEntity() {
        return new Voucher()
            .code(DEFAULT_CODE)
            .type(DEFAULT_TYPE)
            .value(DEFAULT_VALUE)
            .status(DEFAULT_STATUS)
            .startDate(DEFAULT_START_DATE)
            .endDate(DEFAULT_END_DATE)
            .usageLimit(DEFAULT_USAGE_LIMIT)
            .usageCount(DEFAULT_USAGE_COUNT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Voucher createUpdatedEntity() {
        return new Voucher()
            .code(UPDATED_CODE)
            .type(UPDATED_TYPE)
            .value(UPDATED_VALUE)
            .status(UPDATED_STATUS)
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE)
            .usageLimit(UPDATED_USAGE_LIMIT)
            .usageCount(UPDATED_USAGE_COUNT);
    }

    @BeforeEach
    void initTest() {
        voucher = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedVoucher != null) {
            voucherRepository.delete(insertedVoucher);
            insertedVoucher = null;
        }
    }

    @Test
    @Transactional
    void createVoucher() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Voucher
        VoucherDTO voucherDTO = voucherMapper.toDto(voucher);
        var returnedVoucherDTO = om.readValue(
            restVoucherMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(voucherDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            VoucherDTO.class
        );

        // Validate the Voucher in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedVoucher = voucherMapper.toEntity(returnedVoucherDTO);
        assertVoucherUpdatableFieldsEquals(returnedVoucher, getPersistedVoucher(returnedVoucher));

        insertedVoucher = returnedVoucher;
    }

    @Test
    @Transactional
    void createVoucherWithExistingId() throws Exception {
        // Create the Voucher with an existing ID
        voucher.setId(1L);
        VoucherDTO voucherDTO = voucherMapper.toDto(voucher);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restVoucherMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(voucherDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Voucher in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        voucher.setCode(null);

        // Create the Voucher, which fails.
        VoucherDTO voucherDTO = voucherMapper.toDto(voucher);

        restVoucherMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(voucherDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        voucher.setType(null);

        // Create the Voucher, which fails.
        VoucherDTO voucherDTO = voucherMapper.toDto(voucher);

        restVoucherMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(voucherDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkValueIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        voucher.setValue(null);

        // Create the Voucher, which fails.
        VoucherDTO voucherDTO = voucherMapper.toDto(voucher);

        restVoucherMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(voucherDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        voucher.setStatus(null);

        // Create the Voucher, which fails.
        VoucherDTO voucherDTO = voucherMapper.toDto(voucher);

        restVoucherMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(voucherDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllVouchers() throws Exception {
        // Initialize the database
        insertedVoucher = voucherRepository.saveAndFlush(voucher);

        // Get all the voucherList
        restVoucherMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(voucher.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].value").value(hasItem(sameNumber(DEFAULT_VALUE))))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].startDate").value(hasItem(DEFAULT_START_DATE.toString())))
            .andExpect(jsonPath("$.[*].endDate").value(hasItem(DEFAULT_END_DATE.toString())))
            .andExpect(jsonPath("$.[*].usageLimit").value(hasItem(DEFAULT_USAGE_LIMIT)))
            .andExpect(jsonPath("$.[*].usageCount").value(hasItem(DEFAULT_USAGE_COUNT)));
    }

    @Test
    @Transactional
    void getVoucher() throws Exception {
        // Initialize the database
        insertedVoucher = voucherRepository.saveAndFlush(voucher);

        // Get the voucher
        restVoucherMockMvc
            .perform(get(ENTITY_API_URL_ID, voucher.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(voucher.getId().intValue()))
            .andExpect(jsonPath("$.code").value(DEFAULT_CODE))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()))
            .andExpect(jsonPath("$.value").value(sameNumber(DEFAULT_VALUE)))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.startDate").value(DEFAULT_START_DATE.toString()))
            .andExpect(jsonPath("$.endDate").value(DEFAULT_END_DATE.toString()))
            .andExpect(jsonPath("$.usageLimit").value(DEFAULT_USAGE_LIMIT))
            .andExpect(jsonPath("$.usageCount").value(DEFAULT_USAGE_COUNT));
    }

    @Test
    @Transactional
    void getNonExistingVoucher() throws Exception {
        // Get the voucher
        restVoucherMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingVoucher() throws Exception {
        // Initialize the database
        insertedVoucher = voucherRepository.saveAndFlush(voucher);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the voucher
        Voucher updatedVoucher = voucherRepository.findById(voucher.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedVoucher are not directly saved in db
        em.detach(updatedVoucher);
        updatedVoucher
            .code(UPDATED_CODE)
            .type(UPDATED_TYPE)
            .value(UPDATED_VALUE)
            .status(UPDATED_STATUS)
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE)
            .usageLimit(UPDATED_USAGE_LIMIT)
            .usageCount(UPDATED_USAGE_COUNT);
        VoucherDTO voucherDTO = voucherMapper.toDto(updatedVoucher);

        restVoucherMockMvc
            .perform(
                put(ENTITY_API_URL_ID, voucherDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(voucherDTO))
            )
            .andExpect(status().isOk());

        // Validate the Voucher in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedVoucherToMatchAllProperties(updatedVoucher);
    }

    @Test
    @Transactional
    void putNonExistingVoucher() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        voucher.setId(longCount.incrementAndGet());

        // Create the Voucher
        VoucherDTO voucherDTO = voucherMapper.toDto(voucher);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restVoucherMockMvc
            .perform(
                put(ENTITY_API_URL_ID, voucherDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(voucherDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Voucher in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchVoucher() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        voucher.setId(longCount.incrementAndGet());

        // Create the Voucher
        VoucherDTO voucherDTO = voucherMapper.toDto(voucher);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVoucherMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(voucherDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Voucher in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamVoucher() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        voucher.setId(longCount.incrementAndGet());

        // Create the Voucher
        VoucherDTO voucherDTO = voucherMapper.toDto(voucher);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVoucherMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(voucherDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Voucher in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateVoucherWithPatch() throws Exception {
        // Initialize the database
        insertedVoucher = voucherRepository.saveAndFlush(voucher);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the voucher using partial update
        Voucher partialUpdatedVoucher = new Voucher();
        partialUpdatedVoucher.setId(voucher.getId());

        partialUpdatedVoucher.type(UPDATED_TYPE).status(UPDATED_STATUS).startDate(UPDATED_START_DATE).usageCount(UPDATED_USAGE_COUNT);

        restVoucherMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedVoucher.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedVoucher))
            )
            .andExpect(status().isOk());

        // Validate the Voucher in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertVoucherUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedVoucher, voucher), getPersistedVoucher(voucher));
    }

    @Test
    @Transactional
    void fullUpdateVoucherWithPatch() throws Exception {
        // Initialize the database
        insertedVoucher = voucherRepository.saveAndFlush(voucher);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the voucher using partial update
        Voucher partialUpdatedVoucher = new Voucher();
        partialUpdatedVoucher.setId(voucher.getId());

        partialUpdatedVoucher
            .code(UPDATED_CODE)
            .type(UPDATED_TYPE)
            .value(UPDATED_VALUE)
            .status(UPDATED_STATUS)
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE)
            .usageLimit(UPDATED_USAGE_LIMIT)
            .usageCount(UPDATED_USAGE_COUNT);

        restVoucherMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedVoucher.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedVoucher))
            )
            .andExpect(status().isOk());

        // Validate the Voucher in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertVoucherUpdatableFieldsEquals(partialUpdatedVoucher, getPersistedVoucher(partialUpdatedVoucher));
    }

    @Test
    @Transactional
    void patchNonExistingVoucher() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        voucher.setId(longCount.incrementAndGet());

        // Create the Voucher
        VoucherDTO voucherDTO = voucherMapper.toDto(voucher);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restVoucherMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, voucherDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(voucherDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Voucher in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchVoucher() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        voucher.setId(longCount.incrementAndGet());

        // Create the Voucher
        VoucherDTO voucherDTO = voucherMapper.toDto(voucher);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVoucherMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(voucherDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Voucher in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamVoucher() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        voucher.setId(longCount.incrementAndGet());

        // Create the Voucher
        VoucherDTO voucherDTO = voucherMapper.toDto(voucher);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVoucherMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(voucherDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Voucher in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteVoucher() throws Exception {
        // Initialize the database
        insertedVoucher = voucherRepository.saveAndFlush(voucher);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the voucher
        restVoucherMockMvc
            .perform(delete(ENTITY_API_URL_ID, voucher.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return voucherRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected Voucher getPersistedVoucher(Voucher voucher) {
        return voucherRepository.findById(voucher.getId()).orElseThrow();
    }

    protected void assertPersistedVoucherToMatchAllProperties(Voucher expectedVoucher) {
        assertVoucherAllPropertiesEquals(expectedVoucher, getPersistedVoucher(expectedVoucher));
    }

    protected void assertPersistedVoucherToMatchUpdatableProperties(Voucher expectedVoucher) {
        assertVoucherAllUpdatablePropertiesEquals(expectedVoucher, getPersistedVoucher(expectedVoucher));
    }
}
