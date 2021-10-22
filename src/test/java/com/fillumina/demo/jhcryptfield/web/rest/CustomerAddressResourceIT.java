package com.fillumina.demo.jhcryptfield.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fillumina.demo.jhcryptfield.IntegrationTest;
import com.fillumina.demo.jhcryptfield.domain.CustomerAddress;
import com.fillumina.demo.jhcryptfield.repository.CustomerAddressRepository;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link CustomerAddressResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class CustomerAddressResourceIT {

    private static final String DEFAULT_STREET = "AAAAAAAAAA";
    private static final String UPDATED_STREET = "BBBBBBBBBB";

    private static final String DEFAULT_CITY = "AAAAAAAAAA";
    private static final String UPDATED_CITY = "BBBBBBBBBB";

    private static final String DEFAULT_POSTCODE = "AAAAAAAAAA";
    private static final String UPDATED_POSTCODE = "BBBBBBBBBB";

    private static final String DEFAULT_COUNTRY = "AA";
    private static final String UPDATED_COUNTRY = "BB";

    private static final String ENTITY_API_URL = "/api/customer-addresses";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private CustomerAddressRepository customerAddressRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCustomerAddressMockMvc;

    private CustomerAddress customerAddress;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CustomerAddress createEntity(EntityManager em) {
        CustomerAddress customerAddress = new CustomerAddress()
            .street(DEFAULT_STREET)
            .city(DEFAULT_CITY)
            .postcode(DEFAULT_POSTCODE)
            .country(DEFAULT_COUNTRY);
        return customerAddress;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CustomerAddress createUpdatedEntity(EntityManager em) {
        CustomerAddress customerAddress = new CustomerAddress()
            .street(UPDATED_STREET)
            .city(UPDATED_CITY)
            .postcode(UPDATED_POSTCODE)
            .country(UPDATED_COUNTRY);
        return customerAddress;
    }

    @BeforeEach
    public void initTest() {
        customerAddress = createEntity(em);
    }

    @Test
    @Transactional
    void createCustomerAddress() throws Exception {
        int databaseSizeBeforeCreate = customerAddressRepository.findAll().size();
        // Create the CustomerAddress
        restCustomerAddressMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(customerAddress))
            )
            .andExpect(status().isCreated());

        // Validate the CustomerAddress in the database
        List<CustomerAddress> customerAddressList = customerAddressRepository.findAll();
        assertThat(customerAddressList).hasSize(databaseSizeBeforeCreate + 1);
        CustomerAddress testCustomerAddress = customerAddressList.get(customerAddressList.size() - 1);
        assertThat(testCustomerAddress.getStreet()).isEqualTo(DEFAULT_STREET);
        assertThat(testCustomerAddress.getCity()).isEqualTo(DEFAULT_CITY);
        assertThat(testCustomerAddress.getPostcode()).isEqualTo(DEFAULT_POSTCODE);
        assertThat(testCustomerAddress.getCountry()).isEqualTo(DEFAULT_COUNTRY);
    }

    @Test
    @Transactional
    void createCustomerAddressWithExistingId() throws Exception {
        // Create the CustomerAddress with an existing ID
        customerAddress.setId(1L);

        int databaseSizeBeforeCreate = customerAddressRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCustomerAddressMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(customerAddress))
            )
            .andExpect(status().isBadRequest());

        // Validate the CustomerAddress in the database
        List<CustomerAddress> customerAddressList = customerAddressRepository.findAll();
        assertThat(customerAddressList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkPostcodeIsRequired() throws Exception {
        int databaseSizeBeforeTest = customerAddressRepository.findAll().size();
        // set the field null
        customerAddress.setPostcode(null);

        // Create the CustomerAddress, which fails.

        restCustomerAddressMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(customerAddress))
            )
            .andExpect(status().isBadRequest());

        List<CustomerAddress> customerAddressList = customerAddressRepository.findAll();
        assertThat(customerAddressList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCountryIsRequired() throws Exception {
        int databaseSizeBeforeTest = customerAddressRepository.findAll().size();
        // set the field null
        customerAddress.setCountry(null);

        // Create the CustomerAddress, which fails.

        restCustomerAddressMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(customerAddress))
            )
            .andExpect(status().isBadRequest());

        List<CustomerAddress> customerAddressList = customerAddressRepository.findAll();
        assertThat(customerAddressList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllCustomerAddresses() throws Exception {
        // Initialize the database
        customerAddressRepository.saveAndFlush(customerAddress);

        // Get all the customerAddressList
        restCustomerAddressMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(customerAddress.getId().intValue())))
            .andExpect(jsonPath("$.[*].street").value(hasItem(DEFAULT_STREET)))
            .andExpect(jsonPath("$.[*].city").value(hasItem(DEFAULT_CITY)))
            .andExpect(jsonPath("$.[*].postcode").value(hasItem(DEFAULT_POSTCODE)))
            .andExpect(jsonPath("$.[*].country").value(hasItem(DEFAULT_COUNTRY)));
    }

    @Test
    @Transactional
    void getCustomerAddress() throws Exception {
        // Initialize the database
        customerAddressRepository.saveAndFlush(customerAddress);

        // Get the customerAddress
        restCustomerAddressMockMvc
            .perform(get(ENTITY_API_URL_ID, customerAddress.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(customerAddress.getId().intValue()))
            .andExpect(jsonPath("$.street").value(DEFAULT_STREET))
            .andExpect(jsonPath("$.city").value(DEFAULT_CITY))
            .andExpect(jsonPath("$.postcode").value(DEFAULT_POSTCODE))
            .andExpect(jsonPath("$.country").value(DEFAULT_COUNTRY));
    }

    @Test
    @Transactional
    void getNonExistingCustomerAddress() throws Exception {
        // Get the customerAddress
        restCustomerAddressMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewCustomerAddress() throws Exception {
        // Initialize the database
        customerAddressRepository.saveAndFlush(customerAddress);

        int databaseSizeBeforeUpdate = customerAddressRepository.findAll().size();

        // Update the customerAddress
        CustomerAddress updatedCustomerAddress = customerAddressRepository.findById(customerAddress.getId()).get();
        // Disconnect from session so that the updates on updatedCustomerAddress are not directly saved in db
        em.detach(updatedCustomerAddress);
        updatedCustomerAddress.street(UPDATED_STREET).city(UPDATED_CITY).postcode(UPDATED_POSTCODE).country(UPDATED_COUNTRY);

        restCustomerAddressMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedCustomerAddress.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedCustomerAddress))
            )
            .andExpect(status().isOk());

        // Validate the CustomerAddress in the database
        List<CustomerAddress> customerAddressList = customerAddressRepository.findAll();
        assertThat(customerAddressList).hasSize(databaseSizeBeforeUpdate);
        CustomerAddress testCustomerAddress = customerAddressList.get(customerAddressList.size() - 1);
        assertThat(testCustomerAddress.getStreet()).isEqualTo(UPDATED_STREET);
        assertThat(testCustomerAddress.getCity()).isEqualTo(UPDATED_CITY);
        assertThat(testCustomerAddress.getPostcode()).isEqualTo(UPDATED_POSTCODE);
        assertThat(testCustomerAddress.getCountry()).isEqualTo(UPDATED_COUNTRY);
    }

    @Test
    @Transactional
    void putNonExistingCustomerAddress() throws Exception {
        int databaseSizeBeforeUpdate = customerAddressRepository.findAll().size();
        customerAddress.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCustomerAddressMockMvc
            .perform(
                put(ENTITY_API_URL_ID, customerAddress.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(customerAddress))
            )
            .andExpect(status().isBadRequest());

        // Validate the CustomerAddress in the database
        List<CustomerAddress> customerAddressList = customerAddressRepository.findAll();
        assertThat(customerAddressList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchCustomerAddress() throws Exception {
        int databaseSizeBeforeUpdate = customerAddressRepository.findAll().size();
        customerAddress.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCustomerAddressMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(customerAddress))
            )
            .andExpect(status().isBadRequest());

        // Validate the CustomerAddress in the database
        List<CustomerAddress> customerAddressList = customerAddressRepository.findAll();
        assertThat(customerAddressList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCustomerAddress() throws Exception {
        int databaseSizeBeforeUpdate = customerAddressRepository.findAll().size();
        customerAddress.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCustomerAddressMockMvc
            .perform(
                put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(customerAddress))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the CustomerAddress in the database
        List<CustomerAddress> customerAddressList = customerAddressRepository.findAll();
        assertThat(customerAddressList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateCustomerAddressWithPatch() throws Exception {
        // Initialize the database
        customerAddressRepository.saveAndFlush(customerAddress);

        int databaseSizeBeforeUpdate = customerAddressRepository.findAll().size();

        // Update the customerAddress using partial update
        CustomerAddress partialUpdatedCustomerAddress = new CustomerAddress();
        partialUpdatedCustomerAddress.setId(customerAddress.getId());

        partialUpdatedCustomerAddress.street(UPDATED_STREET).postcode(UPDATED_POSTCODE).country(UPDATED_COUNTRY);

        restCustomerAddressMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCustomerAddress.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCustomerAddress))
            )
            .andExpect(status().isOk());

        // Validate the CustomerAddress in the database
        List<CustomerAddress> customerAddressList = customerAddressRepository.findAll();
        assertThat(customerAddressList).hasSize(databaseSizeBeforeUpdate);
        CustomerAddress testCustomerAddress = customerAddressList.get(customerAddressList.size() - 1);
        assertThat(testCustomerAddress.getStreet()).isEqualTo(UPDATED_STREET);
        assertThat(testCustomerAddress.getCity()).isEqualTo(DEFAULT_CITY);
        assertThat(testCustomerAddress.getPostcode()).isEqualTo(UPDATED_POSTCODE);
        assertThat(testCustomerAddress.getCountry()).isEqualTo(UPDATED_COUNTRY);
    }

    @Test
    @Transactional
    void fullUpdateCustomerAddressWithPatch() throws Exception {
        // Initialize the database
        customerAddressRepository.saveAndFlush(customerAddress);

        int databaseSizeBeforeUpdate = customerAddressRepository.findAll().size();

        // Update the customerAddress using partial update
        CustomerAddress partialUpdatedCustomerAddress = new CustomerAddress();
        partialUpdatedCustomerAddress.setId(customerAddress.getId());

        partialUpdatedCustomerAddress.street(UPDATED_STREET).city(UPDATED_CITY).postcode(UPDATED_POSTCODE).country(UPDATED_COUNTRY);

        restCustomerAddressMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCustomerAddress.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCustomerAddress))
            )
            .andExpect(status().isOk());

        // Validate the CustomerAddress in the database
        List<CustomerAddress> customerAddressList = customerAddressRepository.findAll();
        assertThat(customerAddressList).hasSize(databaseSizeBeforeUpdate);
        CustomerAddress testCustomerAddress = customerAddressList.get(customerAddressList.size() - 1);
        assertThat(testCustomerAddress.getStreet()).isEqualTo(UPDATED_STREET);
        assertThat(testCustomerAddress.getCity()).isEqualTo(UPDATED_CITY);
        assertThat(testCustomerAddress.getPostcode()).isEqualTo(UPDATED_POSTCODE);
        assertThat(testCustomerAddress.getCountry()).isEqualTo(UPDATED_COUNTRY);
    }

    @Test
    @Transactional
    void patchNonExistingCustomerAddress() throws Exception {
        int databaseSizeBeforeUpdate = customerAddressRepository.findAll().size();
        customerAddress.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCustomerAddressMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, customerAddress.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(customerAddress))
            )
            .andExpect(status().isBadRequest());

        // Validate the CustomerAddress in the database
        List<CustomerAddress> customerAddressList = customerAddressRepository.findAll();
        assertThat(customerAddressList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCustomerAddress() throws Exception {
        int databaseSizeBeforeUpdate = customerAddressRepository.findAll().size();
        customerAddress.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCustomerAddressMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(customerAddress))
            )
            .andExpect(status().isBadRequest());

        // Validate the CustomerAddress in the database
        List<CustomerAddress> customerAddressList = customerAddressRepository.findAll();
        assertThat(customerAddressList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCustomerAddress() throws Exception {
        int databaseSizeBeforeUpdate = customerAddressRepository.findAll().size();
        customerAddress.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCustomerAddressMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(customerAddress))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the CustomerAddress in the database
        List<CustomerAddress> customerAddressList = customerAddressRepository.findAll();
        assertThat(customerAddressList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteCustomerAddress() throws Exception {
        // Initialize the database
        customerAddressRepository.saveAndFlush(customerAddress);

        int databaseSizeBeforeDelete = customerAddressRepository.findAll().size();

        // Delete the customerAddress
        restCustomerAddressMockMvc
            .perform(delete(ENTITY_API_URL_ID, customerAddress.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<CustomerAddress> customerAddressList = customerAddressRepository.findAll();
        assertThat(customerAddressList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
