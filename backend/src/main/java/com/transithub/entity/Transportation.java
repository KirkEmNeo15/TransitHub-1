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

/**
 * A transportation service (for example "Southern Express Bus").
 * This is the PARENT class of Bus, Jeepney, Van, Shuttle and Train.
 * It is abstract: you cannot create a plain "Transportation", only one of its types.
 *
 * All types are stored in ONE table. The column "transport_type" (the discriminator)
 * tells Hibernate which subclass to create when it reads a row.
 *
 * Phase 5 adds the abstract behavior methods (getTransportationType, calculateFare).
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
}
