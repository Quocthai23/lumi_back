package com.lumiere.app.web.rest;

import static com.lumiere.app.domain.CollectionAsserts.*;
import static com.lumiere.app.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lumiere.app.IntegrationTest;
import com.lumiere.app.domain.Collection;
import com.lumiere.app.repository.CollectionRepository;
import com.lumiere.app.service.CollectionService;
import com.lumiere.app.service.dto.CollectionDTO;
import com.lumiere.app.service.mapper.CollectionMapper;
import jakarta.persistence.EntityManager;
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
 * Integration tests for the {@link CollectionResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class CollectionResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_SLUG = "AAAAAAAAAA";
    private static final String UPDATED_SLUG = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String DEFAULT_IMAGE_URL = "AAAAAAAAAA";
    private static final String UPDATED_IMAGE_URL = "BBBBBBBBBB";

    private static final String DEFAULT_LOOK_IMAGE_URL = "AAAAAAAAAA";
    private static final String UPDATED_LOOK_IMAGE_URL = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/collections";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private CollectionRepository collectionRepository;

    @Mock
    private CollectionRepository collectionRepositoryMock;

    @Autowired
    private CollectionMapper collectionMapper;

    @Mock
    private CollectionService collectionServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCollectionMockMvc;

    private Collection collection;

    private Collection insertedCollection;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Collection createEntity() {
        return new Collection()
            .name(DEFAULT_NAME)
            .slug(DEFAULT_SLUG)
            .description(DEFAULT_DESCRIPTION)
            .imageUrl(DEFAULT_IMAGE_URL)
            .lookImageUrl(DEFAULT_LOOK_IMAGE_URL);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Collection createUpdatedEntity() {
        return new Collection()
            .name(UPDATED_NAME)
            .slug(UPDATED_SLUG)
            .description(UPDATED_DESCRIPTION)
            .imageUrl(UPDATED_IMAGE_URL)
            .lookImageUrl(UPDATED_LOOK_IMAGE_URL);
    }

    @BeforeEach
    void initTest() {
        collection = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedCollection != null) {
            collectionRepository.delete(insertedCollection);
            insertedCollection = null;
        }
    }

    @Test
    @Transactional
    void createCollection() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Collection
        CollectionDTO collectionDTO = collectionMapper.toDto(collection);
        var returnedCollectionDTO = om.readValue(
            restCollectionMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(collectionDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            CollectionDTO.class
        );

        // Validate the Collection in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedCollection = collectionMapper.toEntity(returnedCollectionDTO);
        assertCollectionUpdatableFieldsEquals(returnedCollection, getPersistedCollection(returnedCollection));

        insertedCollection = returnedCollection;
    }

    @Test
    @Transactional
    void createCollectionWithExistingId() throws Exception {
        // Create the Collection with an existing ID
        collection.setId(1L);
        CollectionDTO collectionDTO = collectionMapper.toDto(collection);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCollectionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(collectionDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Collection in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        collection.setName(null);

        // Create the Collection, which fails.
        CollectionDTO collectionDTO = collectionMapper.toDto(collection);

        restCollectionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(collectionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkSlugIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        collection.setSlug(null);

        // Create the Collection, which fails.
        CollectionDTO collectionDTO = collectionMapper.toDto(collection);

        restCollectionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(collectionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllCollections() throws Exception {
        // Initialize the database
        insertedCollection = collectionRepository.saveAndFlush(collection);

        // Get all the collectionList
        restCollectionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(collection.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].slug").value(hasItem(DEFAULT_SLUG)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].imageUrl").value(hasItem(DEFAULT_IMAGE_URL)))
            .andExpect(jsonPath("$.[*].lookImageUrl").value(hasItem(DEFAULT_LOOK_IMAGE_URL)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllCollectionsWithEagerRelationshipsIsEnabled() throws Exception {
        when(collectionServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restCollectionMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(collectionServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllCollectionsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(collectionServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restCollectionMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(collectionRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getCollection() throws Exception {
        // Initialize the database
        insertedCollection = collectionRepository.saveAndFlush(collection);

        // Get the collection
        restCollectionMockMvc
            .perform(get(ENTITY_API_URL_ID, collection.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(collection.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.slug").value(DEFAULT_SLUG))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.imageUrl").value(DEFAULT_IMAGE_URL))
            .andExpect(jsonPath("$.lookImageUrl").value(DEFAULT_LOOK_IMAGE_URL));
    }

    @Test
    @Transactional
    void getNonExistingCollection() throws Exception {
        // Get the collection
        restCollectionMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingCollection() throws Exception {
        // Initialize the database
        insertedCollection = collectionRepository.saveAndFlush(collection);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the collection
        Collection updatedCollection = collectionRepository.findById(collection.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedCollection are not directly saved in db
        em.detach(updatedCollection);
        updatedCollection
            .name(UPDATED_NAME)
            .slug(UPDATED_SLUG)
            .description(UPDATED_DESCRIPTION)
            .imageUrl(UPDATED_IMAGE_URL)
            .lookImageUrl(UPDATED_LOOK_IMAGE_URL);
        CollectionDTO collectionDTO = collectionMapper.toDto(updatedCollection);

        restCollectionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, collectionDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(collectionDTO))
            )
            .andExpect(status().isOk());

        // Validate the Collection in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedCollectionToMatchAllProperties(updatedCollection);
    }

    @Test
    @Transactional
    void putNonExistingCollection() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        collection.setId(longCount.incrementAndGet());

        // Create the Collection
        CollectionDTO collectionDTO = collectionMapper.toDto(collection);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCollectionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, collectionDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(collectionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Collection in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchCollection() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        collection.setId(longCount.incrementAndGet());

        // Create the Collection
        CollectionDTO collectionDTO = collectionMapper.toDto(collection);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCollectionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(collectionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Collection in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCollection() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        collection.setId(longCount.incrementAndGet());

        // Create the Collection
        CollectionDTO collectionDTO = collectionMapper.toDto(collection);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCollectionMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(collectionDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Collection in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateCollectionWithPatch() throws Exception {
        // Initialize the database
        insertedCollection = collectionRepository.saveAndFlush(collection);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the collection using partial update
        Collection partialUpdatedCollection = new Collection();
        partialUpdatedCollection.setId(collection.getId());

        partialUpdatedCollection
            .name(UPDATED_NAME)
            .slug(UPDATED_SLUG)
            .description(UPDATED_DESCRIPTION)
            .lookImageUrl(UPDATED_LOOK_IMAGE_URL);

        restCollectionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCollection.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCollection))
            )
            .andExpect(status().isOk());

        // Validate the Collection in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCollectionUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedCollection, collection),
            getPersistedCollection(collection)
        );
    }

    @Test
    @Transactional
    void fullUpdateCollectionWithPatch() throws Exception {
        // Initialize the database
        insertedCollection = collectionRepository.saveAndFlush(collection);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the collection using partial update
        Collection partialUpdatedCollection = new Collection();
        partialUpdatedCollection.setId(collection.getId());

        partialUpdatedCollection
            .name(UPDATED_NAME)
            .slug(UPDATED_SLUG)
            .description(UPDATED_DESCRIPTION)
            .imageUrl(UPDATED_IMAGE_URL)
            .lookImageUrl(UPDATED_LOOK_IMAGE_URL);

        restCollectionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCollection.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCollection))
            )
            .andExpect(status().isOk());

        // Validate the Collection in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCollectionUpdatableFieldsEquals(partialUpdatedCollection, getPersistedCollection(partialUpdatedCollection));
    }

    @Test
    @Transactional
    void patchNonExistingCollection() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        collection.setId(longCount.incrementAndGet());

        // Create the Collection
        CollectionDTO collectionDTO = collectionMapper.toDto(collection);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCollectionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, collectionDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(collectionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Collection in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCollection() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        collection.setId(longCount.incrementAndGet());

        // Create the Collection
        CollectionDTO collectionDTO = collectionMapper.toDto(collection);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCollectionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(collectionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Collection in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCollection() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        collection.setId(longCount.incrementAndGet());

        // Create the Collection
        CollectionDTO collectionDTO = collectionMapper.toDto(collection);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCollectionMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(collectionDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Collection in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteCollection() throws Exception {
        // Initialize the database
        insertedCollection = collectionRepository.saveAndFlush(collection);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the collection
        restCollectionMockMvc
            .perform(delete(ENTITY_API_URL_ID, collection.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return collectionRepository.count();
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

    protected Collection getPersistedCollection(Collection collection) {
        return collectionRepository.findById(collection.getId()).orElseThrow();
    }

    protected void assertPersistedCollectionToMatchAllProperties(Collection expectedCollection) {
        assertCollectionAllPropertiesEquals(expectedCollection, getPersistedCollection(expectedCollection));
    }

    protected void assertPersistedCollectionToMatchUpdatableProperties(Collection expectedCollection) {
        assertCollectionAllUpdatablePropertiesEquals(expectedCollection, getPersistedCollection(expectedCollection));
    }
}
