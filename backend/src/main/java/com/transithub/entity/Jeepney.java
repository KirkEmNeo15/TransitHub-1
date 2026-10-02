package com.transithub.entity;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

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
}
