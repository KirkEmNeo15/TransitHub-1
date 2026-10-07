package com.transithub.repository;

import com.transithub.entity.Driver;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DriverRepository extends JpaRepository<Driver, Long> {

    boolean existsByLicenseNumber(String licenseNumber);
}
