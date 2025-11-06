package com.lumiere.app.web.rest;

import static com.lumiere.app.domain.ProductQuestionAsserts.*;
import static com.lumiere.app.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lumiere.app.IntegrationTest;
import com.lumiere.app.domain.ProductQuestion;
import com.lumiere.app.domain.enumeration.QuestionStatus;
import com.lumiere.app.repository.ProductQuestionRepository;
import com.lumiere.app.service.ProductQuestionService;
import com.lumiere.app.service.dto.ProductQuestionDTO;
import com.lumiere.app.service.mapper.ProductQuestionMapper;
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
 * Integration tests for the {@link ProductQuestionResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class ProductQuestionResourceIT {

    private static final String DEFAULT_AUTHOR = "AAAAAAAAAA";
    private static final String UPDATED_AUTHOR = "BBBBBBBBBB";

    private static final String DEFAULT_QUESTION_TEXT = "AAAAAAAAAA";
    private static final String UPDATED_QUESTION_TEXT = "BBBBBBBBBB";

    private static final QuestionStatus DEFAULT_STATUS = QuestionStatus.PENDING;
    private static final QuestionStatus UPDATED_STATUS = QuestionStatus.ANSWERED;

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/product-questions";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ProductQuestionRepository productQuestionRepository;

    @Mock
    private ProductQuestionRepository productQuestionRepositoryMock;

    @Autowired
    private ProductQuestionMapper productQuestionMapper;

    @Mock
    private ProductQuestionService productQuestionServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restProductQuestionMockMvc;

    private ProductQuestion productQuestion;

    private ProductQuestion insertedProductQuestion;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ProductQuestion createEntity() {
        return new ProductQuestion()
            .author(DEFAULT_AUTHOR)
            .questionText(DEFAULT_QUESTION_TEXT)
            .status(DEFAULT_STATUS)
            .createdAt(DEFAULT_CREATED_AT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ProductQuestion createUpdatedEntity() {
        return new ProductQuestion()
            .author(UPDATED_AUTHOR)
            .questionText(UPDATED_QUESTION_TEXT)
            .status(UPDATED_STATUS)
            .createdAt(UPDATED_CREATED_AT);
    }

    @BeforeEach
    void initTest() {
        productQuestion = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedProductQuestion != null) {
            productQuestionRepository.delete(insertedProductQuestion);
            insertedProductQuestion = null;
        }
    }

    @Test
    @Transactional
    void createProductQuestion() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the ProductQuestion
        ProductQuestionDTO productQuestionDTO = productQuestionMapper.toDto(productQuestion);
        var returnedProductQuestionDTO = om.readValue(
            restProductQuestionMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(productQuestionDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ProductQuestionDTO.class
        );

        // Validate the ProductQuestion in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedProductQuestion = productQuestionMapper.toEntity(returnedProductQuestionDTO);
        assertProductQuestionUpdatableFieldsEquals(returnedProductQuestion, getPersistedProductQuestion(returnedProductQuestion));

        insertedProductQuestion = returnedProductQuestion;
    }

    @Test
    @Transactional
    void createProductQuestionWithExistingId() throws Exception {
        // Create the ProductQuestion with an existing ID
        productQuestion.setId(1L);
        ProductQuestionDTO productQuestionDTO = productQuestionMapper.toDto(productQuestion);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restProductQuestionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(productQuestionDTO)))
            .andExpect(status().isBadRequest());

        // Validate the ProductQuestion in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkAuthorIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        productQuestion.setAuthor(null);

        // Create the ProductQuestion, which fails.
        ProductQuestionDTO productQuestionDTO = productQuestionMapper.toDto(productQuestion);

        restProductQuestionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(productQuestionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        productQuestion.setStatus(null);

        // Create the ProductQuestion, which fails.
        ProductQuestionDTO productQuestionDTO = productQuestionMapper.toDto(productQuestion);

        restProductQuestionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(productQuestionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCreatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        productQuestion.setCreatedAt(null);

        // Create the ProductQuestion, which fails.
        ProductQuestionDTO productQuestionDTO = productQuestionMapper.toDto(productQuestion);

        restProductQuestionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(productQuestionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllProductQuestions() throws Exception {
        // Initialize the database
        insertedProductQuestion = productQuestionRepository.saveAndFlush(productQuestion);

        // Get all the productQuestionList
        restProductQuestionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(productQuestion.getId().intValue())))
            .andExpect(jsonPath("$.[*].author").value(hasItem(DEFAULT_AUTHOR)))
            .andExpect(jsonPath("$.[*].questionText").value(hasItem(DEFAULT_QUESTION_TEXT)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllProductQuestionsWithEagerRelationshipsIsEnabled() throws Exception {
        when(productQuestionServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restProductQuestionMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(productQuestionServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllProductQuestionsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(productQuestionServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restProductQuestionMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(productQuestionRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getProductQuestion() throws Exception {
        // Initialize the database
        insertedProductQuestion = productQuestionRepository.saveAndFlush(productQuestion);

        // Get the productQuestion
        restProductQuestionMockMvc
            .perform(get(ENTITY_API_URL_ID, productQuestion.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(productQuestion.getId().intValue()))
            .andExpect(jsonPath("$.author").value(DEFAULT_AUTHOR))
            .andExpect(jsonPath("$.questionText").value(DEFAULT_QUESTION_TEXT))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()));
    }

    @Test
    @Transactional
    void getNonExistingProductQuestion() throws Exception {
        // Get the productQuestion
        restProductQuestionMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingProductQuestion() throws Exception {
        // Initialize the database
        insertedProductQuestion = productQuestionRepository.saveAndFlush(productQuestion);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the productQuestion
        ProductQuestion updatedProductQuestion = productQuestionRepository.findById(productQuestion.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedProductQuestion are not directly saved in db
        em.detach(updatedProductQuestion);
        updatedProductQuestion
            .author(UPDATED_AUTHOR)
            .questionText(UPDATED_QUESTION_TEXT)
            .status(UPDATED_STATUS)
            .createdAt(UPDATED_CREATED_AT);
        ProductQuestionDTO productQuestionDTO = productQuestionMapper.toDto(updatedProductQuestion);

        restProductQuestionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, productQuestionDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(productQuestionDTO))
            )
            .andExpect(status().isOk());

        // Validate the ProductQuestion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedProductQuestionToMatchAllProperties(updatedProductQuestion);
    }

    @Test
    @Transactional
    void putNonExistingProductQuestion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        productQuestion.setId(longCount.incrementAndGet());

        // Create the ProductQuestion
        ProductQuestionDTO productQuestionDTO = productQuestionMapper.toDto(productQuestion);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProductQuestionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, productQuestionDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(productQuestionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProductQuestion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchProductQuestion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        productQuestion.setId(longCount.incrementAndGet());

        // Create the ProductQuestion
        ProductQuestionDTO productQuestionDTO = productQuestionMapper.toDto(productQuestion);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductQuestionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(productQuestionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProductQuestion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamProductQuestion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        productQuestion.setId(longCount.incrementAndGet());

        // Create the ProductQuestion
        ProductQuestionDTO productQuestionDTO = productQuestionMapper.toDto(productQuestion);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductQuestionMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(productQuestionDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ProductQuestion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateProductQuestionWithPatch() throws Exception {
        // Initialize the database
        insertedProductQuestion = productQuestionRepository.saveAndFlush(productQuestion);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the productQuestion using partial update
        ProductQuestion partialUpdatedProductQuestion = new ProductQuestion();
        partialUpdatedProductQuestion.setId(productQuestion.getId());

        partialUpdatedProductQuestion
            .author(UPDATED_AUTHOR)
            .questionText(UPDATED_QUESTION_TEXT)
            .status(UPDATED_STATUS)
            .createdAt(UPDATED_CREATED_AT);

        restProductQuestionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProductQuestion.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedProductQuestion))
            )
            .andExpect(status().isOk());

        // Validate the ProductQuestion in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertProductQuestionUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedProductQuestion, productQuestion),
            getPersistedProductQuestion(productQuestion)
        );
    }

    @Test
    @Transactional
    void fullUpdateProductQuestionWithPatch() throws Exception {
        // Initialize the database
        insertedProductQuestion = productQuestionRepository.saveAndFlush(productQuestion);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the productQuestion using partial update
        ProductQuestion partialUpdatedProductQuestion = new ProductQuestion();
        partialUpdatedProductQuestion.setId(productQuestion.getId());

        partialUpdatedProductQuestion
            .author(UPDATED_AUTHOR)
            .questionText(UPDATED_QUESTION_TEXT)
            .status(UPDATED_STATUS)
            .createdAt(UPDATED_CREATED_AT);

        restProductQuestionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProductQuestion.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedProductQuestion))
            )
            .andExpect(status().isOk());

        // Validate the ProductQuestion in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertProductQuestionUpdatableFieldsEquals(
            partialUpdatedProductQuestion,
            getPersistedProductQuestion(partialUpdatedProductQuestion)
        );
    }

    @Test
    @Transactional
    void patchNonExistingProductQuestion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        productQuestion.setId(longCount.incrementAndGet());

        // Create the ProductQuestion
        ProductQuestionDTO productQuestionDTO = productQuestionMapper.toDto(productQuestion);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProductQuestionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, productQuestionDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(productQuestionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProductQuestion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchProductQuestion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        productQuestion.setId(longCount.incrementAndGet());

        // Create the ProductQuestion
        ProductQuestionDTO productQuestionDTO = productQuestionMapper.toDto(productQuestion);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductQuestionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(productQuestionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProductQuestion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamProductQuestion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        productQuestion.setId(longCount.incrementAndGet());

        // Create the ProductQuestion
        ProductQuestionDTO productQuestionDTO = productQuestionMapper.toDto(productQuestion);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductQuestionMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(productQuestionDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ProductQuestion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteProductQuestion() throws Exception {
        // Initialize the database
        insertedProductQuestion = productQuestionRepository.saveAndFlush(productQuestion);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the productQuestion
        restProductQuestionMockMvc
            .perform(delete(ENTITY_API_URL_ID, productQuestion.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return productQuestionRepository.count();
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

    protected ProductQuestion getPersistedProductQuestion(ProductQuestion productQuestion) {
        return productQuestionRepository.findById(productQuestion.getId()).orElseThrow();
    }

    protected void assertPersistedProductQuestionToMatchAllProperties(ProductQuestion expectedProductQuestion) {
        assertProductQuestionAllPropertiesEquals(expectedProductQuestion, getPersistedProductQuestion(expectedProductQuestion));
    }

    protected void assertPersistedProductQuestionToMatchUpdatableProperties(ProductQuestion expectedProductQuestion) {
        assertProductQuestionAllUpdatablePropertiesEquals(expectedProductQuestion, getPersistedProductQuestion(expectedProductQuestion));
    }
}
