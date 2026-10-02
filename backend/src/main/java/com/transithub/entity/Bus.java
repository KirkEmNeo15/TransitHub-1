package com.transithub.entity;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

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
}
