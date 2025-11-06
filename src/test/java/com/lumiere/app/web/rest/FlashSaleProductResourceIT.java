package com.lumiere.app.web.rest;

import static com.lumiere.app.domain.FlashSaleProductAsserts.*;
import static com.lumiere.app.web.rest.TestUtil.createUpdateProxyForBean;
import static com.lumiere.app.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lumiere.app.IntegrationTest;
import com.lumiere.app.domain.FlashSaleProduct;
import com.lumiere.app.repository.FlashSaleProductRepository;
import com.lumiere.app.service.FlashSaleProductService;
import com.lumiere.app.service.dto.FlashSaleProductDTO;
import com.lumiere.app.service.mapper.FlashSaleProductMapper;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
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
 * Integration tests for the {@link FlashSaleProductResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class FlashSaleProductResourceIT {

    private static final BigDecimal DEFAULT_SALE_PRICE = new BigDecimal(0);
    private static final BigDecimal UPDATED_SALE_PRICE = new BigDecimal(1);

    private static final Integer DEFAULT_QUANTITY = 1;
    private static final Integer UPDATED_QUANTITY = 2;

    private static final Integer DEFAULT_SOLD = 1;
    private static final Integer UPDATED_SOLD = 2;

    private static final String ENTITY_API_URL = "/api/flash-sale-products";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private FlashSaleProductRepository flashSaleProductRepository;

    @Mock
    private FlashSaleProductRepository flashSaleProductRepositoryMock;

    @Autowired
    private FlashSaleProductMapper flashSaleProductMapper;

    @Mock
    private FlashSaleProductService flashSaleProductServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restFlashSaleProductMockMvc;

    private FlashSaleProduct flashSaleProduct;

    private FlashSaleProduct insertedFlashSaleProduct;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static FlashSaleProduct createEntity() {
        return new FlashSaleProduct().salePrice(DEFAULT_SALE_PRICE).quantity(DEFAULT_QUANTITY).sold(DEFAULT_SOLD);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static FlashSaleProduct createUpdatedEntity() {
        return new FlashSaleProduct().salePrice(UPDATED_SALE_PRICE).quantity(UPDATED_QUANTITY).sold(UPDATED_SOLD);
    }

    @BeforeEach
    void initTest() {
        flashSaleProduct = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedFlashSaleProduct != null) {
            flashSaleProductRepository.delete(insertedFlashSaleProduct);
            insertedFlashSaleProduct = null;
        }
    }

    @Test
    @Transactional
    void createFlashSaleProduct() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the FlashSaleProduct
        FlashSaleProductDTO flashSaleProductDTO = flashSaleProductMapper.toDto(flashSaleProduct);
        var returnedFlashSaleProductDTO = om.readValue(
            restFlashSaleProductMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(flashSaleProductDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            FlashSaleProductDTO.class
        );

        // Validate the FlashSaleProduct in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedFlashSaleProduct = flashSaleProductMapper.toEntity(returnedFlashSaleProductDTO);
        assertFlashSaleProductUpdatableFieldsEquals(returnedFlashSaleProduct, getPersistedFlashSaleProduct(returnedFlashSaleProduct));

        insertedFlashSaleProduct = returnedFlashSaleProduct;
    }

    @Test
    @Transactional
    void createFlashSaleProductWithExistingId() throws Exception {
        // Create the FlashSaleProduct with an existing ID
        flashSaleProduct.setId(1L);
        FlashSaleProductDTO flashSaleProductDTO = flashSaleProductMapper.toDto(flashSaleProduct);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restFlashSaleProductMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(flashSaleProductDTO)))
            .andExpect(status().isBadRequest());

        // Validate the FlashSaleProduct in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkSalePriceIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        flashSaleProduct.setSalePrice(null);

        // Create the FlashSaleProduct, which fails.
        FlashSaleProductDTO flashSaleProductDTO = flashSaleProductMapper.toDto(flashSaleProduct);

        restFlashSaleProductMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(flashSaleProductDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkQuantityIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        flashSaleProduct.setQuantity(null);

        // Create the FlashSaleProduct, which fails.
        FlashSaleProductDTO flashSaleProductDTO = flashSaleProductMapper.toDto(flashSaleProduct);

        restFlashSaleProductMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(flashSaleProductDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkSoldIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        flashSaleProduct.setSold(null);

        // Create the FlashSaleProduct, which fails.
        FlashSaleProductDTO flashSaleProductDTO = flashSaleProductMapper.toDto(flashSaleProduct);

        restFlashSaleProductMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(flashSaleProductDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllFlashSaleProducts() throws Exception {
        // Initialize the database
        insertedFlashSaleProduct = flashSaleProductRepository.saveAndFlush(flashSaleProduct);

        // Get all the flashSaleProductList
        restFlashSaleProductMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(flashSaleProduct.getId().intValue())))
            .andExpect(jsonPath("$.[*].salePrice").value(hasItem(sameNumber(DEFAULT_SALE_PRICE))))
            .andExpect(jsonPath("$.[*].quantity").value(hasItem(DEFAULT_QUANTITY)))
            .andExpect(jsonPath("$.[*].sold").value(hasItem(DEFAULT_SOLD)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllFlashSaleProductsWithEagerRelationshipsIsEnabled() throws Exception {
        when(flashSaleProductServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restFlashSaleProductMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(flashSaleProductServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllFlashSaleProductsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(flashSaleProductServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restFlashSaleProductMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(flashSaleProductRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getFlashSaleProduct() throws Exception {
        // Initialize the database
        insertedFlashSaleProduct = flashSaleProductRepository.saveAndFlush(flashSaleProduct);

        // Get the flashSaleProduct
        restFlashSaleProductMockMvc
            .perform(get(ENTITY_API_URL_ID, flashSaleProduct.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(flashSaleProduct.getId().intValue()))
            .andExpect(jsonPath("$.salePrice").value(sameNumber(DEFAULT_SALE_PRICE)))
            .andExpect(jsonPath("$.quantity").value(DEFAULT_QUANTITY))
            .andExpect(jsonPath("$.sold").value(DEFAULT_SOLD));
    }

    @Test
    @Transactional
    void getNonExistingFlashSaleProduct() throws Exception {
        // Get the flashSaleProduct
        restFlashSaleProductMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingFlashSaleProduct() throws Exception {
        // Initialize the database
        insertedFlashSaleProduct = flashSaleProductRepository.saveAndFlush(flashSaleProduct);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the flashSaleProduct
        FlashSaleProduct updatedFlashSaleProduct = flashSaleProductRepository.findById(flashSaleProduct.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedFlashSaleProduct are not directly saved in db
        em.detach(updatedFlashSaleProduct);
        updatedFlashSaleProduct.salePrice(UPDATED_SALE_PRICE).quantity(UPDATED_QUANTITY).sold(UPDATED_SOLD);
        FlashSaleProductDTO flashSaleProductDTO = flashSaleProductMapper.toDto(updatedFlashSaleProduct);

        restFlashSaleProductMockMvc
            .perform(
                put(ENTITY_API_URL_ID, flashSaleProductDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(flashSaleProductDTO))
            )
            .andExpect(status().isOk());

        // Validate the FlashSaleProduct in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedFlashSaleProductToMatchAllProperties(updatedFlashSaleProduct);
    }

    @Test
    @Transactional
    void putNonExistingFlashSaleProduct() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        flashSaleProduct.setId(longCount.incrementAndGet());

        // Create the FlashSaleProduct
        FlashSaleProductDTO flashSaleProductDTO = flashSaleProductMapper.toDto(flashSaleProduct);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFlashSaleProductMockMvc
            .perform(
                put(ENTITY_API_URL_ID, flashSaleProductDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(flashSaleProductDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the FlashSaleProduct in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchFlashSaleProduct() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        flashSaleProduct.setId(longCount.incrementAndGet());

        // Create the FlashSaleProduct
        FlashSaleProductDTO flashSaleProductDTO = flashSaleProductMapper.toDto(flashSaleProduct);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFlashSaleProductMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(flashSaleProductDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the FlashSaleProduct in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamFlashSaleProduct() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        flashSaleProduct.setId(longCount.incrementAndGet());

        // Create the FlashSaleProduct
        FlashSaleProductDTO flashSaleProductDTO = flashSaleProductMapper.toDto(flashSaleProduct);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFlashSaleProductMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(flashSaleProductDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the FlashSaleProduct in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateFlashSaleProductWithPatch() throws Exception {
        // Initialize the database
        insertedFlashSaleProduct = flashSaleProductRepository.saveAndFlush(flashSaleProduct);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the flashSaleProduct using partial update
        FlashSaleProduct partialUpdatedFlashSaleProduct = new FlashSaleProduct();
        partialUpdatedFlashSaleProduct.setId(flashSaleProduct.getId());

        partialUpdatedFlashSaleProduct.quantity(UPDATED_QUANTITY);

        restFlashSaleProductMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFlashSaleProduct.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedFlashSaleProduct))
            )
            .andExpect(status().isOk());

        // Validate the FlashSaleProduct in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertFlashSaleProductUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedFlashSaleProduct, flashSaleProduct),
            getPersistedFlashSaleProduct(flashSaleProduct)
        );
    }

    @Test
    @Transactional
    void fullUpdateFlashSaleProductWithPatch() throws Exception {
        // Initialize the database
        insertedFlashSaleProduct = flashSaleProductRepository.saveAndFlush(flashSaleProduct);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the flashSaleProduct using partial update
        FlashSaleProduct partialUpdatedFlashSaleProduct = new FlashSaleProduct();
        partialUpdatedFlashSaleProduct.setId(flashSaleProduct.getId());

        partialUpdatedFlashSaleProduct.salePrice(UPDATED_SALE_PRICE).quantity(UPDATED_QUANTITY).sold(UPDATED_SOLD);

        restFlashSaleProductMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFlashSaleProduct.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedFlashSaleProduct))
            )
            .andExpect(status().isOk());

        // Validate the FlashSaleProduct in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertFlashSaleProductUpdatableFieldsEquals(
            partialUpdatedFlashSaleProduct,
            getPersistedFlashSaleProduct(partialUpdatedFlashSaleProduct)
        );
    }

    @Test
    @Transactional
    void patchNonExistingFlashSaleProduct() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        flashSaleProduct.setId(longCount.incrementAndGet());

        // Create the FlashSaleProduct
        FlashSaleProductDTO flashSaleProductDTO = flashSaleProductMapper.toDto(flashSaleProduct);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFlashSaleProductMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, flashSaleProductDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(flashSaleProductDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the FlashSaleProduct in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchFlashSaleProduct() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        flashSaleProduct.setId(longCount.incrementAndGet());

        // Create the FlashSaleProduct
        FlashSaleProductDTO flashSaleProductDTO = flashSaleProductMapper.toDto(flashSaleProduct);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFlashSaleProductMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(flashSaleProductDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the FlashSaleProduct in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamFlashSaleProduct() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        flashSaleProduct.setId(longCount.incrementAndGet());

        // Create the FlashSaleProduct
        FlashSaleProductDTO flashSaleProductDTO = flashSaleProductMapper.toDto(flashSaleProduct);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFlashSaleProductMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(flashSaleProductDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the FlashSaleProduct in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteFlashSaleProduct() throws Exception {
        // Initialize the database
        insertedFlashSaleProduct = flashSaleProductRepository.saveAndFlush(flashSaleProduct);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the flashSaleProduct
        restFlashSaleProductMockMvc
            .perform(delete(ENTITY_API_URL_ID, flashSaleProduct.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return flashSaleProductRepository.count();
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

    protected FlashSaleProduct getPersistedFlashSaleProduct(FlashSaleProduct flashSaleProduct) {
        return flashSaleProductRepository.findById(flashSaleProduct.getId()).orElseThrow();
    }

    protected void assertPersistedFlashSaleProductToMatchAllProperties(FlashSaleProduct expectedFlashSaleProduct) {
        assertFlashSaleProductAllPropertiesEquals(expectedFlashSaleProduct, getPersistedFlashSaleProduct(expectedFlashSaleProduct));
    }

    protected void assertPersistedFlashSaleProductToMatchUpdatableProperties(FlashSaleProduct expectedFlashSaleProduct) {
        assertFlashSaleProductAllUpdatablePropertiesEquals(
            expectedFlashSaleProduct,
            getPersistedFlashSaleProduct(expectedFlashSaleProduct)
        );
    }
}
