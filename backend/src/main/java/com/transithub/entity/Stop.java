package com.transithub.entity;

import com.transithub.util.ValidationUtils;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** A place where passengers can get on or off. */
@Entity
@Table(name = "stops")
public class Stop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 500)
    private String description;

    @Column(nullable = false)
    private double latitude;

    @Column(nullable = false)
    private double longitude;

    @Column(name = "is_demo_data", nullable = false)
    private boolean demoData;

    protected Stop() {
    }

    public Stop(String name, String description, double latitude, double longitude) {
        setName(name);
        setDescription(description);
        setLatitude(latitude);
        setLongitude(longitude);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = ValidationUtils.requireNotBlank(name, "Stop name");
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = ValidationUtils.trimToNull(description);
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = ValidationUtils.requireInRange(latitude, -90, 90, "Latitude");
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = ValidationUtils.requireInRange(longitude, -180, 180, "Longitude");
    }

    public boolean isDemoData() {
        return demoData;
    }

    public void setDemoData(boolean demoData) {
        this.demoData = demoData;
    }
}
