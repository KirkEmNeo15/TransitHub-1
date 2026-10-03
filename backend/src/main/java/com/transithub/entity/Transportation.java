package com.transithub.entity;

import com.transithub.util.ValidationUtils;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

/**
 * A transportation service (for example "Southern Express Bus").
 * This is the PARENT class of Bus, Jeepney, Van, Shuttle and Train.
 * It is abstract: you cannot create a plain "Transportation", only one of its types.
 *
 * All types are stored in ONE table. The column "transport_type" (the discriminator)
 * tells Hibernate which subclass to create when it reads a row.
 *
 * It declares WHAT every transportation can do (abstract methods). Each subclass decides
 * HOW, by overriding them. That is abstraction + polymorphism.
 */
@Entity
@Table(name = "transportations")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "transport_type", discriminatorType = DiscriminatorType.STRING, length = 20)
public abstract class Transportation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 30)
    private String code;

    @Column(length = 500)
    private String description;

    // Required by JPA. Protected so only Hibernate and subclasses can use it.
    protected Transportation() {
    }

    protected Transportation(String name, String code, String description) {
        setName(name);
        setCode(code);
        setDescription(description);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = ValidationUtils.requireNotBlank(name, "Transportation name");
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = ValidationUtils.requireNotBlank(code, "Transportation code");
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = ValidationUtils.trimToNull(description);
    }

    // ------------------------------------------------------------------
    // ABSTRACT METHODS: every subclass MUST override these.
    // ------------------------------------------------------------------

    /** The name of the type, for example "Bus". Each subclass answers differently. */
    public abstract String getTransportationType();

    /**
     * Calculates the fare for a trip of the given distance, using the route's fare rule.
     * Every type prices differently, so every subclass has its own version.
     */
    public abstract BigDecimal calculateFare(double distanceKm, Fare fareRule);

    /**
     * The details that only this type has, for example {"airConditioned": true} for a Bus.
     * Each subclass answers for itself, so callers never need instanceof checks
     * (which would not even work on Hibernate lazy proxies).
     */
    public abstract Map<String, Object> getTypeDetails();

    // ------------------------------------------------------------------
    // SHARED HELPERS: written once here and inherited by all subclasses.
    // ------------------------------------------------------------------

    /** Rounds an amount to 2 decimal places (centavos). */
    protected static BigDecimal toPesos(BigDecimal amount) {
        return amount.setScale(2, RoundingMode.HALF_UP);
    }

    /** base fare + (rate per km x distance). Used by types that charge by distance. */
    protected static BigDecimal distanceBasedFare(double distanceKm, Fare fareRule) {
        BigDecimal distance = BigDecimal.valueOf(distanceKm);
        return fareRule.getBaseFare().add(fareRule.getPerKmRate().multiply(distance));
    }

    /** Stops a fare calculation early when the input is not valid. */
    protected static void checkFareInputs(double distanceKm, Fare fareRule) {
        ValidationUtils.requirePositive(distanceKm, "Distance");
        ValidationUtils.requireNonNull(fareRule, "Fare rule");
    }
}
