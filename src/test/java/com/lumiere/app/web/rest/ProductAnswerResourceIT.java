package com.lumiere.app.web.rest;

import static com.lumiere.app.domain.ProductAnswerAsserts.*;
import static com.lumiere.app.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lumiere.app.IntegrationTest;
import com.lumiere.app.domain.ProductAnswer;
import com.lumiere.app.repository.ProductAnswerRepository;
import com.lumiere.app.service.ProductAnswerService;
import com.lumiere.app.service.dto.ProductAnswerDTO;
import com.lumiere.app.service.mapper.ProductAnswerMapper;
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
 * Integration tests for the {@link ProductAnswerResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class ProductAnswerResourceIT {

    private static final String DEFAULT_AUTHOR = "AAAAAAAAAA";
    private static final String UPDATED_AUTHOR = "BBBBBBBBBB";

    private static final String DEFAULT_ANSWER_TEXT = "AAAAAAAAAA";
    private static final String UPDATED_ANSWER_TEXT = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/product-answers";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ProductAnswerRepository productAnswerRepository;

    @Mock
    private ProductAnswerRepository productAnswerRepositoryMock;

    @Autowired
    private ProductAnswerMapper productAnswerMapper;

    @Mock
    private ProductAnswerService productAnswerServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restProductAnswerMockMvc;

    private ProductAnswer productAnswer;

    private ProductAnswer insertedProductAnswer;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ProductAnswer createEntity() {
        return new ProductAnswer().author(DEFAULT_AUTHOR).answerText(DEFAULT_ANSWER_TEXT).createdAt(DEFAULT_CREATED_AT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ProductAnswer createUpdatedEntity() {
        return new ProductAnswer().author(UPDATED_AUTHOR).answerText(UPDATED_ANSWER_TEXT).createdAt(UPDATED_CREATED_AT);
    }

    @BeforeEach
    void initTest() {
        productAnswer = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedProductAnswer != null) {
            productAnswerRepository.delete(insertedProductAnswer);
            insertedProductAnswer = null;
        }
    }

    @Test
    @Transactional
    void createProductAnswer() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the ProductAnswer
        ProductAnswerDTO productAnswerDTO = productAnswerMapper.toDto(productAnswer);
        var returnedProductAnswerDTO = om.readValue(
            restProductAnswerMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(productAnswerDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ProductAnswerDTO.class
        );

        // Validate the ProductAnswer in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedProductAnswer = productAnswerMapper.toEntity(returnedProductAnswerDTO);
        assertProductAnswerUpdatableFieldsEquals(returnedProductAnswer, getPersistedProductAnswer(returnedProductAnswer));

        insertedProductAnswer = returnedProductAnswer;
    }

    @Test
    @Transactional
    void createProductAnswerWithExistingId() throws Exception {
        // Create the ProductAnswer with an existing ID
        productAnswer.setId(1L);
        ProductAnswerDTO productAnswerDTO = productAnswerMapper.toDto(productAnswer);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restProductAnswerMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(productAnswerDTO)))
            .andExpect(status().isBadRequest());

        // Validate the ProductAnswer in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkAuthorIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        productAnswer.setAuthor(null);

        // Create the ProductAnswer, which fails.
        ProductAnswerDTO productAnswerDTO = productAnswerMapper.toDto(productAnswer);

        restProductAnswerMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(productAnswerDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCreatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        productAnswer.setCreatedAt(null);

        // Create the ProductAnswer, which fails.
        ProductAnswerDTO productAnswerDTO = productAnswerMapper.toDto(productAnswer);

        restProductAnswerMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(productAnswerDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllProductAnswers() throws Exception {
        // Initialize the database
        insertedProductAnswer = productAnswerRepository.saveAndFlush(productAnswer);

        // Get all the productAnswerList
        restProductAnswerMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(productAnswer.getId().intValue())))
            .andExpect(jsonPath("$.[*].author").value(hasItem(DEFAULT_AUTHOR)))
            .andExpect(jsonPath("$.[*].answerText").value(hasItem(DEFAULT_ANSWER_TEXT)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllProductAnswersWithEagerRelationshipsIsEnabled() throws Exception {
        when(productAnswerServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restProductAnswerMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(productAnswerServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllProductAnswersWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(productAnswerServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restProductAnswerMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(productAnswerRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getProductAnswer() throws Exception {
        // Initialize the database
        insertedProductAnswer = productAnswerRepository.saveAndFlush(productAnswer);

        // Get the productAnswer
        restProductAnswerMockMvc
            .perform(get(ENTITY_API_URL_ID, productAnswer.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(productAnswer.getId().intValue()))
            .andExpect(jsonPath("$.author").value(DEFAULT_AUTHOR))
            .andExpect(jsonPath("$.answerText").value(DEFAULT_ANSWER_TEXT))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()));
    }

    @Test
    @Transactional
    void getNonExistingProductAnswer() throws Exception {
        // Get the productAnswer
        restProductAnswerMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingProductAnswer() throws Exception {
        // Initialize the database
        insertedProductAnswer = productAnswerRepository.saveAndFlush(productAnswer);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the productAnswer
        ProductAnswer updatedProductAnswer = productAnswerRepository.findById(productAnswer.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedProductAnswer are not directly saved in db
        em.detach(updatedProductAnswer);
        updatedProductAnswer.author(UPDATED_AUTHOR).answerText(UPDATED_ANSWER_TEXT).createdAt(UPDATED_CREATED_AT);
        ProductAnswerDTO productAnswerDTO = productAnswerMapper.toDto(updatedProductAnswer);

        restProductAnswerMockMvc
            .perform(
                put(ENTITY_API_URL_ID, productAnswerDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(productAnswerDTO))
            )
            .andExpect(status().isOk());

        // Validate the ProductAnswer in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedProductAnswerToMatchAllProperties(updatedProductAnswer);
    }

    @Test
    @Transactional
    void putNonExistingProductAnswer() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        productAnswer.setId(longCount.incrementAndGet());

        // Create the ProductAnswer
        ProductAnswerDTO productAnswerDTO = productAnswerMapper.toDto(productAnswer);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProductAnswerMockMvc
            .perform(
                put(ENTITY_API_URL_ID, productAnswerDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(productAnswerDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProductAnswer in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchProductAnswer() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        productAnswer.setId(longCount.incrementAndGet());

        // Create the ProductAnswer
        ProductAnswerDTO productAnswerDTO = productAnswerMapper.toDto(productAnswer);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductAnswerMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(productAnswerDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProductAnswer in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamProductAnswer() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        productAnswer.setId(longCount.incrementAndGet());

        // Create the ProductAnswer
        ProductAnswerDTO productAnswerDTO = productAnswerMapper.toDto(productAnswer);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductAnswerMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(productAnswerDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ProductAnswer in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateProductAnswerWithPatch() throws Exception {
        // Initialize the database
        insertedProductAnswer = productAnswerRepository.saveAndFlush(productAnswer);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the productAnswer using partial update
        ProductAnswer partialUpdatedProductAnswer = new ProductAnswer();
        partialUpdatedProductAnswer.setId(productAnswer.getId());

        partialUpdatedProductAnswer.author(UPDATED_AUTHOR);

        restProductAnswerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProductAnswer.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedProductAnswer))
            )
            .andExpect(status().isOk());

        // Validate the ProductAnswer in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertProductAnswerUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedProductAnswer, productAnswer),
            getPersistedProductAnswer(productAnswer)
        );
    }

    @Test
    @Transactional
    void fullUpdateProductAnswerWithPatch() throws Exception {
        // Initialize the database
        insertedProductAnswer = productAnswerRepository.saveAndFlush(productAnswer);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the productAnswer using partial update
        ProductAnswer partialUpdatedProductAnswer = new ProductAnswer();
        partialUpdatedProductAnswer.setId(productAnswer.getId());

        partialUpdatedProductAnswer.author(UPDATED_AUTHOR).answerText(UPDATED_ANSWER_TEXT).createdAt(UPDATED_CREATED_AT);

        restProductAnswerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProductAnswer.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedProductAnswer))
            )
            .andExpect(status().isOk());

        // Validate the ProductAnswer in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertProductAnswerUpdatableFieldsEquals(partialUpdatedProductAnswer, getPersistedProductAnswer(partialUpdatedProductAnswer));
    }

    @Test
    @Transactional
    void patchNonExistingProductAnswer() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        productAnswer.setId(longCount.incrementAndGet());

        // Create the ProductAnswer
        ProductAnswerDTO productAnswerDTO = productAnswerMapper.toDto(productAnswer);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProductAnswerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, productAnswerDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(productAnswerDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProductAnswer in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchProductAnswer() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        productAnswer.setId(longCount.incrementAndGet());

        // Create the ProductAnswer
        ProductAnswerDTO productAnswerDTO = productAnswerMapper.toDto(productAnswer);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductAnswerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(productAnswerDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProductAnswer in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamProductAnswer() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        productAnswer.setId(longCount.incrementAndGet());

        // Create the ProductAnswer
        ProductAnswerDTO productAnswerDTO = productAnswerMapper.toDto(productAnswer);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductAnswerMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(productAnswerDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ProductAnswer in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteProductAnswer() throws Exception {
        // Initialize the database
        insertedProductAnswer = productAnswerRepository.saveAndFlush(productAnswer);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the productAnswer
        restProductAnswerMockMvc
            .perform(delete(ENTITY_API_URL_ID, productAnswer.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return productAnswerRepository.count();
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

    protected ProductAnswer getPersistedProductAnswer(ProductAnswer productAnswer) {
        return productAnswerRepository.findById(productAnswer.getId()).orElseThrow();
    }

    protected void assertPersistedProductAnswerToMatchAllProperties(ProductAnswer expectedProductAnswer) {
        assertProductAnswerAllPropertiesEquals(expectedProductAnswer, getPersistedProductAnswer(expectedProductAnswer));
    }

    protected void assertPersistedProductAnswerToMatchUpdatableProperties(ProductAnswer expectedProductAnswer) {
        assertProductAnswerAllUpdatablePropertiesEquals(expectedProductAnswer, getPersistedProductAnswer(expectedProductAnswer));
    }
}
