package com.transithub.entity;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.math.BigDecimal;

/** A bus service. Inherits id, name, code and description from Transportation. */
@Entity
@DiscriminatorValue("BUS")
public class Bus extends Transportation {

    @Column(name = "has_air_conditioning")
    private boolean airConditioned;

    protected Bus() {
    }

    public Bus(String name, String code, String description, boolean airConditioned) {
        super(name, code, description);
        this.airConditioned = airConditioned;
    }

    public boolean isAirConditioned() {
        return airConditioned;
    }

    public void setAirConditioned(boolean airConditioned) {
        this.airConditioned = airConditioned;
    }

    // Air-conditioned buses charge 10% more
    private static final BigDecimal AIRCON_MULTIPLIER = new BigDecimal("1.10");

    @Override
    public String getTransportationType() {
        return "Bus";
    }

    /** Bus: base fare + rate x distance, plus a 10% surcharge when air-conditioned. */
    @Override
    public BigDecimal calculateFare(double distanceKm, Fare fareRule) {
        checkFareInputs(distanceKm, fareRule);
        BigDecimal amount = distanceBasedFare(distanceKm, fareRule);
        if (airConditioned) {
            amount = amount.multiply(AIRCON_MULTIPLIER);
        }
        return toPesos(amount);
    }
}
