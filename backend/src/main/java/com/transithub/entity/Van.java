package com.transithub.entity;

import com.transithub.util.ValidationUtils;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

/** A van (UV Express style) service. */
@Entity
@DiscriminatorValue("VAN")
public class Van extends Transportation {

    @Column(name = "seating_capacity")
    private int seatingCapacity;

    protected Van() {
    }

    public Van(String name, String code, String description, int seatingCapacity) {
        super(name, code, description);
        setSeatingCapacity(seatingCapacity);
    }

    public int getSeatingCapacity() {
        return seatingCapacity;
    }

    public void setSeatingCapacity(int seatingCapacity) {
        this.seatingCapacity = ValidationUtils.requirePositive(seatingCapacity, "Seating capacity");
    }

    @Override
    public String getTransportationType() {
        return "Van";
    }

    /** Van: base fare + rate x distance, rounded UP to a whole peso (vans collect whole pesos). */
    @Override
    public BigDecimal calculateFare(double distanceKm, Fare fareRule) {
        checkFareInputs(distanceKm, fareRule);
        BigDecimal wholePesos = distanceBasedFare(distanceKm, fareRule).setScale(0, RoundingMode.UP);
        return toPesos(wholePesos);
    }

    @Override
    public Map<String, Object> getTypeDetails() {
        return Map.of("seatingCapacity", seatingCapacity);
    }
}
