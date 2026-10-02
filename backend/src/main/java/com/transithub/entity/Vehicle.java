package com.transithub.entity;

import com.transithub.entity.enums.VehicleStatus;
import com.transithub.util.ValidationUtils;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

/** A physical vehicle that belongs to a transportation service and may have a driver. */
@Entity
@Table(name = "vehicles")
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "plate_number", nullable = false, unique = true, length = 20)
    private String plateNumber;

    @Column(nullable = false)
    private int capacity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private VehicleStatus status = VehicleStatus.ACTIVE;

    // MANY vehicles belong to ONE transportation service
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "transportation_id", nullable = false)
    private Transportation transportation;

    // ONE driver drives at most ONE vehicle (the driver is optional)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id", unique = true)
    private Driver driver;

    protected Vehicle() {
    }

    public Vehicle(String plateNumber, int capacity, Transportation transportation) {
        setPlateNumber(plateNumber);
        setCapacity(capacity);
        setTransportation(transportation);
    }

    public Long getId() {
        return id;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = ValidationUtils.requireNotBlank(plateNumber, "Plate number");
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = ValidationUtils.requirePositive(capacity, "Capacity");
    }

    public VehicleStatus getStatus() {
        return status;
    }

    public void setStatus(VehicleStatus status) {
        this.status = ValidationUtils.requireNonNull(status, "Vehicle status");
    }

    public Transportation getTransportation() {
        return transportation;
    }

    public void setTransportation(Transportation transportation) {
        this.transportation = ValidationUtils.requireNonNull(transportation, "Transportation");
    }

    public Driver getDriver() {
        return driver;
    }

    /** The driver is optional, so null is allowed. */
    public void setDriver(Driver driver) {
        this.driver = driver;
    }
}
