package com.lumiere.app.web.rest;

import static com.lumiere.app.domain.ProductVariantAsserts.*;
import static com.lumiere.app.web.rest.TestUtil.createUpdateProxyForBean;
import static com.lumiere.app.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lumiere.app.IntegrationTest;
import com.lumiere.app.domain.Product;
import com.lumiere.app.domain.ProductVariant;
import com.lumiere.app.repository.ProductVariantRepository;
import com.lumiere.app.service.ProductVariantService;
import com.lumiere.app.service.dto.ProductVariantDTO;
import com.lumiere.app.service.mapper.ProductVariantMapper;
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
 * Integration tests for the {@link ProductVariantResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class ProductVariantResourceIT {

    private static final String DEFAULT_SKU = "AAAAAAAAAA";
    private static final String UPDATED_SKU = "BBBBBBBBBB";

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_PRICE = new BigDecimal(0);
    private static final BigDecimal UPDATED_PRICE = new BigDecimal(1);
    private static final BigDecimal SMALLER_PRICE = new BigDecimal(0 - 1);

    private static final BigDecimal DEFAULT_COMPARE_AT_PRICE = new BigDecimal(0);
    private static final BigDecimal UPDATED_COMPARE_AT_PRICE = new BigDecimal(1);
    private static final BigDecimal SMALLER_COMPARE_AT_PRICE = new BigDecimal(0 - 1);

    private static final String DEFAULT_CURRENCY = "AAA";
    private static final String UPDATED_CURRENCY = "BBB";

    private static final Long DEFAULT_STOCK_QUANTITY = 0L;
    private static final Long UPDATED_STOCK_QUANTITY = 1L;
    private static final Long SMALLER_STOCK_QUANTITY = 0L - 1L;

    private static final Boolean DEFAULT_IS_DEFAULT = false;
    private static final Boolean UPDATED_IS_DEFAULT = true;

    private static final String DEFAULT_COLOR = "AAAAAAAAAA";
    private static final String UPDATED_COLOR = "BBBBBBBBBB";

    private static final String DEFAULT_SIZE = "AAAAAAAAAA";
    private static final String UPDATED_SIZE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/product-variants";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ProductVariantRepository productVariantRepository;

    @Mock
    private ProductVariantRepository productVariantRepositoryMock;

    @Autowired
    private ProductVariantMapper productVariantMapper;

    @Mock
    private ProductVariantService productVariantServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restProductVariantMockMvc;

    private ProductVariant productVariant;

    private ProductVariant insertedProductVariant;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ProductVariant createEntity() {
        return new ProductVariant()
            .sku(DEFAULT_SKU)
            .name(DEFAULT_NAME)
            .price(DEFAULT_PRICE)
            .compareAtPrice(DEFAULT_COMPARE_AT_PRICE)
            .currency(DEFAULT_CURRENCY)
            .stockQuantity(DEFAULT_STOCK_QUANTITY)
            .isDefault(DEFAULT_IS_DEFAULT)
            .color(DEFAULT_COLOR)
            .size(DEFAULT_SIZE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ProductVariant createUpdatedEntity() {
        return new ProductVariant()
            .sku(UPDATED_SKU)
            .name(UPDATED_NAME)
            .price(UPDATED_PRICE)
            .compareAtPrice(UPDATED_COMPARE_AT_PRICE)
            .currency(UPDATED_CURRENCY)
            .stockQuantity(UPDATED_STOCK_QUANTITY)
            .isDefault(UPDATED_IS_DEFAULT)
            .color(UPDATED_COLOR)
            .size(UPDATED_SIZE);
    }

    @BeforeEach
    void initTest() {
        productVariant = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedProductVariant != null) {
            productVariantRepository.delete(insertedProductVariant);
            insertedProductVariant = null;
        }
    }

    @Test
    @Transactional
    void createProductVariant() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the ProductVariant
        ProductVariantDTO productVariantDTO = productVariantMapper.toDto(productVariant);
        var returnedProductVariantDTO = om.readValue(
            restProductVariantMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(productVariantDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ProductVariantDTO.class
        );

        // Validate the ProductVariant in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedProductVariant = productVariantMapper.toEntity(returnedProductVariantDTO);
        assertProductVariantUpdatableFieldsEquals(returnedProductVariant, getPersistedProductVariant(returnedProductVariant));

        insertedProductVariant = returnedProductVariant;
    }

    @Test
    @Transactional
    void createProductVariantWithExistingId() throws Exception {
        // Create the ProductVariant with an existing ID
        productVariant.setId(1L);
        ProductVariantDTO productVariantDTO = productVariantMapper.toDto(productVariant);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restProductVariantMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(productVariantDTO)))
            .andExpect(status().isBadRequest());

        // Validate the ProductVariant in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkSkuIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        productVariant.setSku(null);

        // Create the ProductVariant, which fails.
        ProductVariantDTO productVariantDTO = productVariantMapper.toDto(productVariant);

        restProductVariantMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(productVariantDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        productVariant.setName(null);

        // Create the ProductVariant, which fails.
        ProductVariantDTO productVariantDTO = productVariantMapper.toDto(productVariant);

        restProductVariantMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(productVariantDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPriceIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        productVariant.setPrice(null);

        // Create the ProductVariant, which fails.
        ProductVariantDTO productVariantDTO = productVariantMapper.toDto(productVariant);

        restProductVariantMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(productVariantDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStockQuantityIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        productVariant.setStockQuantity(null);

        // Create the ProductVariant, which fails.
        ProductVariantDTO productVariantDTO = productVariantMapper.toDto(productVariant);

        restProductVariantMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(productVariantDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkIsDefaultIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        productVariant.setIsDefault(null);

        // Create the ProductVariant, which fails.
        ProductVariantDTO productVariantDTO = productVariantMapper.toDto(productVariant);

        restProductVariantMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(productVariantDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllProductVariants() throws Exception {
        // Initialize the database
        insertedProductVariant = productVariantRepository.saveAndFlush(productVariant);

        // Get all the productVariantList
        restProductVariantMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(productVariant.getId().intValue())))
            .andExpect(jsonPath("$.[*].sku").value(hasItem(DEFAULT_SKU)))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].price").value(hasItem(sameNumber(DEFAULT_PRICE))))
            .andExpect(jsonPath("$.[*].compareAtPrice").value(hasItem(sameNumber(DEFAULT_COMPARE_AT_PRICE))))
            .andExpect(jsonPath("$.[*].currency").value(hasItem(DEFAULT_CURRENCY)))
            .andExpect(jsonPath("$.[*].stockQuantity").value(hasItem(DEFAULT_STOCK_QUANTITY.intValue())))
            .andExpect(jsonPath("$.[*].isDefault").value(hasItem(DEFAULT_IS_DEFAULT)))
            .andExpect(jsonPath("$.[*].color").value(hasItem(DEFAULT_COLOR)))
            .andExpect(jsonPath("$.[*].size").value(hasItem(DEFAULT_SIZE)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllProductVariantsWithEagerRelationshipsIsEnabled() throws Exception {
        when(productVariantServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restProductVariantMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(productVariantServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllProductVariantsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(productVariantServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restProductVariantMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(productVariantRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getProductVariant() throws Exception {
        // Initialize the database
        insertedProductVariant = productVariantRepository.saveAndFlush(productVariant);

        // Get the productVariant
        restProductVariantMockMvc
            .perform(get(ENTITY_API_URL_ID, productVariant.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(productVariant.getId().intValue()))
            .andExpect(jsonPath("$.sku").value(DEFAULT_SKU))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.price").value(sameNumber(DEFAULT_PRICE)))
            .andExpect(jsonPath("$.compareAtPrice").value(sameNumber(DEFAULT_COMPARE_AT_PRICE)))
            .andExpect(jsonPath("$.currency").value(DEFAULT_CURRENCY))
            .andExpect(jsonPath("$.stockQuantity").value(DEFAULT_STOCK_QUANTITY.intValue()))
            .andExpect(jsonPath("$.isDefault").value(DEFAULT_IS_DEFAULT))
            .andExpect(jsonPath("$.color").value(DEFAULT_COLOR))
            .andExpect(jsonPath("$.size").value(DEFAULT_SIZE));
    }

    @Test
    @Transactional
    void getProductVariantsByIdFiltering() throws Exception {
        // Initialize the database
        insertedProductVariant = productVariantRepository.saveAndFlush(productVariant);

        Long id = productVariant.getId();

        defaultProductVariantFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultProductVariantFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultProductVariantFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllProductVariantsBySkuIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedProductVariant = productVariantRepository.saveAndFlush(productVariant);

        // Get all the productVariantList where sku equals to
        defaultProductVariantFiltering("sku.equals=" + DEFAULT_SKU, "sku.equals=" + UPDATED_SKU);
    }

    @Test
    @Transactional
    void getAllProductVariantsBySkuIsInShouldWork() throws Exception {
        // Initialize the database
        insertedProductVariant = productVariantRepository.saveAndFlush(productVariant);

        // Get all the productVariantList where sku in
        defaultProductVariantFiltering("sku.in=" + DEFAULT_SKU + "," + UPDATED_SKU, "sku.in=" + UPDATED_SKU);
    }

    @Test
    @Transactional
    void getAllProductVariantsBySkuIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedProductVariant = productVariantRepository.saveAndFlush(productVariant);

        // Get all the productVariantList where sku is not null
        defaultProductVariantFiltering("sku.specified=true", "sku.specified=false");
    }

    @Test
    @Transactional
    void getAllProductVariantsBySkuContainsSomething() throws Exception {
        // Initialize the database
        insertedProductVariant = productVariantRepository.saveAndFlush(productVariant);

        // Get all the productVariantList where sku contains
        defaultProductVariantFiltering("sku.contains=" + DEFAULT_SKU, "sku.contains=" + UPDATED_SKU);
    }

    @Test
    @Transactional
    void getAllProductVariantsBySkuNotContainsSomething() throws Exception {
        // Initialize the database
        insertedProductVariant = productVariantRepository.saveAndFlush(productVariant);

        // Get all the productVariantList where sku does not contain
        defaultProductVariantFiltering("sku.doesNotContain=" + UPDATED_SKU, "sku.doesNotContain=" + DEFAULT_SKU);
    }

    @Test
    @Transactional
    void getAllProductVariantsByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedProductVariant = productVariantRepository.saveAndFlush(productVariant);

        // Get all the productVariantList where name equals to
        defaultProductVariantFiltering("name.equals=" + DEFAULT_NAME, "name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllProductVariantsByNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedProductVariant = productVariantRepository.saveAndFlush(productVariant);

        // Get all the productVariantList where name in
        defaultProductVariantFiltering("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME, "name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllProductVariantsByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedProductVariant = productVariantRepository.saveAndFlush(productVariant);

        // Get all the productVariantList where name is not null
        defaultProductVariantFiltering("name.specified=true", "name.specified=false");
    }

    @Test
    @Transactional
    void getAllProductVariantsByNameContainsSomething() throws Exception {
        // Initialize the database
        insertedProductVariant = productVariantRepository.saveAndFlush(productVariant);

        // Get all the productVariantList where name contains
        defaultProductVariantFiltering("name.contains=" + DEFAULT_NAME, "name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllProductVariantsByNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedProductVariant = productVariantRepository.saveAndFlush(productVariant);

        // Get all the productVariantList where name does not contain
        defaultProductVariantFiltering("name.doesNotContain=" + UPDATED_NAME, "name.doesNotContain=" + DEFAULT_NAME);
    }

    @Test
    @Transactional
    void getAllProductVariantsByPriceIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedProductVariant = productVariantRepository.saveAndFlush(productVariant);

        // Get all the productVariantList where price equals to
        defaultProductVariantFiltering("price.equals=" + DEFAULT_PRICE, "price.equals=" + UPDATED_PRICE);
    }

    @Test
    @Transactional
    void getAllProductVariantsByPriceIsInShouldWork() throws Exception {
        // Initialize the database
        insertedProductVariant = productVariantRepository.saveAndFlush(productVariant);

        // Get all the productVariantList where price in
        defaultProductVariantFiltering("price.in=" + DEFAULT_PRICE + "," + UPDATED_PRICE, "price.in=" + UPDATED_PRICE);
    }

    @Test
    @Transactional
    void getAllProductVariantsByPriceIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedProductVariant = productVariantRepository.saveAndFlush(productVariant);

        // Get all the productVariantList where price is not null
        defaultProductVariantFiltering("price.specified=true", "price.specified=false");
    }

    @Test
    @Transactional
    void getAllProductVariantsByPriceIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedProductVariant = productVariantRepository.saveAndFlush(productVariant);

        // Get all the productVariantList where price is greater than or equal to
        defaultProductVariantFiltering("price.greaterThanOrEqual=" + DEFAULT_PRICE, "price.greaterThanOrEqual=" + UPDATED_PRICE);
    }

    @Test
    @Transactional
    void getAllProductVariantsByPriceIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedProductVariant = productVariantRepository.saveAndFlush(productVariant);

        // Get all the productVariantList where price is less than or equal to
        defaultProductVariantFiltering("price.lessThanOrEqual=" + DEFAULT_PRICE, "price.lessThanOrEqual=" + SMALLER_PRICE);
    }

    @Test
    @Transactional
    void getAllProductVariantsByPriceIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedProductVariant = productVariantRepository.saveAndFlush(productVariant);

        // Get all the productVariantList where price is less than
        defaultProductVariantFiltering("price.lessThan=" + UPDATED_PRICE, "price.lessThan=" + DEFAULT_PRICE);
    }

    @Test
    @Transactional
    void getAllProductVariantsByPriceIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedProductVariant = productVariantRepository.saveAndFlush(productVariant);

        // Get all the productVariantList where price is greater than
        defaultProductVariantFiltering("price.greaterThan=" + SMALLER_PRICE, "price.greaterThan=" + DEFAULT_PRICE);
    }

    @Test
    @Transactional
    void getAllProductVariantsByCompareAtPriceIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedProductVariant = productVariantRepository.saveAndFlush(productVariant);

        // Get all the productVariantList where compareAtPrice equals to
        defaultProductVariantFiltering(
            "compareAtPrice.equals=" + DEFAULT_COMPARE_AT_PRICE,
            "compareAtPrice.equals=" + UPDATED_COMPARE_AT_PRICE
        );
    }

    @Test
    @Transactional
    void getAllProductVariantsByCompareAtPriceIsInShouldWork() throws Exception {
        // Initialize the database
        insertedProductVariant = productVariantRepository.saveAndFlush(productVariant);

        // Get all the productVariantList where compareAtPrice in
        defaultProductVariantFiltering(
            "compareAtPrice.in=" + DEFAULT_COMPARE_AT_PRICE + "," + UPDATED_COMPARE_AT_PRICE,
            "compareAtPrice.in=" + UPDATED_COMPARE_AT_PRICE
        );
    }

    @Test
    @Transactional
    void getAllProductVariantsByCompareAtPriceIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedProductVariant = productVariantRepository.saveAndFlush(productVariant);

        // Get all the productVariantList where compareAtPrice is not null
        defaultProductVariantFiltering("compareAtPrice.specified=true", "compareAtPrice.specified=false");
    }

    @Test
    @Transactional
    void getAllProductVariantsByCompareAtPriceIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedProductVariant = productVariantRepository.saveAndFlush(productVariant);

        // Get all the productVariantList where compareAtPrice is greater than or equal to
        defaultProductVariantFiltering(
            "compareAtPrice.greaterThanOrEqual=" + DEFAULT_COMPARE_AT_PRICE,
            "compareAtPrice.greaterThanOrEqual=" + UPDATED_COMPARE_AT_PRICE
        );
    }

    @Test
    @Transactional
    void getAllProductVariantsByCompareAtPriceIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedProductVariant = productVariantRepository.saveAndFlush(productVariant);

        // Get all the productVariantList where compareAtPrice is less than or equal to
        defaultProductVariantFiltering(
            "compareAtPrice.lessThanOrEqual=" + DEFAULT_COMPARE_AT_PRICE,
            "compareAtPrice.lessThanOrEqual=" + SMALLER_COMPARE_AT_PRICE
        );
    }

    @Test
    @Transactional
    void getAllProductVariantsByCompareAtPriceIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedProductVariant = productVariantRepository.saveAndFlush(productVariant);

        // Get all the productVariantList where compareAtPrice is less than
        defaultProductVariantFiltering(
            "compareAtPrice.lessThan=" + UPDATED_COMPARE_AT_PRICE,
            "compareAtPrice.lessThan=" + DEFAULT_COMPARE_AT_PRICE
        );
    }

    @Test
    @Transactional
    void getAllProductVariantsByCompareAtPriceIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedProductVariant = productVariantRepository.saveAndFlush(productVariant);

        // Get all the productVariantList where compareAtPrice is greater than
        defaultProductVariantFiltering(
            "compareAtPrice.greaterThan=" + SMALLER_COMPARE_AT_PRICE,
            "compareAtPrice.greaterThan=" + DEFAULT_COMPARE_AT_PRICE
        );
    }

    @Test
    @Transactional
    void getAllProductVariantsByCurrencyIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedProductVariant = productVariantRepository.saveAndFlush(productVariant);

        // Get all the productVariantList where currency equals to
        defaultProductVariantFiltering("currency.equals=" + DEFAULT_CURRENCY, "currency.equals=" + UPDATED_CURRENCY);
    }

    @Test
    @Transactional
    void getAllProductVariantsByCurrencyIsInShouldWork() throws Exception {
        // Initialize the database
        insertedProductVariant = productVariantRepository.saveAndFlush(productVariant);

        // Get all the productVariantList where currency in
        defaultProductVariantFiltering("currency.in=" + DEFAULT_CURRENCY + "," + UPDATED_CURRENCY, "currency.in=" + UPDATED_CURRENCY);
    }

    @Test
    @Transactional
    void getAllProductVariantsByCurrencyIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedProductVariant = productVariantRepository.saveAndFlush(productVariant);

        // Get all the productVariantList where currency is not null
        defaultProductVariantFiltering("currency.specified=true", "currency.specified=false");
    }

    @Test
    @Transactional
    void getAllProductVariantsByCurrencyContainsSomething() throws Exception {
        // Initialize the database
        insertedProductVariant = productVariantRepository.saveAndFlush(productVariant);

        // Get all the productVariantList where currency contains
        defaultProductVariantFiltering("currency.contains=" + DEFAULT_CURRENCY, "currency.contains=" + UPDATED_CURRENCY);
    }

    @Test
    @Transactional
    void getAllProductVariantsByCurrencyNotContainsSomething() throws Exception {
        // Initialize the database
        insertedProductVariant = productVariantRepository.saveAndFlush(productVariant);

        // Get all the productVariantList where currency does not contain
        defaultProductVariantFiltering("currency.doesNotContain=" + UPDATED_CURRENCY, "currency.doesNotContain=" + DEFAULT_CURRENCY);
    }

    @Test
    @Transactional
    void getAllProductVariantsByStockQuantityIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedProductVariant = productVariantRepository.saveAndFlush(productVariant);

        // Get all the productVariantList where stockQuantity equals to
        defaultProductVariantFiltering("stockQuantity.equals=" + DEFAULT_STOCK_QUANTITY, "stockQuantity.equals=" + UPDATED_STOCK_QUANTITY);
    }

    @Test
    @Transactional
    void getAllProductVariantsByStockQuantityIsInShouldWork() throws Exception {
        // Initialize the database
        insertedProductVariant = productVariantRepository.saveAndFlush(productVariant);

        // Get all the productVariantList where stockQuantity in
        defaultProductVariantFiltering(
            "stockQuantity.in=" + DEFAULT_STOCK_QUANTITY + "," + UPDATED_STOCK_QUANTITY,
            "stockQuantity.in=" + UPDATED_STOCK_QUANTITY
        );
    }

    @Test
    @Transactional
    void getAllProductVariantsByStockQuantityIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedProductVariant = productVariantRepository.saveAndFlush(productVariant);

        // Get all the productVariantList where stockQuantity is not null
        defaultProductVariantFiltering("stockQuantity.specified=true", "stockQuantity.specified=false");
    }

    @Test
    @Transactional
    void getAllProductVariantsByStockQuantityIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedProductVariant = productVariantRepository.saveAndFlush(productVariant);

        // Get all the productVariantList where stockQuantity is greater than or equal to
        defaultProductVariantFiltering(
            "stockQuantity.greaterThanOrEqual=" + DEFAULT_STOCK_QUANTITY,
            "stockQuantity.greaterThanOrEqual=" + UPDATED_STOCK_QUANTITY
        );
    }

    @Test
    @Transactional
    void getAllProductVariantsByStockQuantityIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedProductVariant = productVariantRepository.saveAndFlush(productVariant);

        // Get all the productVariantList where stockQuantity is less than or equal to
        defaultProductVariantFiltering(
            "stockQuantity.lessThanOrEqual=" + DEFAULT_STOCK_QUANTITY,
            "stockQuantity.lessThanOrEqual=" + SMALLER_STOCK_QUANTITY
        );
    }

    @Test
    @Transactional
    void getAllProductVariantsByStockQuantityIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedProductVariant = productVariantRepository.saveAndFlush(productVariant);

        // Get all the productVariantList where stockQuantity is less than
        defaultProductVariantFiltering(
            "stockQuantity.lessThan=" + UPDATED_STOCK_QUANTITY,
            "stockQuantity.lessThan=" + DEFAULT_STOCK_QUANTITY
        );
    }

    @Test
    @Transactional
    void getAllProductVariantsByStockQuantityIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedProductVariant = productVariantRepository.saveAndFlush(productVariant);

        // Get all the productVariantList where stockQuantity is greater than
        defaultProductVariantFiltering(
            "stockQuantity.greaterThan=" + SMALLER_STOCK_QUANTITY,
            "stockQuantity.greaterThan=" + DEFAULT_STOCK_QUANTITY
        );
    }

    @Test
    @Transactional
    void getAllProductVariantsByIsDefaultIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedProductVariant = productVariantRepository.saveAndFlush(productVariant);

        // Get all the productVariantList where isDefault equals to
        defaultProductVariantFiltering("isDefault.equals=" + DEFAULT_IS_DEFAULT, "isDefault.equals=" + UPDATED_IS_DEFAULT);
    }

    @Test
    @Transactional
    void getAllProductVariantsByIsDefaultIsInShouldWork() throws Exception {
        // Initialize the database
        insertedProductVariant = productVariantRepository.saveAndFlush(productVariant);

        // Get all the productVariantList where isDefault in
        defaultProductVariantFiltering(
            "isDefault.in=" + DEFAULT_IS_DEFAULT + "," + UPDATED_IS_DEFAULT,
            "isDefault.in=" + UPDATED_IS_DEFAULT
        );
    }

    @Test
    @Transactional
    void getAllProductVariantsByIsDefaultIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedProductVariant = productVariantRepository.saveAndFlush(productVariant);

        // Get all the productVariantList where isDefault is not null
        defaultProductVariantFiltering("isDefault.specified=true", "isDefault.specified=false");
    }

    @Test
    @Transactional
    void getAllProductVariantsByColorIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedProductVariant = productVariantRepository.saveAndFlush(productVariant);

        // Get all the productVariantList where color equals to
        defaultProductVariantFiltering("color.equals=" + DEFAULT_COLOR, "color.equals=" + UPDATED_COLOR);
    }

    @Test
    @Transactional
    void getAllProductVariantsByColorIsInShouldWork() throws Exception {
        // Initialize the database
        insertedProductVariant = productVariantRepository.saveAndFlush(productVariant);

        // Get all the productVariantList where color in
        defaultProductVariantFiltering("color.in=" + DEFAULT_COLOR + "," + UPDATED_COLOR, "color.in=" + UPDATED_COLOR);
    }

    @Test
    @Transactional
    void getAllProductVariantsByColorIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedProductVariant = productVariantRepository.saveAndFlush(productVariant);

        // Get all the productVariantList where color is not null
        defaultProductVariantFiltering("color.specified=true", "color.specified=false");
    }

    @Test
    @Transactional
    void getAllProductVariantsByColorContainsSomething() throws Exception {
        // Initialize the database
        insertedProductVariant = productVariantRepository.saveAndFlush(productVariant);

        // Get all the productVariantList where color contains
        defaultProductVariantFiltering("color.contains=" + DEFAULT_COLOR, "color.contains=" + UPDATED_COLOR);
    }

    @Test
    @Transactional
    void getAllProductVariantsByColorNotContainsSomething() throws Exception {
        // Initialize the database
        insertedProductVariant = productVariantRepository.saveAndFlush(productVariant);

        // Get all the productVariantList where color does not contain
        defaultProductVariantFiltering("color.doesNotContain=" + UPDATED_COLOR, "color.doesNotContain=" + DEFAULT_COLOR);
    }

    @Test
    @Transactional
    void getAllProductVariantsBySizeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedProductVariant = productVariantRepository.saveAndFlush(productVariant);

        // Get all the productVariantList where size equals to
        defaultProductVariantFiltering("size.equals=" + DEFAULT_SIZE, "size.equals=" + UPDATED_SIZE);
    }

    @Test
    @Transactional
    void getAllProductVariantsBySizeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedProductVariant = productVariantRepository.saveAndFlush(productVariant);

        // Get all the productVariantList where size in
        defaultProductVariantFiltering("size.in=" + DEFAULT_SIZE + "," + UPDATED_SIZE, "size.in=" + UPDATED_SIZE);
    }

    @Test
    @Transactional
    void getAllProductVariantsBySizeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedProductVariant = productVariantRepository.saveAndFlush(productVariant);

        // Get all the productVariantList where size is not null
        defaultProductVariantFiltering("size.specified=true", "size.specified=false");
    }

    @Test
    @Transactional
    void getAllProductVariantsBySizeContainsSomething() throws Exception {
        // Initialize the database
        insertedProductVariant = productVariantRepository.saveAndFlush(productVariant);

        // Get all the productVariantList where size contains
        defaultProductVariantFiltering("size.contains=" + DEFAULT_SIZE, "size.contains=" + UPDATED_SIZE);
    }

    @Test
    @Transactional
    void getAllProductVariantsBySizeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedProductVariant = productVariantRepository.saveAndFlush(productVariant);

        // Get all the productVariantList where size does not contain
        defaultProductVariantFiltering("size.doesNotContain=" + UPDATED_SIZE, "size.doesNotContain=" + DEFAULT_SIZE);
    }

    @Test
    @Transactional
    void getAllProductVariantsByProductIsEqualToSomething() throws Exception {
        Product product;
        if (TestUtil.findAll(em, Product.class).isEmpty()) {
            productVariantRepository.saveAndFlush(productVariant);
            product = ProductResourceIT.createEntity();
        } else {
            product = TestUtil.findAll(em, Product.class).get(0);
        }
        em.persist(product);
        em.flush();
        productVariant.setProduct(product);
        productVariantRepository.saveAndFlush(productVariant);
        Long productId = product.getId();
        // Get all the productVariantList where product equals to productId
        defaultProductVariantShouldBeFound("productId.equals=" + productId);

        // Get all the productVariantList where product equals to (productId + 1)
        defaultProductVariantShouldNotBeFound("productId.equals=" + (productId + 1));
    }

    private void defaultProductVariantFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultProductVariantShouldBeFound(shouldBeFound);
        defaultProductVariantShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultProductVariantShouldBeFound(String filter) throws Exception {
        restProductVariantMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(productVariant.getId().intValue())))
            .andExpect(jsonPath("$.[*].sku").value(hasItem(DEFAULT_SKU)))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].price").value(hasItem(sameNumber(DEFAULT_PRICE))))
            .andExpect(jsonPath("$.[*].compareAtPrice").value(hasItem(sameNumber(DEFAULT_COMPARE_AT_PRICE))))
            .andExpect(jsonPath("$.[*].currency").value(hasItem(DEFAULT_CURRENCY)))
            .andExpect(jsonPath("$.[*].stockQuantity").value(hasItem(DEFAULT_STOCK_QUANTITY.intValue())))
            .andExpect(jsonPath("$.[*].isDefault").value(hasItem(DEFAULT_IS_DEFAULT)))
            .andExpect(jsonPath("$.[*].color").value(hasItem(DEFAULT_COLOR)))
            .andExpect(jsonPath("$.[*].size").value(hasItem(DEFAULT_SIZE)));

        // Check, that the count call also returns 1
        restProductVariantMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultProductVariantShouldNotBeFound(String filter) throws Exception {
        restProductVariantMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restProductVariantMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingProductVariant() throws Exception {
        // Get the productVariant
        restProductVariantMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingProductVariant() throws Exception {
        // Initialize the database
        insertedProductVariant = productVariantRepository.saveAndFlush(productVariant);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the productVariant
        ProductVariant updatedProductVariant = productVariantRepository.findById(productVariant.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedProductVariant are not directly saved in db
        em.detach(updatedProductVariant);
        updatedProductVariant
            .sku(UPDATED_SKU)
            .name(UPDATED_NAME)
            .price(UPDATED_PRICE)
            .compareAtPrice(UPDATED_COMPARE_AT_PRICE)
            .currency(UPDATED_CURRENCY)
            .stockQuantity(UPDATED_STOCK_QUANTITY)
            .isDefault(UPDATED_IS_DEFAULT)
            .color(UPDATED_COLOR)
            .size(UPDATED_SIZE);
        ProductVariantDTO productVariantDTO = productVariantMapper.toDto(updatedProductVariant);

        restProductVariantMockMvc
            .perform(
                put(ENTITY_API_URL_ID, productVariantDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(productVariantDTO))
            )
            .andExpect(status().isOk());

        // Validate the ProductVariant in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedProductVariantToMatchAllProperties(updatedProductVariant);
    }

    @Test
    @Transactional
    void putNonExistingProductVariant() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        productVariant.setId(longCount.incrementAndGet());

        // Create the ProductVariant
        ProductVariantDTO productVariantDTO = productVariantMapper.toDto(productVariant);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProductVariantMockMvc
            .perform(
                put(ENTITY_API_URL_ID, productVariantDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(productVariantDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProductVariant in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchProductVariant() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        productVariant.setId(longCount.incrementAndGet());

        // Create the ProductVariant
        ProductVariantDTO productVariantDTO = productVariantMapper.toDto(productVariant);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductVariantMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(productVariantDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProductVariant in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamProductVariant() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        productVariant.setId(longCount.incrementAndGet());

        // Create the ProductVariant
        ProductVariantDTO productVariantDTO = productVariantMapper.toDto(productVariant);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductVariantMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(productVariantDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ProductVariant in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateProductVariantWithPatch() throws Exception {
        // Initialize the database
        insertedProductVariant = productVariantRepository.saveAndFlush(productVariant);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the productVariant using partial update
        ProductVariant partialUpdatedProductVariant = new ProductVariant();
        partialUpdatedProductVariant.setId(productVariant.getId());

        partialUpdatedProductVariant
            .sku(UPDATED_SKU)
            .price(UPDATED_PRICE)
            .compareAtPrice(UPDATED_COMPARE_AT_PRICE)
            .isDefault(UPDATED_IS_DEFAULT)
            .size(UPDATED_SIZE);

        restProductVariantMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProductVariant.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedProductVariant))
            )
            .andExpect(status().isOk());

        // Validate the ProductVariant in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertProductVariantUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedProductVariant, productVariant),
            getPersistedProductVariant(productVariant)
        );
    }

    @Test
    @Transactional
    void fullUpdateProductVariantWithPatch() throws Exception {
        // Initialize the database
        insertedProductVariant = productVariantRepository.saveAndFlush(productVariant);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the productVariant using partial update
        ProductVariant partialUpdatedProductVariant = new ProductVariant();
        partialUpdatedProductVariant.setId(productVariant.getId());

        partialUpdatedProductVariant
            .sku(UPDATED_SKU)
            .name(UPDATED_NAME)
            .price(UPDATED_PRICE)
            .compareAtPrice(UPDATED_COMPARE_AT_PRICE)
            .currency(UPDATED_CURRENCY)
            .stockQuantity(UPDATED_STOCK_QUANTITY)
            .isDefault(UPDATED_IS_DEFAULT)
            .color(UPDATED_COLOR)
            .size(UPDATED_SIZE);

        restProductVariantMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProductVariant.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedProductVariant))
            )
            .andExpect(status().isOk());

        // Validate the ProductVariant in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertProductVariantUpdatableFieldsEquals(partialUpdatedProductVariant, getPersistedProductVariant(partialUpdatedProductVariant));
    }

    @Test
    @Transactional
    void patchNonExistingProductVariant() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        productVariant.setId(longCount.incrementAndGet());

        // Create the ProductVariant
        ProductVariantDTO productVariantDTO = productVariantMapper.toDto(productVariant);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProductVariantMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, productVariantDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(productVariantDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProductVariant in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchProductVariant() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        productVariant.setId(longCount.incrementAndGet());

        // Create the ProductVariant
        ProductVariantDTO productVariantDTO = productVariantMapper.toDto(productVariant);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductVariantMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(productVariantDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProductVariant in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamProductVariant() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        productVariant.setId(longCount.incrementAndGet());

        // Create the ProductVariant
        ProductVariantDTO productVariantDTO = productVariantMapper.toDto(productVariant);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductVariantMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(productVariantDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ProductVariant in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteProductVariant() throws Exception {
        // Initialize the database
        insertedProductVariant = productVariantRepository.saveAndFlush(productVariant);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the productVariant
        restProductVariantMockMvc
            .perform(delete(ENTITY_API_URL_ID, productVariant.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return productVariantRepository.count();
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

    protected ProductVariant getPersistedProductVariant(ProductVariant productVariant) {
        return productVariantRepository.findById(productVariant.getId()).orElseThrow();
    }

    protected void assertPersistedProductVariantToMatchAllProperties(ProductVariant expectedProductVariant) {
        assertProductVariantAllPropertiesEquals(expectedProductVariant, getPersistedProductVariant(expectedProductVariant));
    }

    protected void assertPersistedProductVariantToMatchUpdatableProperties(ProductVariant expectedProductVariant) {
        assertProductVariantAllUpdatablePropertiesEquals(expectedProductVariant, getPersistedProductVariant(expectedProductVariant));
    }
}
