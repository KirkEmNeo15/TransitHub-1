package com.transithub.entity;

import com.transithub.util.ValidationUtils;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

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
}
