package com.transithub.entity;

import com.transithub.util.ValidationUtils;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "drivers")
public class Driver {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @Column(name = "license_number", nullable = false, unique = true, length = 30)
    private String licenseNumber;

    @Column(name = "contact_number", length = 20)
    private String contactNumber;

    @Column(nullable = false)
    private boolean active = true;

    protected Driver() {
    }

    public Driver(String fullName, String licenseNumber) {
        setFullName(fullName);
        setLicenseNumber(licenseNumber);
    }

    public Long getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = ValidationUtils.requireNotBlank(fullName, "Driver name");
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = ValidationUtils.requireNotBlank(licenseNumber, "License number");
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = ValidationUtils.trimToNull(contactNumber);
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
