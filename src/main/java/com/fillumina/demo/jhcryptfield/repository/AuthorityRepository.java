package com.fillumina.demo.jhcryptfield.repository;

import com.fillumina.demo.jhcryptfield.domain.Authority;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for the {@link Authority} entity.
 */
public interface AuthorityRepository extends JpaRepository<Authority, String> {}
