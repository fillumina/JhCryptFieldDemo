package com.fillumina.demo.jhcryptfield.web.rest;

import com.fillumina.demo.jhcryptfield.domain.CustomerAddress;
import com.fillumina.demo.jhcryptfield.repository.CustomerAddressRepository;
import com.fillumina.demo.jhcryptfield.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
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

    private final CustomerAddressRepository customerAddressRepository;

    public CustomerAddressResource(CustomerAddressRepository customerAddressRepository) {
        this.customerAddressRepository = customerAddressRepository;
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
        CustomerAddress result = customerAddressRepository.save(customerAddress);
        return ResponseEntity
            .created(new URI("/api/customer-addresses/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
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

        if (!customerAddressRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        CustomerAddress result = customerAddressRepository.save(customerAddress);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, customerAddress.getId().toString()))
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

        if (!customerAddressRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<CustomerAddress> result = customerAddressRepository
            .findById(customerAddress.getId())
            .map(existingCustomerAddress -> {
                if (customerAddress.getStreet() != null) {
                    existingCustomerAddress.setStreet(customerAddress.getStreet());
                }
                if (customerAddress.getCity() != null) {
                    existingCustomerAddress.setCity(customerAddress.getCity());
                }
                if (customerAddress.getPostcode() != null) {
                    existingCustomerAddress.setPostcode(customerAddress.getPostcode());
                }
                if (customerAddress.getCountry() != null) {
                    existingCustomerAddress.setCountry(customerAddress.getCountry());
                }

                return existingCustomerAddress;
            })
            .map(customerAddressRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, customerAddress.getId().toString())
        );
    }

    /**
     * {@code GET  /customer-addresses} : get all the customerAddresses.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of customerAddresses in body.
     */
    @GetMapping("/customer-addresses")
    public ResponseEntity<List<CustomerAddress>> getAllCustomerAddresses(Pageable pageable) {
        log.debug("REST request to get a page of CustomerAddresses");
        Page<CustomerAddress> page = customerAddressRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
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
        Optional<CustomerAddress> customerAddress = customerAddressRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(customerAddress);
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
        customerAddressRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
