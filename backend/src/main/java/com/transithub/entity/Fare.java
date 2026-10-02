package com.transithub.entity;

import com.transithub.util.ValidationUtils;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;

/**
 * Fare rules of one route, in Philippine pesos.
 * Money uses BigDecimal because double cannot store decimals like 0.1 exactly.
 */
@Entity
@Table(name = "fares")
public class Fare {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "route_id", nullable = false, unique = true)
    private Route route;

    @Column(name = "base_fare", nullable = false, precision = 8, scale = 2)
    private BigDecimal baseFare;

    @Column(name = "per_km_rate", nullable = false, precision = 8, scale = 2)
    private BigDecimal perKmRate;

    protected Fare() {
    }

    public Fare(BigDecimal baseFare, BigDecimal perKmRate) {
        setBaseFare(baseFare);
        setPerKmRate(perKmRate);
    }

    // Package-private: called by Route.setFare(...)
    void attachTo(Route route) {
        this.route = route;
    }

    public Long getId() {
        return id;
    }

    public Route getRoute() {
        return route;
    }

    public BigDecimal getBaseFare() {
        return baseFare;
    }

    public void setBaseFare(BigDecimal baseFare) {
        this.baseFare = ValidationUtils.requireNonNegative(baseFare, "Base fare");
    }

    public BigDecimal getPerKmRate() {
        return perKmRate;
    }

    public void setPerKmRate(BigDecimal perKmRate) {
        this.perKmRate = ValidationUtils.requireNonNegative(perKmRate, "Per-kilometer rate");
    }
}
