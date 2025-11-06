package com.lumiere.app.web.rest;

import static com.lumiere.app.domain.StockMovementAsserts.*;
import static com.lumiere.app.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lumiere.app.IntegrationTest;
import com.lumiere.app.domain.StockMovement;
import com.lumiere.app.domain.enumeration.StockMovementReason;
import com.lumiere.app.repository.StockMovementRepository;
import com.lumiere.app.service.StockMovementService;
import com.lumiere.app.service.dto.StockMovementDTO;
import com.lumiere.app.service.mapper.StockMovementMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link StockMovementResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class StockMovementResourceIT {

    private static final Long DEFAULT_QUANTITY_CHANGE = 1L;
    private static final Long UPDATED_QUANTITY_CHANGE = 2L;

    private static final String DEFAULT_NOTE = "AAAAAAAAAA";
    private static final String UPDATED_NOTE = "BBBBBBBBBB";

    private static final StockMovementReason DEFAULT_REASON = StockMovementReason.SALE;
    private static final StockMovementReason UPDATED_REASON = StockMovementReason.RETURN;

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/stock-movements";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private StockMovementRepository stockMovementRepository;

    @Mock
    private StockMovementRepository stockMovementRepositoryMock;

    @Autowired
    private StockMovementMapper stockMovementMapper;

    @Mock
    private StockMovementService stockMovementServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restStockMovementMockMvc;

    private StockMovement stockMovement;

    private StockMovement insertedStockMovement;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static StockMovement createEntity() {
        return new StockMovement()
            .quantityChange(DEFAULT_QUANTITY_CHANGE)
            .note(DEFAULT_NOTE)
            .reason(DEFAULT_REASON)
            .createdAt(DEFAULT_CREATED_AT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static StockMovement createUpdatedEntity() {
        return new StockMovement()
            .quantityChange(UPDATED_QUANTITY_CHANGE)
            .note(UPDATED_NOTE)
            .reason(UPDATED_REASON)
            .createdAt(UPDATED_CREATED_AT);
    }

    @BeforeEach
    void initTest() {
        stockMovement = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedStockMovement != null) {
            stockMovementRepository.delete(insertedStockMovement);
            insertedStockMovement = null;
        }
    }

    @Test
    @Transactional
    void createStockMovement() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the StockMovement
        StockMovementDTO stockMovementDTO = stockMovementMapper.toDto(stockMovement);
        var returnedStockMovementDTO = om.readValue(
            restStockMovementMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(stockMovementDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            StockMovementDTO.class
        );

        // Validate the StockMovement in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedStockMovement = stockMovementMapper.toEntity(returnedStockMovementDTO);
        assertStockMovementUpdatableFieldsEquals(returnedStockMovement, getPersistedStockMovement(returnedStockMovement));

        insertedStockMovement = returnedStockMovement;
    }

    @Test
    @Transactional
    void createStockMovementWithExistingId() throws Exception {
        // Create the StockMovement with an existing ID
        stockMovement.setId(1L);
        StockMovementDTO stockMovementDTO = stockMovementMapper.toDto(stockMovement);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restStockMovementMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(stockMovementDTO)))
            .andExpect(status().isBadRequest());

        // Validate the StockMovement in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkQuantityChangeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        stockMovement.setQuantityChange(null);

        // Create the StockMovement, which fails.
        StockMovementDTO stockMovementDTO = stockMovementMapper.toDto(stockMovement);

        restStockMovementMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(stockMovementDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkReasonIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        stockMovement.setReason(null);

        // Create the StockMovement, which fails.
        StockMovementDTO stockMovementDTO = stockMovementMapper.toDto(stockMovement);

        restStockMovementMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(stockMovementDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCreatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        stockMovement.setCreatedAt(null);

        // Create the StockMovement, which fails.
        StockMovementDTO stockMovementDTO = stockMovementMapper.toDto(stockMovement);

        restStockMovementMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(stockMovementDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllStockMovements() throws Exception {
        // Initialize the database
        insertedStockMovement = stockMovementRepository.saveAndFlush(stockMovement);

        // Get all the stockMovementList
        restStockMovementMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(stockMovement.getId().intValue())))
            .andExpect(jsonPath("$.[*].quantityChange").value(hasItem(DEFAULT_QUANTITY_CHANGE.intValue())))
            .andExpect(jsonPath("$.[*].note").value(hasItem(DEFAULT_NOTE)))
            .andExpect(jsonPath("$.[*].reason").value(hasItem(DEFAULT_REASON.toString())))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllStockMovementsWithEagerRelationshipsIsEnabled() throws Exception {
        when(stockMovementServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restStockMovementMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(stockMovementServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllStockMovementsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(stockMovementServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restStockMovementMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(stockMovementRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getStockMovement() throws Exception {
        // Initialize the database
        insertedStockMovement = stockMovementRepository.saveAndFlush(stockMovement);

        // Get the stockMovement
        restStockMovementMockMvc
            .perform(get(ENTITY_API_URL_ID, stockMovement.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(stockMovement.getId().intValue()))
            .andExpect(jsonPath("$.quantityChange").value(DEFAULT_QUANTITY_CHANGE.intValue()))
            .andExpect(jsonPath("$.note").value(DEFAULT_NOTE))
            .andExpect(jsonPath("$.reason").value(DEFAULT_REASON.toString()))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()));
    }

    @Test
    @Transactional
    void getNonExistingStockMovement() throws Exception {
        // Get the stockMovement
        restStockMovementMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingStockMovement() throws Exception {
        // Initialize the database
        insertedStockMovement = stockMovementRepository.saveAndFlush(stockMovement);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the stockMovement
        StockMovement updatedStockMovement = stockMovementRepository.findById(stockMovement.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedStockMovement are not directly saved in db
        em.detach(updatedStockMovement);
        updatedStockMovement
            .quantityChange(UPDATED_QUANTITY_CHANGE)
            .note(UPDATED_NOTE)
            .reason(UPDATED_REASON)
            .createdAt(UPDATED_CREATED_AT);
        StockMovementDTO stockMovementDTO = stockMovementMapper.toDto(updatedStockMovement);

        restStockMovementMockMvc
            .perform(
                put(ENTITY_API_URL_ID, stockMovementDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(stockMovementDTO))
            )
            .andExpect(status().isOk());

        // Validate the StockMovement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedStockMovementToMatchAllProperties(updatedStockMovement);
    }

    @Test
    @Transactional
    void putNonExistingStockMovement() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        stockMovement.setId(longCount.incrementAndGet());

        // Create the StockMovement
        StockMovementDTO stockMovementDTO = stockMovementMapper.toDto(stockMovement);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restStockMovementMockMvc
            .perform(
                put(ENTITY_API_URL_ID, stockMovementDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(stockMovementDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the StockMovement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchStockMovement() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        stockMovement.setId(longCount.incrementAndGet());

        // Create the StockMovement
        StockMovementDTO stockMovementDTO = stockMovementMapper.toDto(stockMovement);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStockMovementMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(stockMovementDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the StockMovement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamStockMovement() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        stockMovement.setId(longCount.incrementAndGet());

        // Create the StockMovement
        StockMovementDTO stockMovementDTO = stockMovementMapper.toDto(stockMovement);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStockMovementMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(stockMovementDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the StockMovement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateStockMovementWithPatch() throws Exception {
        // Initialize the database
        insertedStockMovement = stockMovementRepository.saveAndFlush(stockMovement);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the stockMovement using partial update
        StockMovement partialUpdatedStockMovement = new StockMovement();
        partialUpdatedStockMovement.setId(stockMovement.getId());

        partialUpdatedStockMovement.quantityChange(UPDATED_QUANTITY_CHANGE).createdAt(UPDATED_CREATED_AT);

        restStockMovementMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedStockMovement.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedStockMovement))
            )
            .andExpect(status().isOk());

        // Validate the StockMovement in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertStockMovementUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedStockMovement, stockMovement),
            getPersistedStockMovement(stockMovement)
        );
    }

    @Test
    @Transactional
    void fullUpdateStockMovementWithPatch() throws Exception {
        // Initialize the database
        insertedStockMovement = stockMovementRepository.saveAndFlush(stockMovement);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the stockMovement using partial update
        StockMovement partialUpdatedStockMovement = new StockMovement();
        partialUpdatedStockMovement.setId(stockMovement.getId());

        partialUpdatedStockMovement
            .quantityChange(UPDATED_QUANTITY_CHANGE)
            .note(UPDATED_NOTE)
            .reason(UPDATED_REASON)
            .createdAt(UPDATED_CREATED_AT);

        restStockMovementMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedStockMovement.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedStockMovement))
            )
            .andExpect(status().isOk());

        // Validate the StockMovement in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertStockMovementUpdatableFieldsEquals(partialUpdatedStockMovement, getPersistedStockMovement(partialUpdatedStockMovement));
    }

    @Test
    @Transactional
    void patchNonExistingStockMovement() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        stockMovement.setId(longCount.incrementAndGet());

        // Create the StockMovement
        StockMovementDTO stockMovementDTO = stockMovementMapper.toDto(stockMovement);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restStockMovementMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, stockMovementDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(stockMovementDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the StockMovement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchStockMovement() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        stockMovement.setId(longCount.incrementAndGet());

        // Create the StockMovement
        StockMovementDTO stockMovementDTO = stockMovementMapper.toDto(stockMovement);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStockMovementMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(stockMovementDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the StockMovement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamStockMovement() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        stockMovement.setId(longCount.incrementAndGet());

        // Create the StockMovement
        StockMovementDTO stockMovementDTO = stockMovementMapper.toDto(stockMovement);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStockMovementMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(stockMovementDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the StockMovement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteStockMovement() throws Exception {
        // Initialize the database
        insertedStockMovement = stockMovementRepository.saveAndFlush(stockMovement);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the stockMovement
        restStockMovementMockMvc
            .perform(delete(ENTITY_API_URL_ID, stockMovement.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return stockMovementRepository.count();
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

    protected StockMovement getPersistedStockMovement(StockMovement stockMovement) {
        return stockMovementRepository.findById(stockMovement.getId()).orElseThrow();
    }

    protected void assertPersistedStockMovementToMatchAllProperties(StockMovement expectedStockMovement) {
        assertStockMovementAllPropertiesEquals(expectedStockMovement, getPersistedStockMovement(expectedStockMovement));
    }

    protected void assertPersistedStockMovementToMatchUpdatableProperties(StockMovement expectedStockMovement) {
        assertStockMovementAllUpdatablePropertiesEquals(expectedStockMovement, getPersistedStockMovement(expectedStockMovement));
    }
}
