package com.transithub.repository;

import com.transithub.entity.Transportation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * One repository for ALL transportation types.
 * Asking for Transportation returns Bus, Jeepney, Van... objects (polymorphism in the database layer).
 */
public interface TransportationRepository extends JpaRepository<Transportation, Long> {

    Optional<Transportation> findByCode(String code);

    boolean existsByCode(String code);

    List<Transportation> findAllByOrderByNameAsc();
}
