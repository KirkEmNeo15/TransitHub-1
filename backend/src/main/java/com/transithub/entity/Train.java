package com.transithub.entity;

import com.transithub.util.ValidationUtils;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

/** A train service. */
@Entity
@DiscriminatorValue("TRAIN")
public class Train extends Transportation {

    @Column(name = "number_of_cars")
    private int numberOfCars;

    protected Train() {
    }

    public Train(String name, String code, String description, int numberOfCars) {
        super(name, code, description);
        setNumberOfCars(numberOfCars);
    }

    public int getNumberOfCars() {
        return numberOfCars;
    }

    public void setNumberOfCars(int numberOfCars) {
        this.numberOfCars = ValidationUtils.requirePositive(numberOfCars, "Number of cars");
    }
}
