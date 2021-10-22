package com.fillumina.demo.jhcryptfield.repository;

import com.fillumina.demo.jhcryptfield.domain.CustomerAddress;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the CustomerAddress entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CustomerAddressRepository extends JpaRepository<CustomerAddress, Long> {}
