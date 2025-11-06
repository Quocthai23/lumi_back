package com.lumiere.app.web.rest;

import static com.lumiere.app.domain.FlashSaleAsserts.*;
import static com.lumiere.app.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lumiere.app.IntegrationTest;
import com.lumiere.app.domain.FlashSale;
import com.lumiere.app.repository.FlashSaleRepository;
import com.lumiere.app.service.dto.FlashSaleDTO;
import com.lumiere.app.service.mapper.FlashSaleMapper;
import jakarta.persistence.EntityManager;
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
 * Integration tests for the {@link FlashSaleResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class FlashSaleResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final Instant DEFAULT_START_TIME = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_START_TIME = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_END_TIME = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_END_TIME = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/flash-sales";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private FlashSaleRepository flashSaleRepository;

    @Autowired
    private FlashSaleMapper flashSaleMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restFlashSaleMockMvc;

    private FlashSale flashSale;

    private FlashSale insertedFlashSale;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static FlashSale createEntity() {
        return new FlashSale().name(DEFAULT_NAME).startTime(DEFAULT_START_TIME).endTime(DEFAULT_END_TIME);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static FlashSale createUpdatedEntity() {
        return new FlashSale().name(UPDATED_NAME).startTime(UPDATED_START_TIME).endTime(UPDATED_END_TIME);
    }

    @BeforeEach
    void initTest() {
        flashSale = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedFlashSale != null) {
            flashSaleRepository.delete(insertedFlashSale);
            insertedFlashSale = null;
        }
    }

    @Test
    @Transactional
    void createFlashSale() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the FlashSale
        FlashSaleDTO flashSaleDTO = flashSaleMapper.toDto(flashSale);
        var returnedFlashSaleDTO = om.readValue(
            restFlashSaleMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(flashSaleDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            FlashSaleDTO.class
        );

        // Validate the FlashSale in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedFlashSale = flashSaleMapper.toEntity(returnedFlashSaleDTO);
        assertFlashSaleUpdatableFieldsEquals(returnedFlashSale, getPersistedFlashSale(returnedFlashSale));

        insertedFlashSale = returnedFlashSale;
    }

    @Test
    @Transactional
    void createFlashSaleWithExistingId() throws Exception {
        // Create the FlashSale with an existing ID
        flashSale.setId(1L);
        FlashSaleDTO flashSaleDTO = flashSaleMapper.toDto(flashSale);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restFlashSaleMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(flashSaleDTO)))
            .andExpect(status().isBadRequest());

        // Validate the FlashSale in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        flashSale.setName(null);

        // Create the FlashSale, which fails.
        FlashSaleDTO flashSaleDTO = flashSaleMapper.toDto(flashSale);

        restFlashSaleMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(flashSaleDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStartTimeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        flashSale.setStartTime(null);

        // Create the FlashSale, which fails.
        FlashSaleDTO flashSaleDTO = flashSaleMapper.toDto(flashSale);

        restFlashSaleMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(flashSaleDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkEndTimeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        flashSale.setEndTime(null);

        // Create the FlashSale, which fails.
        FlashSaleDTO flashSaleDTO = flashSaleMapper.toDto(flashSale);

        restFlashSaleMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(flashSaleDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllFlashSales() throws Exception {
        // Initialize the database
        insertedFlashSale = flashSaleRepository.saveAndFlush(flashSale);

        // Get all the flashSaleList
        restFlashSaleMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(flashSale.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].startTime").value(hasItem(DEFAULT_START_TIME.toString())))
            .andExpect(jsonPath("$.[*].endTime").value(hasItem(DEFAULT_END_TIME.toString())));
    }

    @Test
    @Transactional
    void getFlashSale() throws Exception {
        // Initialize the database
        insertedFlashSale = flashSaleRepository.saveAndFlush(flashSale);

        // Get the flashSale
        restFlashSaleMockMvc
            .perform(get(ENTITY_API_URL_ID, flashSale.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(flashSale.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.startTime").value(DEFAULT_START_TIME.toString()))
            .andExpect(jsonPath("$.endTime").value(DEFAULT_END_TIME.toString()));
    }

    @Test
    @Transactional
    void getNonExistingFlashSale() throws Exception {
        // Get the flashSale
        restFlashSaleMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingFlashSale() throws Exception {
        // Initialize the database
        insertedFlashSale = flashSaleRepository.saveAndFlush(flashSale);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the flashSale
        FlashSale updatedFlashSale = flashSaleRepository.findById(flashSale.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedFlashSale are not directly saved in db
        em.detach(updatedFlashSale);
        updatedFlashSale.name(UPDATED_NAME).startTime(UPDATED_START_TIME).endTime(UPDATED_END_TIME);
        FlashSaleDTO flashSaleDTO = flashSaleMapper.toDto(updatedFlashSale);

        restFlashSaleMockMvc
            .perform(
                put(ENTITY_API_URL_ID, flashSaleDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(flashSaleDTO))
            )
            .andExpect(status().isOk());

        // Validate the FlashSale in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedFlashSaleToMatchAllProperties(updatedFlashSale);
    }

    @Test
    @Transactional
    void putNonExistingFlashSale() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        flashSale.setId(longCount.incrementAndGet());

        // Create the FlashSale
        FlashSaleDTO flashSaleDTO = flashSaleMapper.toDto(flashSale);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFlashSaleMockMvc
            .perform(
                put(ENTITY_API_URL_ID, flashSaleDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(flashSaleDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the FlashSale in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchFlashSale() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        flashSale.setId(longCount.incrementAndGet());

        // Create the FlashSale
        FlashSaleDTO flashSaleDTO = flashSaleMapper.toDto(flashSale);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFlashSaleMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(flashSaleDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the FlashSale in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamFlashSale() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        flashSale.setId(longCount.incrementAndGet());

        // Create the FlashSale
        FlashSaleDTO flashSaleDTO = flashSaleMapper.toDto(flashSale);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFlashSaleMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(flashSaleDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the FlashSale in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateFlashSaleWithPatch() throws Exception {
        // Initialize the database
        insertedFlashSale = flashSaleRepository.saveAndFlush(flashSale);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the flashSale using partial update
        FlashSale partialUpdatedFlashSale = new FlashSale();
        partialUpdatedFlashSale.setId(flashSale.getId());

        partialUpdatedFlashSale.endTime(UPDATED_END_TIME);

        restFlashSaleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFlashSale.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedFlashSale))
            )
            .andExpect(status().isOk());

        // Validate the FlashSale in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertFlashSaleUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedFlashSale, flashSale),
            getPersistedFlashSale(flashSale)
        );
    }

    @Test
    @Transactional
    void fullUpdateFlashSaleWithPatch() throws Exception {
        // Initialize the database
        insertedFlashSale = flashSaleRepository.saveAndFlush(flashSale);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the flashSale using partial update
        FlashSale partialUpdatedFlashSale = new FlashSale();
        partialUpdatedFlashSale.setId(flashSale.getId());

        partialUpdatedFlashSale.name(UPDATED_NAME).startTime(UPDATED_START_TIME).endTime(UPDATED_END_TIME);

        restFlashSaleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFlashSale.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedFlashSale))
            )
            .andExpect(status().isOk());

        // Validate the FlashSale in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertFlashSaleUpdatableFieldsEquals(partialUpdatedFlashSale, getPersistedFlashSale(partialUpdatedFlashSale));
    }

    @Test
    @Transactional
    void patchNonExistingFlashSale() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        flashSale.setId(longCount.incrementAndGet());

        // Create the FlashSale
        FlashSaleDTO flashSaleDTO = flashSaleMapper.toDto(flashSale);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFlashSaleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, flashSaleDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(flashSaleDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the FlashSale in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchFlashSale() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        flashSale.setId(longCount.incrementAndGet());

        // Create the FlashSale
        FlashSaleDTO flashSaleDTO = flashSaleMapper.toDto(flashSale);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFlashSaleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(flashSaleDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the FlashSale in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamFlashSale() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        flashSale.setId(longCount.incrementAndGet());

        // Create the FlashSale
        FlashSaleDTO flashSaleDTO = flashSaleMapper.toDto(flashSale);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFlashSaleMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(flashSaleDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the FlashSale in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteFlashSale() throws Exception {
        // Initialize the database
        insertedFlashSale = flashSaleRepository.saveAndFlush(flashSale);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the flashSale
        restFlashSaleMockMvc
            .perform(delete(ENTITY_API_URL_ID, flashSale.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return flashSaleRepository.count();
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

    protected FlashSale getPersistedFlashSale(FlashSale flashSale) {
        return flashSaleRepository.findById(flashSale.getId()).orElseThrow();
    }

    protected void assertPersistedFlashSaleToMatchAllProperties(FlashSale expectedFlashSale) {
        assertFlashSaleAllPropertiesEquals(expectedFlashSale, getPersistedFlashSale(expectedFlashSale));
    }

    protected void assertPersistedFlashSaleToMatchUpdatableProperties(FlashSale expectedFlashSale) {
        assertFlashSaleAllUpdatablePropertiesEquals(expectedFlashSale, getPersistedFlashSale(expectedFlashSale));
    }
}
