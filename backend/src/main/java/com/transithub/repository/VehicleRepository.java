package com.transithub.repository;

import com.transithub.entity.Vehicle;
import com.transithub.entity.enums.VehicleStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    boolean existsByPlateNumber(String plateNumber);

    // "TransportationId" means the id of the vehicle's transportation
    List<Vehicle> findByTransportationId(Long transportationId);

    // Is this driver already assigned to a vehicle?
    boolean existsByDriverId(Long driverId);

    long countByStatus(VehicleStatus status);
}
