package com.fillumina.demo.jhcryptfield.web.rest;

import com.fillumina.demo.jhcryptfield.domain.Customer;
import com.fillumina.demo.jhcryptfield.domain.CustomerAddress;
import com.fillumina.demo.jhcryptfield.repository.CustomerRepository;
import com.fillumina.demo.jhcryptfield.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.fillumina.demo.jhcryptfield.domain.CustomerAddress}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class CustomerAddressResource {

    private final Logger log = LoggerFactory.getLogger(CustomerAddressResource.class);

    private static final String ENTITY_NAME = "customerAddress";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CustomerRepository customerRepository;

    public CustomerAddressResource(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    /**
     * {@code POST  /customer-addresses} : Create a new customerAddress.
     *
     * @param customerAddress the customerAddress to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new customerAddress, or with status {@code 400 (Bad Request)} if the customerAddress has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/customer-addresses")
    public ResponseEntity<CustomerAddress> createCustomerAddress(@Valid @RequestBody CustomerAddress customerAddress)
        throws URISyntaxException {
        log.debug("REST request to save CustomerAddress : {}", customerAddress);
        if (customerAddress.getId() != null) {
            throw new BadRequestAlertException("A new customerAddress cannot already have an ID", ENTITY_NAME, "idexists");
        }
        CustomerAddress result = saveCustomerAddress(customerAddress);
        return ResponseEntity
            .created(new URI("/api/customer-addresses/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * Save {@link CustomerAddress} by loading, updating and saving it's owner class.
     * It's a bit convoluted but should work.
     *
     * @param customerAddress
     * @return
     */
    private CustomerAddress saveCustomerAddress(CustomerAddress customerAddress) {
        Customer customer = customerRepository.getById(customerAddress.getId());
        customer.setAddress(customerAddress);
        Customer savedCustomer = customerRepository.save(customer);
        CustomerAddress result = savedCustomer.getAddress();
        return result;
    }

    /**
     * {@code PUT  /customer-addresses/:id} : Updates an existing customerAddress.
     *
     * @param id the id of the customerAddress to save.
     * @param customerAddress the customerAddress to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated customerAddress,
     * or with status {@code 400 (Bad Request)} if the customerAddress is not valid,
     * or with status {@code 500 (Internal Server Error)} if the customerAddress couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/customer-addresses/{id}")
    public ResponseEntity<CustomerAddress> updateCustomerAddress(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody CustomerAddress customerAddress
    ) throws URISyntaxException {
        log.debug("REST request to update CustomerAddress : {}, {}", id, customerAddress);
        if (customerAddress.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, customerAddress.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!customerRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        CustomerAddress result = saveCustomerAddress(customerAddress);

        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, customerAddress.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /customer-addresses/:id} : Partial updates given fields of an existing customerAddress, field will ignore if it is null
     *
     * @param id the id of the customerAddress to save.
     * @param customerAddress the customerAddress to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated customerAddress,
     * or with status {@code 400 (Bad Request)} if the customerAddress is not valid,
     * or with status {@code 404 (Not Found)} if the customerAddress is not found,
     * or with status {@code 500 (Internal Server Error)} if the customerAddress couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/customer-addresses/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<CustomerAddress> partialUpdateCustomerAddress(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody CustomerAddress customerAddress
    ) throws URISyntaxException {
        log.debug("REST request to partial update CustomerAddress partially : {}, {}", id, customerAddress);
        if (customerAddress.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, customerAddress.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!customerRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Customer> customerResult = customerRepository
            .findById(customerAddress.getId())
            .map(customer -> {
                final CustomerAddress address = customer.getAddress();
                if (customerAddress.getStreet() != null) {
                    address.setStreet(customerAddress.getStreet());
                }
                if (customerAddress.getCity() != null) {
                    address.setCity(customerAddress.getCity());
                }
                if (customerAddress.getPostcode() != null) {
                    address.setPostcode(customerAddress.getPostcode());
                }
                if (customerAddress.getCountry() != null) {
                    address.setCountry(customerAddress.getCountry());
                }

                return customer;
            })
            .map(customerRepository::save);

        Optional<CustomerAddress> result = customerResult.isPresent() ? Optional.of(customerResult.get().getAddress()) : Optional.empty();

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, customerAddress.getId().toString())
        );
    }

    /**
     * {@code GET  /customer-addresses} : get all the customerAddresses.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of customerAddresses in body.
     */
    @GetMapping("/customer-addresses")
    public List<CustomerAddress> getAllCustomerAddresses() {
        log.debug("REST request to get all CustomerAddresses");
        List<Customer> customerList = customerRepository.findAll();
        return customerList.stream().map(c -> c.getAddress()).filter(a -> a != null).collect(Collectors.toList());
    }

    /**
     * {@code GET  /customer-addresses/:id} : get the "id" customerAddress.
     *
     * @param id the id of the customerAddress to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the customerAddress, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/customer-addresses/{id}")
    public ResponseEntity<CustomerAddress> getCustomerAddress(@PathVariable Long id) {
        log.debug("REST request to get CustomerAddress : {}", id);
        Optional<Customer> customerOpt = customerRepository.findById(id);
        Optional<CustomerAddress> customerAddressOpt;
        if (customerOpt.isPresent()) {
            Customer customer = customerOpt.get();
            CustomerAddress customerAddress = customer.getAddress();
            CustomerAddress address = customerAddress != null ? customerAddress : new CustomerAddress().id(id);
            customerAddressOpt = Optional.of(address);
        } else {
            customerAddressOpt = Optional.empty();
        }
        return ResponseUtil.wrapOrNotFound(customerAddressOpt);
    }

    /**
     * {@code DELETE  /customer-addresses/:id} : delete the "id" customerAddress.
     *
     * @param id the id of the customerAddress to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/customer-addresses/{id}")
    public ResponseEntity<Void> deleteCustomerAddress(@PathVariable Long id) {
        log.debug("REST request to delete CustomerAddress : {}", id);
        customerRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
