package com.lumiere.app.web.rest;

import static com.lumiere.app.domain.WarehouseAsserts.*;
import static com.lumiere.app.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lumiere.app.IntegrationTest;
import com.lumiere.app.domain.Warehouse;
import com.lumiere.app.repository.WarehouseRepository;
import com.lumiere.app.service.dto.WarehouseDTO;
import com.lumiere.app.service.mapper.WarehouseMapper;
import jakarta.persistence.EntityManager;
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
 * Integration tests for the {@link WarehouseResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class WarehouseResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_ADDRESS = "AAAAAAAAAA";
    private static final String UPDATED_ADDRESS = "BBBBBBBBBB";

    private static final Boolean DEFAULT_IS_ACTIVE = false;
    private static final Boolean UPDATED_IS_ACTIVE = true;

    private static final String ENTITY_API_URL = "/api/warehouses";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private WarehouseRepository warehouseRepository;

    @Autowired
    private WarehouseMapper warehouseMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restWarehouseMockMvc;

    private Warehouse warehouse;

    private Warehouse insertedWarehouse;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Warehouse createEntity() {
        return new Warehouse().name(DEFAULT_NAME).address(DEFAULT_ADDRESS).isActive(DEFAULT_IS_ACTIVE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Warehouse createUpdatedEntity() {
        return new Warehouse().name(UPDATED_NAME).address(UPDATED_ADDRESS).isActive(UPDATED_IS_ACTIVE);
    }

    @BeforeEach
    void initTest() {
        warehouse = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedWarehouse != null) {
            warehouseRepository.delete(insertedWarehouse);
            insertedWarehouse = null;
        }
    }

    @Test
    @Transactional
    void createWarehouse() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Warehouse
        WarehouseDTO warehouseDTO = warehouseMapper.toDto(warehouse);
        var returnedWarehouseDTO = om.readValue(
            restWarehouseMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(warehouseDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            WarehouseDTO.class
        );

        // Validate the Warehouse in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedWarehouse = warehouseMapper.toEntity(returnedWarehouseDTO);
        assertWarehouseUpdatableFieldsEquals(returnedWarehouse, getPersistedWarehouse(returnedWarehouse));

        insertedWarehouse = returnedWarehouse;
    }

    @Test
    @Transactional
    void createWarehouseWithExistingId() throws Exception {
        // Create the Warehouse with an existing ID
        warehouse.setId(1L);
        WarehouseDTO warehouseDTO = warehouseMapper.toDto(warehouse);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restWarehouseMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(warehouseDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Warehouse in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        warehouse.setName(null);

        // Create the Warehouse, which fails.
        WarehouseDTO warehouseDTO = warehouseMapper.toDto(warehouse);

        restWarehouseMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(warehouseDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkIsActiveIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        warehouse.setIsActive(null);

        // Create the Warehouse, which fails.
        WarehouseDTO warehouseDTO = warehouseMapper.toDto(warehouse);

        restWarehouseMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(warehouseDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllWarehouses() throws Exception {
        // Initialize the database
        insertedWarehouse = warehouseRepository.saveAndFlush(warehouse);

        // Get all the warehouseList
        restWarehouseMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(warehouse.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].address").value(hasItem(DEFAULT_ADDRESS)))
            .andExpect(jsonPath("$.[*].isActive").value(hasItem(DEFAULT_IS_ACTIVE)));
    }

    @Test
    @Transactional
    void getWarehouse() throws Exception {
        // Initialize the database
        insertedWarehouse = warehouseRepository.saveAndFlush(warehouse);

        // Get the warehouse
        restWarehouseMockMvc
            .perform(get(ENTITY_API_URL_ID, warehouse.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(warehouse.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.address").value(DEFAULT_ADDRESS))
            .andExpect(jsonPath("$.isActive").value(DEFAULT_IS_ACTIVE));
    }

    @Test
    @Transactional
    void getWarehousesByIdFiltering() throws Exception {
        // Initialize the database
        insertedWarehouse = warehouseRepository.saveAndFlush(warehouse);

        Long id = warehouse.getId();

        defaultWarehouseFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultWarehouseFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultWarehouseFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllWarehousesByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedWarehouse = warehouseRepository.saveAndFlush(warehouse);

        // Get all the warehouseList where name equals to
        defaultWarehouseFiltering("name.equals=" + DEFAULT_NAME, "name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllWarehousesByNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedWarehouse = warehouseRepository.saveAndFlush(warehouse);

        // Get all the warehouseList where name in
        defaultWarehouseFiltering("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME, "name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllWarehousesByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedWarehouse = warehouseRepository.saveAndFlush(warehouse);

        // Get all the warehouseList where name is not null
        defaultWarehouseFiltering("name.specified=true", "name.specified=false");
    }

    @Test
    @Transactional
    void getAllWarehousesByNameContainsSomething() throws Exception {
        // Initialize the database
        insertedWarehouse = warehouseRepository.saveAndFlush(warehouse);

        // Get all the warehouseList where name contains
        defaultWarehouseFiltering("name.contains=" + DEFAULT_NAME, "name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllWarehousesByNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedWarehouse = warehouseRepository.saveAndFlush(warehouse);

        // Get all the warehouseList where name does not contain
        defaultWarehouseFiltering("name.doesNotContain=" + UPDATED_NAME, "name.doesNotContain=" + DEFAULT_NAME);
    }

    @Test
    @Transactional
    void getAllWarehousesByAddressIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedWarehouse = warehouseRepository.saveAndFlush(warehouse);

        // Get all the warehouseList where address equals to
        defaultWarehouseFiltering("address.equals=" + DEFAULT_ADDRESS, "address.equals=" + UPDATED_ADDRESS);
    }

    @Test
    @Transactional
    void getAllWarehousesByAddressIsInShouldWork() throws Exception {
        // Initialize the database
        insertedWarehouse = warehouseRepository.saveAndFlush(warehouse);

        // Get all the warehouseList where address in
        defaultWarehouseFiltering("address.in=" + DEFAULT_ADDRESS + "," + UPDATED_ADDRESS, "address.in=" + UPDATED_ADDRESS);
    }

    @Test
    @Transactional
    void getAllWarehousesByAddressIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedWarehouse = warehouseRepository.saveAndFlush(warehouse);

        // Get all the warehouseList where address is not null
        defaultWarehouseFiltering("address.specified=true", "address.specified=false");
    }

    @Test
    @Transactional
    void getAllWarehousesByAddressContainsSomething() throws Exception {
        // Initialize the database
        insertedWarehouse = warehouseRepository.saveAndFlush(warehouse);

        // Get all the warehouseList where address contains
        defaultWarehouseFiltering("address.contains=" + DEFAULT_ADDRESS, "address.contains=" + UPDATED_ADDRESS);
    }

    @Test
    @Transactional
    void getAllWarehousesByAddressNotContainsSomething() throws Exception {
        // Initialize the database
        insertedWarehouse = warehouseRepository.saveAndFlush(warehouse);

        // Get all the warehouseList where address does not contain
        defaultWarehouseFiltering("address.doesNotContain=" + UPDATED_ADDRESS, "address.doesNotContain=" + DEFAULT_ADDRESS);
    }

    @Test
    @Transactional
    void getAllWarehousesByIsActiveIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedWarehouse = warehouseRepository.saveAndFlush(warehouse);

        // Get all the warehouseList where isActive equals to
        defaultWarehouseFiltering("isActive.equals=" + DEFAULT_IS_ACTIVE, "isActive.equals=" + UPDATED_IS_ACTIVE);
    }

    @Test
    @Transactional
    void getAllWarehousesByIsActiveIsInShouldWork() throws Exception {
        // Initialize the database
        insertedWarehouse = warehouseRepository.saveAndFlush(warehouse);

        // Get all the warehouseList where isActive in
        defaultWarehouseFiltering("isActive.in=" + DEFAULT_IS_ACTIVE + "," + UPDATED_IS_ACTIVE, "isActive.in=" + UPDATED_IS_ACTIVE);
    }

    @Test
    @Transactional
    void getAllWarehousesByIsActiveIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedWarehouse = warehouseRepository.saveAndFlush(warehouse);

        // Get all the warehouseList where isActive is not null
        defaultWarehouseFiltering("isActive.specified=true", "isActive.specified=false");
    }

    private void defaultWarehouseFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultWarehouseShouldBeFound(shouldBeFound);
        defaultWarehouseShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultWarehouseShouldBeFound(String filter) throws Exception {
        restWarehouseMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(warehouse.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].address").value(hasItem(DEFAULT_ADDRESS)))
            .andExpect(jsonPath("$.[*].isActive").value(hasItem(DEFAULT_IS_ACTIVE)));

        // Check, that the count call also returns 1
        restWarehouseMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultWarehouseShouldNotBeFound(String filter) throws Exception {
        restWarehouseMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restWarehouseMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingWarehouse() throws Exception {
        // Get the warehouse
        restWarehouseMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingWarehouse() throws Exception {
        // Initialize the database
        insertedWarehouse = warehouseRepository.saveAndFlush(warehouse);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the warehouse
        Warehouse updatedWarehouse = warehouseRepository.findById(warehouse.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedWarehouse are not directly saved in db
        em.detach(updatedWarehouse);
        updatedWarehouse.name(UPDATED_NAME).address(UPDATED_ADDRESS).isActive(UPDATED_IS_ACTIVE);
        WarehouseDTO warehouseDTO = warehouseMapper.toDto(updatedWarehouse);

        restWarehouseMockMvc
            .perform(
                put(ENTITY_API_URL_ID, warehouseDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(warehouseDTO))
            )
            .andExpect(status().isOk());

        // Validate the Warehouse in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedWarehouseToMatchAllProperties(updatedWarehouse);
    }

    @Test
    @Transactional
    void putNonExistingWarehouse() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        warehouse.setId(longCount.incrementAndGet());

        // Create the Warehouse
        WarehouseDTO warehouseDTO = warehouseMapper.toDto(warehouse);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restWarehouseMockMvc
            .perform(
                put(ENTITY_API_URL_ID, warehouseDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(warehouseDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Warehouse in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchWarehouse() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        warehouse.setId(longCount.incrementAndGet());

        // Create the Warehouse
        WarehouseDTO warehouseDTO = warehouseMapper.toDto(warehouse);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWarehouseMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(warehouseDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Warehouse in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamWarehouse() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        warehouse.setId(longCount.incrementAndGet());

        // Create the Warehouse
        WarehouseDTO warehouseDTO = warehouseMapper.toDto(warehouse);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWarehouseMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(warehouseDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Warehouse in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateWarehouseWithPatch() throws Exception {
        // Initialize the database
        insertedWarehouse = warehouseRepository.saveAndFlush(warehouse);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the warehouse using partial update
        Warehouse partialUpdatedWarehouse = new Warehouse();
        partialUpdatedWarehouse.setId(warehouse.getId());

        partialUpdatedWarehouse.address(UPDATED_ADDRESS).isActive(UPDATED_IS_ACTIVE);

        restWarehouseMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedWarehouse.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedWarehouse))
            )
            .andExpect(status().isOk());

        // Validate the Warehouse in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertWarehouseUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedWarehouse, warehouse),
            getPersistedWarehouse(warehouse)
        );
    }

    @Test
    @Transactional
    void fullUpdateWarehouseWithPatch() throws Exception {
        // Initialize the database
        insertedWarehouse = warehouseRepository.saveAndFlush(warehouse);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the warehouse using partial update
        Warehouse partialUpdatedWarehouse = new Warehouse();
        partialUpdatedWarehouse.setId(warehouse.getId());

        partialUpdatedWarehouse.name(UPDATED_NAME).address(UPDATED_ADDRESS).isActive(UPDATED_IS_ACTIVE);

        restWarehouseMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedWarehouse.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedWarehouse))
            )
            .andExpect(status().isOk());

        // Validate the Warehouse in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertWarehouseUpdatableFieldsEquals(partialUpdatedWarehouse, getPersistedWarehouse(partialUpdatedWarehouse));
    }

    @Test
    @Transactional
    void patchNonExistingWarehouse() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        warehouse.setId(longCount.incrementAndGet());

        // Create the Warehouse
        WarehouseDTO warehouseDTO = warehouseMapper.toDto(warehouse);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restWarehouseMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, warehouseDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(warehouseDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Warehouse in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchWarehouse() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        warehouse.setId(longCount.incrementAndGet());

        // Create the Warehouse
        WarehouseDTO warehouseDTO = warehouseMapper.toDto(warehouse);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWarehouseMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(warehouseDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Warehouse in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamWarehouse() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        warehouse.setId(longCount.incrementAndGet());

        // Create the Warehouse
        WarehouseDTO warehouseDTO = warehouseMapper.toDto(warehouse);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWarehouseMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(warehouseDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Warehouse in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteWarehouse() throws Exception {
        // Initialize the database
        insertedWarehouse = warehouseRepository.saveAndFlush(warehouse);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the warehouse
        restWarehouseMockMvc
            .perform(delete(ENTITY_API_URL_ID, warehouse.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return warehouseRepository.count();
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

    protected Warehouse getPersistedWarehouse(Warehouse warehouse) {
        return warehouseRepository.findById(warehouse.getId()).orElseThrow();
    }

    protected void assertPersistedWarehouseToMatchAllProperties(Warehouse expectedWarehouse) {
        assertWarehouseAllPropertiesEquals(expectedWarehouse, getPersistedWarehouse(expectedWarehouse));
    }

    protected void assertPersistedWarehouseToMatchUpdatableProperties(Warehouse expectedWarehouse) {
        assertWarehouseAllUpdatablePropertiesEquals(expectedWarehouse, getPersistedWarehouse(expectedWarehouse));
    }
}
