package com.lumiere.app.web.rest;

import static com.lumiere.app.domain.OrdersAsserts.*;
import static com.lumiere.app.web.rest.TestUtil.createUpdateProxyForBean;
import static com.lumiere.app.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lumiere.app.IntegrationTest;
import com.lumiere.app.domain.Customer;
import com.lumiere.app.domain.Orders;
import com.lumiere.app.domain.enumeration.OrderStatus;
import com.lumiere.app.domain.enumeration.PaymentStatus;
import com.lumiere.app.repository.OrdersRepository;
import com.lumiere.app.service.OrdersService;
import com.lumiere.app.service.dto.OrdersDTO;
import com.lumiere.app.service.mapper.OrdersMapper;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
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
 * Integration tests for the {@link OrdersResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class OrdersResourceIT {

    private static final String DEFAULT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CODE = "BBBBBBBBBB";

    private static final OrderStatus DEFAULT_STATUS = OrderStatus.PENDING;
    private static final OrderStatus UPDATED_STATUS = OrderStatus.CONFIRMED;

    private static final PaymentStatus DEFAULT_PAYMENT_STATUS = PaymentStatus.UNPAID;
    private static final PaymentStatus UPDATED_PAYMENT_STATUS = PaymentStatus.PAID;

    private static final BigDecimal DEFAULT_TOTAL_AMOUNT = new BigDecimal(0);
    private static final BigDecimal UPDATED_TOTAL_AMOUNT = new BigDecimal(1);
    private static final BigDecimal SMALLER_TOTAL_AMOUNT = new BigDecimal(0 - 1);

    private static final String DEFAULT_NOTE = "AAAAAAAAAA";
    private static final String UPDATED_NOTE = "BBBBBBBBBB";

    private static final String DEFAULT_PAYMENT_METHOD = "AAAAAAAAAA";
    private static final String UPDATED_PAYMENT_METHOD = "BBBBBBBBBB";

    private static final Instant DEFAULT_PLACED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_PLACED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Integer DEFAULT_REDEEMED_POINTS = 0;
    private static final Integer UPDATED_REDEEMED_POINTS = 1;
    private static final Integer SMALLER_REDEEMED_POINTS = 0 - 1;

    private static final String ENTITY_API_URL = "/api/orders";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private OrdersRepository ordersRepository;

    @Mock
    private OrdersRepository ordersRepositoryMock;

    @Autowired
    private OrdersMapper ordersMapper;

    @Mock
    private OrdersService ordersServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restOrdersMockMvc;

    private Orders orders;

    private Orders insertedOrders;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Orders createEntity() {
        return new Orders()
            .code(DEFAULT_CODE)
            .status(DEFAULT_STATUS)
            .paymentStatus(DEFAULT_PAYMENT_STATUS)
            .totalAmount(DEFAULT_TOTAL_AMOUNT)
            .note(DEFAULT_NOTE)
            .paymentMethod(DEFAULT_PAYMENT_METHOD)
            .placedAt(DEFAULT_PLACED_AT)
            .redeemedPoints(DEFAULT_REDEEMED_POINTS);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Orders createUpdatedEntity() {
        return new Orders()
            .code(UPDATED_CODE)
            .status(UPDATED_STATUS)
            .paymentStatus(UPDATED_PAYMENT_STATUS)
            .totalAmount(UPDATED_TOTAL_AMOUNT)
            .note(UPDATED_NOTE)
            .paymentMethod(UPDATED_PAYMENT_METHOD)
            .placedAt(UPDATED_PLACED_AT)
            .redeemedPoints(UPDATED_REDEEMED_POINTS);
    }

    @BeforeEach
    void initTest() {
        orders = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedOrders != null) {
            ordersRepository.delete(insertedOrders);
            insertedOrders = null;
        }
    }

    @Test
    @Transactional
    void createOrders() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Orders
        OrdersDTO ordersDTO = ordersMapper.toDto(orders);
        var returnedOrdersDTO = om.readValue(
            restOrdersMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ordersDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            OrdersDTO.class
        );

        // Validate the Orders in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedOrders = ordersMapper.toEntity(returnedOrdersDTO);
        assertOrdersUpdatableFieldsEquals(returnedOrders, getPersistedOrders(returnedOrders));

        insertedOrders = returnedOrders;
    }

    @Test
    @Transactional
    void createOrdersWithExistingId() throws Exception {
        // Create the Orders with an existing ID
        orders.setId(1L);
        OrdersDTO ordersDTO = ordersMapper.toDto(orders);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restOrdersMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ordersDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Orders in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        orders.setCode(null);

        // Create the Orders, which fails.
        OrdersDTO ordersDTO = ordersMapper.toDto(orders);

        restOrdersMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ordersDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        orders.setStatus(null);

        // Create the Orders, which fails.
        OrdersDTO ordersDTO = ordersMapper.toDto(orders);

        restOrdersMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ordersDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPaymentStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        orders.setPaymentStatus(null);

        // Create the Orders, which fails.
        OrdersDTO ordersDTO = ordersMapper.toDto(orders);

        restOrdersMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ordersDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTotalAmountIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        orders.setTotalAmount(null);

        // Create the Orders, which fails.
        OrdersDTO ordersDTO = ordersMapper.toDto(orders);

        restOrdersMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ordersDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPlacedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        orders.setPlacedAt(null);

        // Create the Orders, which fails.
        OrdersDTO ordersDTO = ordersMapper.toDto(orders);

        restOrdersMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ordersDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllOrders() throws Exception {
        // Initialize the database
        insertedOrders = ordersRepository.saveAndFlush(orders);

        // Get all the ordersList
        restOrdersMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(orders.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].paymentStatus").value(hasItem(DEFAULT_PAYMENT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].totalAmount").value(hasItem(sameNumber(DEFAULT_TOTAL_AMOUNT))))
            .andExpect(jsonPath("$.[*].note").value(hasItem(DEFAULT_NOTE)))
            .andExpect(jsonPath("$.[*].paymentMethod").value(hasItem(DEFAULT_PAYMENT_METHOD)))
            .andExpect(jsonPath("$.[*].placedAt").value(hasItem(DEFAULT_PLACED_AT.toString())))
            .andExpect(jsonPath("$.[*].redeemedPoints").value(hasItem(DEFAULT_REDEEMED_POINTS)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllOrdersWithEagerRelationshipsIsEnabled() throws Exception {
        when(ordersServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restOrdersMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(ordersServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllOrdersWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(ordersServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restOrdersMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(ordersRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getOrders() throws Exception {
        // Initialize the database
        insertedOrders = ordersRepository.saveAndFlush(orders);

        // Get the orders
        restOrdersMockMvc
            .perform(get(ENTITY_API_URL_ID, orders.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(orders.getId().intValue()))
            .andExpect(jsonPath("$.code").value(DEFAULT_CODE))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.paymentStatus").value(DEFAULT_PAYMENT_STATUS.toString()))
            .andExpect(jsonPath("$.totalAmount").value(sameNumber(DEFAULT_TOTAL_AMOUNT)))
            .andExpect(jsonPath("$.note").value(DEFAULT_NOTE))
            .andExpect(jsonPath("$.paymentMethod").value(DEFAULT_PAYMENT_METHOD))
            .andExpect(jsonPath("$.placedAt").value(DEFAULT_PLACED_AT.toString()))
            .andExpect(jsonPath("$.redeemedPoints").value(DEFAULT_REDEEMED_POINTS));
    }

    @Test
    @Transactional
    void getOrdersByIdFiltering() throws Exception {
        // Initialize the database
        insertedOrders = ordersRepository.saveAndFlush(orders);

        Long id = orders.getId();

        defaultOrdersFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultOrdersFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultOrdersFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllOrdersByCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedOrders = ordersRepository.saveAndFlush(orders);

        // Get all the ordersList where code equals to
        defaultOrdersFiltering("code.equals=" + DEFAULT_CODE, "code.equals=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllOrdersByCodeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedOrders = ordersRepository.saveAndFlush(orders);

        // Get all the ordersList where code in
        defaultOrdersFiltering("code.in=" + DEFAULT_CODE + "," + UPDATED_CODE, "code.in=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllOrdersByCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedOrders = ordersRepository.saveAndFlush(orders);

        // Get all the ordersList where code is not null
        defaultOrdersFiltering("code.specified=true", "code.specified=false");
    }

    @Test
    @Transactional
    void getAllOrdersByCodeContainsSomething() throws Exception {
        // Initialize the database
        insertedOrders = ordersRepository.saveAndFlush(orders);

        // Get all the ordersList where code contains
        defaultOrdersFiltering("code.contains=" + DEFAULT_CODE, "code.contains=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllOrdersByCodeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedOrders = ordersRepository.saveAndFlush(orders);

        // Get all the ordersList where code does not contain
        defaultOrdersFiltering("code.doesNotContain=" + UPDATED_CODE, "code.doesNotContain=" + DEFAULT_CODE);
    }

    @Test
    @Transactional
    void getAllOrdersByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedOrders = ordersRepository.saveAndFlush(orders);

        // Get all the ordersList where status equals to
        defaultOrdersFiltering("status.equals=" + DEFAULT_STATUS, "status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllOrdersByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        insertedOrders = ordersRepository.saveAndFlush(orders);

        // Get all the ordersList where status in
        defaultOrdersFiltering("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS, "status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllOrdersByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedOrders = ordersRepository.saveAndFlush(orders);

        // Get all the ordersList where status is not null
        defaultOrdersFiltering("status.specified=true", "status.specified=false");
    }

    @Test
    @Transactional
    void getAllOrdersByPaymentStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedOrders = ordersRepository.saveAndFlush(orders);

        // Get all the ordersList where paymentStatus equals to
        defaultOrdersFiltering("paymentStatus.equals=" + DEFAULT_PAYMENT_STATUS, "paymentStatus.equals=" + UPDATED_PAYMENT_STATUS);
    }

    @Test
    @Transactional
    void getAllOrdersByPaymentStatusIsInShouldWork() throws Exception {
        // Initialize the database
        insertedOrders = ordersRepository.saveAndFlush(orders);

        // Get all the ordersList where paymentStatus in
        defaultOrdersFiltering(
            "paymentStatus.in=" + DEFAULT_PAYMENT_STATUS + "," + UPDATED_PAYMENT_STATUS,
            "paymentStatus.in=" + UPDATED_PAYMENT_STATUS
        );
    }

    @Test
    @Transactional
    void getAllOrdersByPaymentStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedOrders = ordersRepository.saveAndFlush(orders);

        // Get all the ordersList where paymentStatus is not null
        defaultOrdersFiltering("paymentStatus.specified=true", "paymentStatus.specified=false");
    }

    @Test
    @Transactional
    void getAllOrdersByTotalAmountIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedOrders = ordersRepository.saveAndFlush(orders);

        // Get all the ordersList where totalAmount equals to
        defaultOrdersFiltering("totalAmount.equals=" + DEFAULT_TOTAL_AMOUNT, "totalAmount.equals=" + UPDATED_TOTAL_AMOUNT);
    }

    @Test
    @Transactional
    void getAllOrdersByTotalAmountIsInShouldWork() throws Exception {
        // Initialize the database
        insertedOrders = ordersRepository.saveAndFlush(orders);

        // Get all the ordersList where totalAmount in
        defaultOrdersFiltering(
            "totalAmount.in=" + DEFAULT_TOTAL_AMOUNT + "," + UPDATED_TOTAL_AMOUNT,
            "totalAmount.in=" + UPDATED_TOTAL_AMOUNT
        );
    }

    @Test
    @Transactional
    void getAllOrdersByTotalAmountIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedOrders = ordersRepository.saveAndFlush(orders);

        // Get all the ordersList where totalAmount is not null
        defaultOrdersFiltering("totalAmount.specified=true", "totalAmount.specified=false");
    }

    @Test
    @Transactional
    void getAllOrdersByTotalAmountIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedOrders = ordersRepository.saveAndFlush(orders);

        // Get all the ordersList where totalAmount is greater than or equal to
        defaultOrdersFiltering(
            "totalAmount.greaterThanOrEqual=" + DEFAULT_TOTAL_AMOUNT,
            "totalAmount.greaterThanOrEqual=" + UPDATED_TOTAL_AMOUNT
        );
    }

    @Test
    @Transactional
    void getAllOrdersByTotalAmountIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedOrders = ordersRepository.saveAndFlush(orders);

        // Get all the ordersList where totalAmount is less than or equal to
        defaultOrdersFiltering(
            "totalAmount.lessThanOrEqual=" + DEFAULT_TOTAL_AMOUNT,
            "totalAmount.lessThanOrEqual=" + SMALLER_TOTAL_AMOUNT
        );
    }

    @Test
    @Transactional
    void getAllOrdersByTotalAmountIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedOrders = ordersRepository.saveAndFlush(orders);

        // Get all the ordersList where totalAmount is less than
        defaultOrdersFiltering("totalAmount.lessThan=" + UPDATED_TOTAL_AMOUNT, "totalAmount.lessThan=" + DEFAULT_TOTAL_AMOUNT);
    }

    @Test
    @Transactional
    void getAllOrdersByTotalAmountIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedOrders = ordersRepository.saveAndFlush(orders);

        // Get all the ordersList where totalAmount is greater than
        defaultOrdersFiltering("totalAmount.greaterThan=" + SMALLER_TOTAL_AMOUNT, "totalAmount.greaterThan=" + DEFAULT_TOTAL_AMOUNT);
    }

    @Test
    @Transactional
    void getAllOrdersByNoteIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedOrders = ordersRepository.saveAndFlush(orders);

        // Get all the ordersList where note equals to
        defaultOrdersFiltering("note.equals=" + DEFAULT_NOTE, "note.equals=" + UPDATED_NOTE);
    }

    @Test
    @Transactional
    void getAllOrdersByNoteIsInShouldWork() throws Exception {
        // Initialize the database
        insertedOrders = ordersRepository.saveAndFlush(orders);

        // Get all the ordersList where note in
        defaultOrdersFiltering("note.in=" + DEFAULT_NOTE + "," + UPDATED_NOTE, "note.in=" + UPDATED_NOTE);
    }

    @Test
    @Transactional
    void getAllOrdersByNoteIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedOrders = ordersRepository.saveAndFlush(orders);

        // Get all the ordersList where note is not null
        defaultOrdersFiltering("note.specified=true", "note.specified=false");
    }

    @Test
    @Transactional
    void getAllOrdersByNoteContainsSomething() throws Exception {
        // Initialize the database
        insertedOrders = ordersRepository.saveAndFlush(orders);

        // Get all the ordersList where note contains
        defaultOrdersFiltering("note.contains=" + DEFAULT_NOTE, "note.contains=" + UPDATED_NOTE);
    }

    @Test
    @Transactional
    void getAllOrdersByNoteNotContainsSomething() throws Exception {
        // Initialize the database
        insertedOrders = ordersRepository.saveAndFlush(orders);

        // Get all the ordersList where note does not contain
        defaultOrdersFiltering("note.doesNotContain=" + UPDATED_NOTE, "note.doesNotContain=" + DEFAULT_NOTE);
    }

    @Test
    @Transactional
    void getAllOrdersByPaymentMethodIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedOrders = ordersRepository.saveAndFlush(orders);

        // Get all the ordersList where paymentMethod equals to
        defaultOrdersFiltering("paymentMethod.equals=" + DEFAULT_PAYMENT_METHOD, "paymentMethod.equals=" + UPDATED_PAYMENT_METHOD);
    }

    @Test
    @Transactional
    void getAllOrdersByPaymentMethodIsInShouldWork() throws Exception {
        // Initialize the database
        insertedOrders = ordersRepository.saveAndFlush(orders);

        // Get all the ordersList where paymentMethod in
        defaultOrdersFiltering(
            "paymentMethod.in=" + DEFAULT_PAYMENT_METHOD + "," + UPDATED_PAYMENT_METHOD,
            "paymentMethod.in=" + UPDATED_PAYMENT_METHOD
        );
    }

    @Test
    @Transactional
    void getAllOrdersByPaymentMethodIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedOrders = ordersRepository.saveAndFlush(orders);

        // Get all the ordersList where paymentMethod is not null
        defaultOrdersFiltering("paymentMethod.specified=true", "paymentMethod.specified=false");
    }

    @Test
    @Transactional
    void getAllOrdersByPaymentMethodContainsSomething() throws Exception {
        // Initialize the database
        insertedOrders = ordersRepository.saveAndFlush(orders);

        // Get all the ordersList where paymentMethod contains
        defaultOrdersFiltering("paymentMethod.contains=" + DEFAULT_PAYMENT_METHOD, "paymentMethod.contains=" + UPDATED_PAYMENT_METHOD);
    }

    @Test
    @Transactional
    void getAllOrdersByPaymentMethodNotContainsSomething() throws Exception {
        // Initialize the database
        insertedOrders = ordersRepository.saveAndFlush(orders);

        // Get all the ordersList where paymentMethod does not contain
        defaultOrdersFiltering(
            "paymentMethod.doesNotContain=" + UPDATED_PAYMENT_METHOD,
            "paymentMethod.doesNotContain=" + DEFAULT_PAYMENT_METHOD
        );
    }

    @Test
    @Transactional
    void getAllOrdersByPlacedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedOrders = ordersRepository.saveAndFlush(orders);

        // Get all the ordersList where placedAt equals to
        defaultOrdersFiltering("placedAt.equals=" + DEFAULT_PLACED_AT, "placedAt.equals=" + UPDATED_PLACED_AT);
    }

    @Test
    @Transactional
    void getAllOrdersByPlacedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedOrders = ordersRepository.saveAndFlush(orders);

        // Get all the ordersList where placedAt in
        defaultOrdersFiltering("placedAt.in=" + DEFAULT_PLACED_AT + "," + UPDATED_PLACED_AT, "placedAt.in=" + UPDATED_PLACED_AT);
    }

    @Test
    @Transactional
    void getAllOrdersByPlacedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedOrders = ordersRepository.saveAndFlush(orders);

        // Get all the ordersList where placedAt is not null
        defaultOrdersFiltering("placedAt.specified=true", "placedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllOrdersByRedeemedPointsIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedOrders = ordersRepository.saveAndFlush(orders);

        // Get all the ordersList where redeemedPoints equals to
        defaultOrdersFiltering("redeemedPoints.equals=" + DEFAULT_REDEEMED_POINTS, "redeemedPoints.equals=" + UPDATED_REDEEMED_POINTS);
    }

    @Test
    @Transactional
    void getAllOrdersByRedeemedPointsIsInShouldWork() throws Exception {
        // Initialize the database
        insertedOrders = ordersRepository.saveAndFlush(orders);

        // Get all the ordersList where redeemedPoints in
        defaultOrdersFiltering(
            "redeemedPoints.in=" + DEFAULT_REDEEMED_POINTS + "," + UPDATED_REDEEMED_POINTS,
            "redeemedPoints.in=" + UPDATED_REDEEMED_POINTS
        );
    }

    @Test
    @Transactional
    void getAllOrdersByRedeemedPointsIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedOrders = ordersRepository.saveAndFlush(orders);

        // Get all the ordersList where redeemedPoints is not null
        defaultOrdersFiltering("redeemedPoints.specified=true", "redeemedPoints.specified=false");
    }

    @Test
    @Transactional
    void getAllOrdersByRedeemedPointsIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedOrders = ordersRepository.saveAndFlush(orders);

        // Get all the ordersList where redeemedPoints is greater than or equal to
        defaultOrdersFiltering(
            "redeemedPoints.greaterThanOrEqual=" + DEFAULT_REDEEMED_POINTS,
            "redeemedPoints.greaterThanOrEqual=" + UPDATED_REDEEMED_POINTS
        );
    }

    @Test
    @Transactional
    void getAllOrdersByRedeemedPointsIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedOrders = ordersRepository.saveAndFlush(orders);

        // Get all the ordersList where redeemedPoints is less than or equal to
        defaultOrdersFiltering(
            "redeemedPoints.lessThanOrEqual=" + DEFAULT_REDEEMED_POINTS,
            "redeemedPoints.lessThanOrEqual=" + SMALLER_REDEEMED_POINTS
        );
    }

    @Test
    @Transactional
    void getAllOrdersByRedeemedPointsIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedOrders = ordersRepository.saveAndFlush(orders);

        // Get all the ordersList where redeemedPoints is less than
        defaultOrdersFiltering("redeemedPoints.lessThan=" + UPDATED_REDEEMED_POINTS, "redeemedPoints.lessThan=" + DEFAULT_REDEEMED_POINTS);
    }

    @Test
    @Transactional
    void getAllOrdersByRedeemedPointsIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedOrders = ordersRepository.saveAndFlush(orders);

        // Get all the ordersList where redeemedPoints is greater than
        defaultOrdersFiltering(
            "redeemedPoints.greaterThan=" + SMALLER_REDEEMED_POINTS,
            "redeemedPoints.greaterThan=" + DEFAULT_REDEEMED_POINTS
        );
    }

    @Test
    @Transactional
    void getAllOrdersByCustomerIsEqualToSomething() throws Exception {
        Customer customer;
        if (TestUtil.findAll(em, Customer.class).isEmpty()) {
            ordersRepository.saveAndFlush(orders);
            customer = CustomerResourceIT.createEntity();
        } else {
            customer = TestUtil.findAll(em, Customer.class).get(0);
        }
        em.persist(customer);
        em.flush();
        orders.setCustomer(customer);
        ordersRepository.saveAndFlush(orders);
        Long customerId = customer.getId();
        // Get all the ordersList where customer equals to customerId
        defaultOrdersShouldBeFound("customerId.equals=" + customerId);

        // Get all the ordersList where customer equals to (customerId + 1)
        defaultOrdersShouldNotBeFound("customerId.equals=" + (customerId + 1));
    }

    private void defaultOrdersFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultOrdersShouldBeFound(shouldBeFound);
        defaultOrdersShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultOrdersShouldBeFound(String filter) throws Exception {
        restOrdersMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(orders.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].paymentStatus").value(hasItem(DEFAULT_PAYMENT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].totalAmount").value(hasItem(sameNumber(DEFAULT_TOTAL_AMOUNT))))
            .andExpect(jsonPath("$.[*].note").value(hasItem(DEFAULT_NOTE)))
            .andExpect(jsonPath("$.[*].paymentMethod").value(hasItem(DEFAULT_PAYMENT_METHOD)))
            .andExpect(jsonPath("$.[*].placedAt").value(hasItem(DEFAULT_PLACED_AT.toString())))
            .andExpect(jsonPath("$.[*].redeemedPoints").value(hasItem(DEFAULT_REDEEMED_POINTS)));

        // Check, that the count call also returns 1
        restOrdersMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultOrdersShouldNotBeFound(String filter) throws Exception {
        restOrdersMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restOrdersMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingOrders() throws Exception {
        // Get the orders
        restOrdersMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingOrders() throws Exception {
        // Initialize the database
        insertedOrders = ordersRepository.saveAndFlush(orders);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the orders
        Orders updatedOrders = ordersRepository.findById(orders.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedOrders are not directly saved in db
        em.detach(updatedOrders);
        updatedOrders
            .code(UPDATED_CODE)
            .status(UPDATED_STATUS)
            .paymentStatus(UPDATED_PAYMENT_STATUS)
            .totalAmount(UPDATED_TOTAL_AMOUNT)
            .note(UPDATED_NOTE)
            .paymentMethod(UPDATED_PAYMENT_METHOD)
            .placedAt(UPDATED_PLACED_AT)
            .redeemedPoints(UPDATED_REDEEMED_POINTS);
        OrdersDTO ordersDTO = ordersMapper.toDto(updatedOrders);

        restOrdersMockMvc
            .perform(
                put(ENTITY_API_URL_ID, ordersDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ordersDTO))
            )
            .andExpect(status().isOk());

        // Validate the Orders in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedOrdersToMatchAllProperties(updatedOrders);
    }

    @Test
    @Transactional
    void putNonExistingOrders() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        orders.setId(longCount.incrementAndGet());

        // Create the Orders
        OrdersDTO ordersDTO = ordersMapper.toDto(orders);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restOrdersMockMvc
            .perform(
                put(ENTITY_API_URL_ID, ordersDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ordersDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Orders in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchOrders() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        orders.setId(longCount.incrementAndGet());

        // Create the Orders
        OrdersDTO ordersDTO = ordersMapper.toDto(orders);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOrdersMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(ordersDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Orders in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamOrders() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        orders.setId(longCount.incrementAndGet());

        // Create the Orders
        OrdersDTO ordersDTO = ordersMapper.toDto(orders);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOrdersMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ordersDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Orders in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateOrdersWithPatch() throws Exception {
        // Initialize the database
        insertedOrders = ordersRepository.saveAndFlush(orders);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the orders using partial update
        Orders partialUpdatedOrders = new Orders();
        partialUpdatedOrders.setId(orders.getId());

        partialUpdatedOrders.code(UPDATED_CODE).paymentMethod(UPDATED_PAYMENT_METHOD).redeemedPoints(UPDATED_REDEEMED_POINTS);

        restOrdersMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedOrders.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedOrders))
            )
            .andExpect(status().isOk());

        // Validate the Orders in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertOrdersUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedOrders, orders), getPersistedOrders(orders));
    }

    @Test
    @Transactional
    void fullUpdateOrdersWithPatch() throws Exception {
        // Initialize the database
        insertedOrders = ordersRepository.saveAndFlush(orders);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the orders using partial update
        Orders partialUpdatedOrders = new Orders();
        partialUpdatedOrders.setId(orders.getId());

        partialUpdatedOrders
            .code(UPDATED_CODE)
            .status(UPDATED_STATUS)
            .paymentStatus(UPDATED_PAYMENT_STATUS)
            .totalAmount(UPDATED_TOTAL_AMOUNT)
            .note(UPDATED_NOTE)
            .paymentMethod(UPDATED_PAYMENT_METHOD)
            .placedAt(UPDATED_PLACED_AT)
            .redeemedPoints(UPDATED_REDEEMED_POINTS);

        restOrdersMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedOrders.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedOrders))
            )
            .andExpect(status().isOk());

        // Validate the Orders in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertOrdersUpdatableFieldsEquals(partialUpdatedOrders, getPersistedOrders(partialUpdatedOrders));
    }

    @Test
    @Transactional
    void patchNonExistingOrders() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        orders.setId(longCount.incrementAndGet());

        // Create the Orders
        OrdersDTO ordersDTO = ordersMapper.toDto(orders);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restOrdersMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, ordersDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(ordersDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Orders in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchOrders() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        orders.setId(longCount.incrementAndGet());

        // Create the Orders
        OrdersDTO ordersDTO = ordersMapper.toDto(orders);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOrdersMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(ordersDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Orders in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamOrders() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        orders.setId(longCount.incrementAndGet());

        // Create the Orders
        OrdersDTO ordersDTO = ordersMapper.toDto(orders);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOrdersMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(ordersDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Orders in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteOrders() throws Exception {
        // Initialize the database
        insertedOrders = ordersRepository.saveAndFlush(orders);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the orders
        restOrdersMockMvc
            .perform(delete(ENTITY_API_URL_ID, orders.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return ordersRepository.count();
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

    protected Orders getPersistedOrders(Orders orders) {
        return ordersRepository.findById(orders.getId()).orElseThrow();
    }

    protected void assertPersistedOrdersToMatchAllProperties(Orders expectedOrders) {
        assertOrdersAllPropertiesEquals(expectedOrders, getPersistedOrders(expectedOrders));
    }

    protected void assertPersistedOrdersToMatchUpdatableProperties(Orders expectedOrders) {
        assertOrdersAllUpdatablePropertiesEquals(expectedOrders, getPersistedOrders(expectedOrders));
    }
}
