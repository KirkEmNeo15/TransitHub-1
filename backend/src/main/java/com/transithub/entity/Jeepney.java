package com.transithub.entity;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.math.BigDecimal;
import java.util.Map;

/** A jeepney service. */
@Entity
@DiscriminatorValue("JEEPNEY")
public class Jeepney extends Transportation {

    @Column(name = "is_modernized")
    private boolean modernized;

    protected Jeepney() {
    }

    public Jeepney(String name, String code, String description, boolean modernized) {
        super(name, code, description);
        this.modernized = modernized;
    }

    public boolean isModernized() {
        return modernized;
    }

    public void setModernized(boolean modernized) {
        this.modernized = modernized;
    }

    // The base fare already pays for the first 4 km
    private static final double KM_COVERED_BY_BASE_FARE = 4.0;

    @Override
    public String getTransportationType() {
        return "Jeepney";
    }

    /** Jeepney: base fare covers the first 4 km, then the rate applies to each extra km. */
    @Override
    public BigDecimal calculateFare(double distanceKm, Fare fareRule) {
        checkFareInputs(distanceKm, fareRule);
        double extraKm = Math.max(0, distanceKm - KM_COVERED_BY_BASE_FARE);
        BigDecimal extraCost = fareRule.getPerKmRate().multiply(BigDecimal.valueOf(extraKm));
        return toPesos(fareRule.getBaseFare().add(extraCost));
    }

    @Override
    public Map<String, Object> getTypeDetails() {
        return Map.of("modernized", modernized);
    }
}
