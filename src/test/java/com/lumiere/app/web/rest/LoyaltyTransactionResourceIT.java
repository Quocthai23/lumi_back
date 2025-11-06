package com.lumiere.app.web.rest;

import static com.lumiere.app.domain.LoyaltyTransactionAsserts.*;
import static com.lumiere.app.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lumiere.app.IntegrationTest;
import com.lumiere.app.domain.LoyaltyTransaction;
import com.lumiere.app.domain.enumeration.LoyaltyTransactionType;
import com.lumiere.app.repository.LoyaltyTransactionRepository;
import com.lumiere.app.service.LoyaltyTransactionService;
import com.lumiere.app.service.dto.LoyaltyTransactionDTO;
import com.lumiere.app.service.mapper.LoyaltyTransactionMapper;
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
 * Integration tests for the {@link LoyaltyTransactionResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class LoyaltyTransactionResourceIT {

    private static final LoyaltyTransactionType DEFAULT_TYPE = LoyaltyTransactionType.EARNED;
    private static final LoyaltyTransactionType UPDATED_TYPE = LoyaltyTransactionType.REDEEMED;

    private static final Integer DEFAULT_POINTS = 1;
    private static final Integer UPDATED_POINTS = 2;

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/loyalty-transactions";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private LoyaltyTransactionRepository loyaltyTransactionRepository;

    @Mock
    private LoyaltyTransactionRepository loyaltyTransactionRepositoryMock;

    @Autowired
    private LoyaltyTransactionMapper loyaltyTransactionMapper;

    @Mock
    private LoyaltyTransactionService loyaltyTransactionServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restLoyaltyTransactionMockMvc;

    private LoyaltyTransaction loyaltyTransaction;

    private LoyaltyTransaction insertedLoyaltyTransaction;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static LoyaltyTransaction createEntity() {
        return new LoyaltyTransaction()
            .type(DEFAULT_TYPE)
            .points(DEFAULT_POINTS)
            .description(DEFAULT_DESCRIPTION)
            .createdAt(DEFAULT_CREATED_AT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static LoyaltyTransaction createUpdatedEntity() {
        return new LoyaltyTransaction()
            .type(UPDATED_TYPE)
            .points(UPDATED_POINTS)
            .description(UPDATED_DESCRIPTION)
            .createdAt(UPDATED_CREATED_AT);
    }

    @BeforeEach
    void initTest() {
        loyaltyTransaction = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedLoyaltyTransaction != null) {
            loyaltyTransactionRepository.delete(insertedLoyaltyTransaction);
            insertedLoyaltyTransaction = null;
        }
    }

    @Test
    @Transactional
    void createLoyaltyTransaction() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the LoyaltyTransaction
        LoyaltyTransactionDTO loyaltyTransactionDTO = loyaltyTransactionMapper.toDto(loyaltyTransaction);
        var returnedLoyaltyTransactionDTO = om.readValue(
            restLoyaltyTransactionMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(loyaltyTransactionDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            LoyaltyTransactionDTO.class
        );

        // Validate the LoyaltyTransaction in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedLoyaltyTransaction = loyaltyTransactionMapper.toEntity(returnedLoyaltyTransactionDTO);
        assertLoyaltyTransactionUpdatableFieldsEquals(
            returnedLoyaltyTransaction,
            getPersistedLoyaltyTransaction(returnedLoyaltyTransaction)
        );

        insertedLoyaltyTransaction = returnedLoyaltyTransaction;
    }

    @Test
    @Transactional
    void createLoyaltyTransactionWithExistingId() throws Exception {
        // Create the LoyaltyTransaction with an existing ID
        loyaltyTransaction.setId(1L);
        LoyaltyTransactionDTO loyaltyTransactionDTO = loyaltyTransactionMapper.toDto(loyaltyTransaction);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restLoyaltyTransactionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(loyaltyTransactionDTO)))
            .andExpect(status().isBadRequest());

        // Validate the LoyaltyTransaction in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        loyaltyTransaction.setType(null);

        // Create the LoyaltyTransaction, which fails.
        LoyaltyTransactionDTO loyaltyTransactionDTO = loyaltyTransactionMapper.toDto(loyaltyTransaction);

        restLoyaltyTransactionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(loyaltyTransactionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPointsIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        loyaltyTransaction.setPoints(null);

        // Create the LoyaltyTransaction, which fails.
        LoyaltyTransactionDTO loyaltyTransactionDTO = loyaltyTransactionMapper.toDto(loyaltyTransaction);

        restLoyaltyTransactionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(loyaltyTransactionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCreatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        loyaltyTransaction.setCreatedAt(null);

        // Create the LoyaltyTransaction, which fails.
        LoyaltyTransactionDTO loyaltyTransactionDTO = loyaltyTransactionMapper.toDto(loyaltyTransaction);

        restLoyaltyTransactionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(loyaltyTransactionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllLoyaltyTransactions() throws Exception {
        // Initialize the database
        insertedLoyaltyTransaction = loyaltyTransactionRepository.saveAndFlush(loyaltyTransaction);

        // Get all the loyaltyTransactionList
        restLoyaltyTransactionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(loyaltyTransaction.getId().intValue())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].points").value(hasItem(DEFAULT_POINTS)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllLoyaltyTransactionsWithEagerRelationshipsIsEnabled() throws Exception {
        when(loyaltyTransactionServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restLoyaltyTransactionMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(loyaltyTransactionServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllLoyaltyTransactionsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(loyaltyTransactionServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restLoyaltyTransactionMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(loyaltyTransactionRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getLoyaltyTransaction() throws Exception {
        // Initialize the database
        insertedLoyaltyTransaction = loyaltyTransactionRepository.saveAndFlush(loyaltyTransaction);

        // Get the loyaltyTransaction
        restLoyaltyTransactionMockMvc
            .perform(get(ENTITY_API_URL_ID, loyaltyTransaction.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(loyaltyTransaction.getId().intValue()))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()))
            .andExpect(jsonPath("$.points").value(DEFAULT_POINTS))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()));
    }

    @Test
    @Transactional
    void getNonExistingLoyaltyTransaction() throws Exception {
        // Get the loyaltyTransaction
        restLoyaltyTransactionMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingLoyaltyTransaction() throws Exception {
        // Initialize the database
        insertedLoyaltyTransaction = loyaltyTransactionRepository.saveAndFlush(loyaltyTransaction);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the loyaltyTransaction
        LoyaltyTransaction updatedLoyaltyTransaction = loyaltyTransactionRepository.findById(loyaltyTransaction.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedLoyaltyTransaction are not directly saved in db
        em.detach(updatedLoyaltyTransaction);
        updatedLoyaltyTransaction.type(UPDATED_TYPE).points(UPDATED_POINTS).description(UPDATED_DESCRIPTION).createdAt(UPDATED_CREATED_AT);
        LoyaltyTransactionDTO loyaltyTransactionDTO = loyaltyTransactionMapper.toDto(updatedLoyaltyTransaction);

        restLoyaltyTransactionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, loyaltyTransactionDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(loyaltyTransactionDTO))
            )
            .andExpect(status().isOk());

        // Validate the LoyaltyTransaction in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedLoyaltyTransactionToMatchAllProperties(updatedLoyaltyTransaction);
    }

    @Test
    @Transactional
    void putNonExistingLoyaltyTransaction() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        loyaltyTransaction.setId(longCount.incrementAndGet());

        // Create the LoyaltyTransaction
        LoyaltyTransactionDTO loyaltyTransactionDTO = loyaltyTransactionMapper.toDto(loyaltyTransaction);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLoyaltyTransactionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, loyaltyTransactionDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(loyaltyTransactionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the LoyaltyTransaction in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchLoyaltyTransaction() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        loyaltyTransaction.setId(longCount.incrementAndGet());

        // Create the LoyaltyTransaction
        LoyaltyTransactionDTO loyaltyTransactionDTO = loyaltyTransactionMapper.toDto(loyaltyTransaction);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLoyaltyTransactionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(loyaltyTransactionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the LoyaltyTransaction in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamLoyaltyTransaction() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        loyaltyTransaction.setId(longCount.incrementAndGet());

        // Create the LoyaltyTransaction
        LoyaltyTransactionDTO loyaltyTransactionDTO = loyaltyTransactionMapper.toDto(loyaltyTransaction);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLoyaltyTransactionMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(loyaltyTransactionDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the LoyaltyTransaction in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateLoyaltyTransactionWithPatch() throws Exception {
        // Initialize the database
        insertedLoyaltyTransaction = loyaltyTransactionRepository.saveAndFlush(loyaltyTransaction);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the loyaltyTransaction using partial update
        LoyaltyTransaction partialUpdatedLoyaltyTransaction = new LoyaltyTransaction();
        partialUpdatedLoyaltyTransaction.setId(loyaltyTransaction.getId());

        partialUpdatedLoyaltyTransaction
            .type(UPDATED_TYPE)
            .points(UPDATED_POINTS)
            .description(UPDATED_DESCRIPTION)
            .createdAt(UPDATED_CREATED_AT);

        restLoyaltyTransactionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedLoyaltyTransaction.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedLoyaltyTransaction))
            )
            .andExpect(status().isOk());

        // Validate the LoyaltyTransaction in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertLoyaltyTransactionUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedLoyaltyTransaction, loyaltyTransaction),
            getPersistedLoyaltyTransaction(loyaltyTransaction)
        );
    }

    @Test
    @Transactional
    void fullUpdateLoyaltyTransactionWithPatch() throws Exception {
        // Initialize the database
        insertedLoyaltyTransaction = loyaltyTransactionRepository.saveAndFlush(loyaltyTransaction);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the loyaltyTransaction using partial update
        LoyaltyTransaction partialUpdatedLoyaltyTransaction = new LoyaltyTransaction();
        partialUpdatedLoyaltyTransaction.setId(loyaltyTransaction.getId());

        partialUpdatedLoyaltyTransaction
            .type(UPDATED_TYPE)
            .points(UPDATED_POINTS)
            .description(UPDATED_DESCRIPTION)
            .createdAt(UPDATED_CREATED_AT);

        restLoyaltyTransactionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedLoyaltyTransaction.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedLoyaltyTransaction))
            )
            .andExpect(status().isOk());

        // Validate the LoyaltyTransaction in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertLoyaltyTransactionUpdatableFieldsEquals(
            partialUpdatedLoyaltyTransaction,
            getPersistedLoyaltyTransaction(partialUpdatedLoyaltyTransaction)
        );
    }

    @Test
    @Transactional
    void patchNonExistingLoyaltyTransaction() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        loyaltyTransaction.setId(longCount.incrementAndGet());

        // Create the LoyaltyTransaction
        LoyaltyTransactionDTO loyaltyTransactionDTO = loyaltyTransactionMapper.toDto(loyaltyTransaction);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLoyaltyTransactionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, loyaltyTransactionDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(loyaltyTransactionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the LoyaltyTransaction in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchLoyaltyTransaction() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        loyaltyTransaction.setId(longCount.incrementAndGet());

        // Create the LoyaltyTransaction
        LoyaltyTransactionDTO loyaltyTransactionDTO = loyaltyTransactionMapper.toDto(loyaltyTransaction);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLoyaltyTransactionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(loyaltyTransactionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the LoyaltyTransaction in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamLoyaltyTransaction() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        loyaltyTransaction.setId(longCount.incrementAndGet());

        // Create the LoyaltyTransaction
        LoyaltyTransactionDTO loyaltyTransactionDTO = loyaltyTransactionMapper.toDto(loyaltyTransaction);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLoyaltyTransactionMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(loyaltyTransactionDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the LoyaltyTransaction in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteLoyaltyTransaction() throws Exception {
        // Initialize the database
        insertedLoyaltyTransaction = loyaltyTransactionRepository.saveAndFlush(loyaltyTransaction);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the loyaltyTransaction
        restLoyaltyTransactionMockMvc
            .perform(delete(ENTITY_API_URL_ID, loyaltyTransaction.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return loyaltyTransactionRepository.count();
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

    protected LoyaltyTransaction getPersistedLoyaltyTransaction(LoyaltyTransaction loyaltyTransaction) {
        return loyaltyTransactionRepository.findById(loyaltyTransaction.getId()).orElseThrow();
    }

    protected void assertPersistedLoyaltyTransactionToMatchAllProperties(LoyaltyTransaction expectedLoyaltyTransaction) {
        assertLoyaltyTransactionAllPropertiesEquals(expectedLoyaltyTransaction, getPersistedLoyaltyTransaction(expectedLoyaltyTransaction));
    }

    protected void assertPersistedLoyaltyTransactionToMatchUpdatableProperties(LoyaltyTransaction expectedLoyaltyTransaction) {
        assertLoyaltyTransactionAllUpdatablePropertiesEquals(
            expectedLoyaltyTransaction,
            getPersistedLoyaltyTransaction(expectedLoyaltyTransaction)
        );
    }
}
