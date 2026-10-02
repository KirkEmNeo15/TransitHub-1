package com.transithub.entity;

import com.transithub.util.ValidationUtils;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.math.BigDecimal;

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

    @Override
    public String getTransportationType() {
        return "Shuttle";
    }

    /** Shuttle: one flat fare for any trip, so only the base fare is used. */
    @Override
    public BigDecimal calculateFare(double distanceKm, Fare fareRule) {
        checkFareInputs(distanceKm, fareRule);
        return toPesos(fareRule.getBaseFare());
    }
}
