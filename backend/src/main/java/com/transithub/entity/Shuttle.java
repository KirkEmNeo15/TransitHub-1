package com.transithub.entity;

import com.transithub.util.ValidationUtils;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

/** A shuttle service that serves a limited area (for example a mall or campus). */
@Entity
@DiscriminatorValue("SHUTTLE")
public class Shuttle extends Transportation {

    @Column(name = "service_area", length = 100)
    private String serviceArea;

    protected Shuttle() {
    }

    public Shuttle(String name, String code, String description, String serviceArea) {
        super(name, code, description);
        setServiceArea(serviceArea);
    }

    public String getServiceArea() {
        return serviceArea;
    }

    public void setServiceArea(String serviceArea) {
        this.serviceArea = ValidationUtils.requireNotBlank(serviceArea, "Service area");
    }
}
