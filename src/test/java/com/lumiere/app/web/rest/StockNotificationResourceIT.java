package com.lumiere.app.web.rest;

import static com.lumiere.app.domain.StockNotificationAsserts.*;
import static com.lumiere.app.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lumiere.app.IntegrationTest;
import com.lumiere.app.domain.StockNotification;
import com.lumiere.app.repository.StockNotificationRepository;
import com.lumiere.app.service.StockNotificationService;
import com.lumiere.app.service.dto.StockNotificationDTO;
import com.lumiere.app.service.mapper.StockNotificationMapper;
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
 * Integration tests for the {@link StockNotificationResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class StockNotificationResourceIT {

    private static final String DEFAULT_EMAIL = "AAAAAAAAAA";
    private static final String UPDATED_EMAIL = "BBBBBBBBBB";

    private static final Boolean DEFAULT_NOTIFIED = false;
    private static final Boolean UPDATED_NOTIFIED = true;

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/stock-notifications";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private StockNotificationRepository stockNotificationRepository;

    @Mock
    private StockNotificationRepository stockNotificationRepositoryMock;

    @Autowired
    private StockNotificationMapper stockNotificationMapper;

    @Mock
    private StockNotificationService stockNotificationServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restStockNotificationMockMvc;

    private StockNotification stockNotification;

    private StockNotification insertedStockNotification;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static StockNotification createEntity() {
        return new StockNotification().email(DEFAULT_EMAIL).notified(DEFAULT_NOTIFIED).createdAt(DEFAULT_CREATED_AT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static StockNotification createUpdatedEntity() {
        return new StockNotification().email(UPDATED_EMAIL).notified(UPDATED_NOTIFIED).createdAt(UPDATED_CREATED_AT);
    }

    @BeforeEach
    void initTest() {
        stockNotification = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedStockNotification != null) {
            stockNotificationRepository.delete(insertedStockNotification);
            insertedStockNotification = null;
        }
    }

    @Test
    @Transactional
    void createStockNotification() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the StockNotification
        StockNotificationDTO stockNotificationDTO = stockNotificationMapper.toDto(stockNotification);
        var returnedStockNotificationDTO = om.readValue(
            restStockNotificationMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(stockNotificationDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            StockNotificationDTO.class
        );

        // Validate the StockNotification in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedStockNotification = stockNotificationMapper.toEntity(returnedStockNotificationDTO);
        assertStockNotificationUpdatableFieldsEquals(returnedStockNotification, getPersistedStockNotification(returnedStockNotification));

        insertedStockNotification = returnedStockNotification;
    }

    @Test
    @Transactional
    void createStockNotificationWithExistingId() throws Exception {
        // Create the StockNotification with an existing ID
        stockNotification.setId(1L);
        StockNotificationDTO stockNotificationDTO = stockNotificationMapper.toDto(stockNotification);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restStockNotificationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(stockNotificationDTO)))
            .andExpect(status().isBadRequest());

        // Validate the StockNotification in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkEmailIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        stockNotification.setEmail(null);

        // Create the StockNotification, which fails.
        StockNotificationDTO stockNotificationDTO = stockNotificationMapper.toDto(stockNotification);

        restStockNotificationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(stockNotificationDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkNotifiedIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        stockNotification.setNotified(null);

        // Create the StockNotification, which fails.
        StockNotificationDTO stockNotificationDTO = stockNotificationMapper.toDto(stockNotification);

        restStockNotificationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(stockNotificationDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCreatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        stockNotification.setCreatedAt(null);

        // Create the StockNotification, which fails.
        StockNotificationDTO stockNotificationDTO = stockNotificationMapper.toDto(stockNotification);

        restStockNotificationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(stockNotificationDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllStockNotifications() throws Exception {
        // Initialize the database
        insertedStockNotification = stockNotificationRepository.saveAndFlush(stockNotification);

        // Get all the stockNotificationList
        restStockNotificationMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(stockNotification.getId().intValue())))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].notified").value(hasItem(DEFAULT_NOTIFIED)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllStockNotificationsWithEagerRelationshipsIsEnabled() throws Exception {
        when(stockNotificationServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restStockNotificationMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(stockNotificationServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllStockNotificationsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(stockNotificationServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restStockNotificationMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(stockNotificationRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getStockNotification() throws Exception {
        // Initialize the database
        insertedStockNotification = stockNotificationRepository.saveAndFlush(stockNotification);

        // Get the stockNotification
        restStockNotificationMockMvc
            .perform(get(ENTITY_API_URL_ID, stockNotification.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(stockNotification.getId().intValue()))
            .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL))
            .andExpect(jsonPath("$.notified").value(DEFAULT_NOTIFIED))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()));
    }

    @Test
    @Transactional
    void getNonExistingStockNotification() throws Exception {
        // Get the stockNotification
        restStockNotificationMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingStockNotification() throws Exception {
        // Initialize the database
        insertedStockNotification = stockNotificationRepository.saveAndFlush(stockNotification);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the stockNotification
        StockNotification updatedStockNotification = stockNotificationRepository.findById(stockNotification.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedStockNotification are not directly saved in db
        em.detach(updatedStockNotification);
        updatedStockNotification.email(UPDATED_EMAIL).notified(UPDATED_NOTIFIED).createdAt(UPDATED_CREATED_AT);
        StockNotificationDTO stockNotificationDTO = stockNotificationMapper.toDto(updatedStockNotification);

        restStockNotificationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, stockNotificationDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(stockNotificationDTO))
            )
            .andExpect(status().isOk());

        // Validate the StockNotification in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedStockNotificationToMatchAllProperties(updatedStockNotification);
    }

    @Test
    @Transactional
    void putNonExistingStockNotification() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        stockNotification.setId(longCount.incrementAndGet());

        // Create the StockNotification
        StockNotificationDTO stockNotificationDTO = stockNotificationMapper.toDto(stockNotification);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restStockNotificationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, stockNotificationDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(stockNotificationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the StockNotification in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchStockNotification() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        stockNotification.setId(longCount.incrementAndGet());

        // Create the StockNotification
        StockNotificationDTO stockNotificationDTO = stockNotificationMapper.toDto(stockNotification);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStockNotificationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(stockNotificationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the StockNotification in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamStockNotification() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        stockNotification.setId(longCount.incrementAndGet());

        // Create the StockNotification
        StockNotificationDTO stockNotificationDTO = stockNotificationMapper.toDto(stockNotification);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStockNotificationMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(stockNotificationDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the StockNotification in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateStockNotificationWithPatch() throws Exception {
        // Initialize the database
        insertedStockNotification = stockNotificationRepository.saveAndFlush(stockNotification);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the stockNotification using partial update
        StockNotification partialUpdatedStockNotification = new StockNotification();
        partialUpdatedStockNotification.setId(stockNotification.getId());

        partialUpdatedStockNotification.notified(UPDATED_NOTIFIED).createdAt(UPDATED_CREATED_AT);

        restStockNotificationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedStockNotification.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedStockNotification))
            )
            .andExpect(status().isOk());

        // Validate the StockNotification in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertStockNotificationUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedStockNotification, stockNotification),
            getPersistedStockNotification(stockNotification)
        );
    }

    @Test
    @Transactional
    void fullUpdateStockNotificationWithPatch() throws Exception {
        // Initialize the database
        insertedStockNotification = stockNotificationRepository.saveAndFlush(stockNotification);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the stockNotification using partial update
        StockNotification partialUpdatedStockNotification = new StockNotification();
        partialUpdatedStockNotification.setId(stockNotification.getId());

        partialUpdatedStockNotification.email(UPDATED_EMAIL).notified(UPDATED_NOTIFIED).createdAt(UPDATED_CREATED_AT);

        restStockNotificationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedStockNotification.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedStockNotification))
            )
            .andExpect(status().isOk());

        // Validate the StockNotification in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertStockNotificationUpdatableFieldsEquals(
            partialUpdatedStockNotification,
            getPersistedStockNotification(partialUpdatedStockNotification)
        );
    }

    @Test
    @Transactional
    void patchNonExistingStockNotification() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        stockNotification.setId(longCount.incrementAndGet());

        // Create the StockNotification
        StockNotificationDTO stockNotificationDTO = stockNotificationMapper.toDto(stockNotification);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restStockNotificationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, stockNotificationDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(stockNotificationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the StockNotification in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchStockNotification() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        stockNotification.setId(longCount.incrementAndGet());

        // Create the StockNotification
        StockNotificationDTO stockNotificationDTO = stockNotificationMapper.toDto(stockNotification);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStockNotificationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(stockNotificationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the StockNotification in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamStockNotification() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        stockNotification.setId(longCount.incrementAndGet());

        // Create the StockNotification
        StockNotificationDTO stockNotificationDTO = stockNotificationMapper.toDto(stockNotification);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStockNotificationMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(stockNotificationDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the StockNotification in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteStockNotification() throws Exception {
        // Initialize the database
        insertedStockNotification = stockNotificationRepository.saveAndFlush(stockNotification);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the stockNotification
        restStockNotificationMockMvc
            .perform(delete(ENTITY_API_URL_ID, stockNotification.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return stockNotificationRepository.count();
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

    protected StockNotification getPersistedStockNotification(StockNotification stockNotification) {
        return stockNotificationRepository.findById(stockNotification.getId()).orElseThrow();
    }

    protected void assertPersistedStockNotificationToMatchAllProperties(StockNotification expectedStockNotification) {
        assertStockNotificationAllPropertiesEquals(expectedStockNotification, getPersistedStockNotification(expectedStockNotification));
    }

    protected void assertPersistedStockNotificationToMatchUpdatableProperties(StockNotification expectedStockNotification) {
        assertStockNotificationAllUpdatablePropertiesEquals(
            expectedStockNotification,
            getPersistedStockNotification(expectedStockNotification)
        );
    }
}
