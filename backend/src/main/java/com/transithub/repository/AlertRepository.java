package com.transithub.repository;

import com.transithub.entity.Alert;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlertRepository extends JpaRepository<Alert, Long> {

    // Newest first
    List<Alert> findByActiveTrueOrderByCreatedAtDesc();

    List<Alert> findByRouteIdOrderByCreatedAtDesc(Long routeId);

    long countByActiveTrue();
}
