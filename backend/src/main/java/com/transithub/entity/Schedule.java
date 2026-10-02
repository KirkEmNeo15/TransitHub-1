package com.transithub.entity;

import com.transithub.util.ValidationUtils;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalTime;

/** Operating hours and how often a vehicle leaves, for one route. */
@Entity
@Table(name = "schedules")
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "route_id", nullable = false)
    private Route route;

    @Column(name = "first_trip", nullable = false)
    private LocalTime firstTrip;

    @Column(name = "last_trip", nullable = false)
    private LocalTime lastTrip;

    @Column(name = "frequency_minutes", nullable = false)
    private int frequencyMinutes;

    @Column(name = "days_operating", nullable = false, length = 50)
    private String daysOperating;

    protected Schedule() {
    }

    public Schedule(LocalTime firstTrip, LocalTime lastTrip, int frequencyMinutes, String daysOperating) {
        setOperatingHours(firstTrip, lastTrip);
        setFrequencyMinutes(frequencyMinutes);
        setDaysOperating(daysOperating);
    }

    // Package-private: called by Route.addSchedule(...)
    void attachTo(Route route) {
        this.route = route;
    }

    public Long getId() {
        return id;
    }

    public Route getRoute() {
        return route;
    }

    public LocalTime getFirstTrip() {
        return firstTrip;
    }

    public LocalTime getLastTrip() {
        return lastTrip;
    }

    /** First and last trip are set together because the first must be earlier than the last. */
    public void setOperatingHours(LocalTime firstTrip, LocalTime lastTrip) {
        ValidationUtils.requireNonNull(firstTrip, "First trip time");
        ValidationUtils.requireNonNull(lastTrip, "Last trip time");
        if (!firstTrip.isBefore(lastTrip)) {
            throw new IllegalArgumentException("First trip must be earlier than the last trip");
        }
        this.firstTrip = firstTrip;
        this.lastTrip = lastTrip;
    }

    public int getFrequencyMinutes() {
        return frequencyMinutes;
    }

    public void setFrequencyMinutes(int frequencyMinutes) {
        this.frequencyMinutes = ValidationUtils.requirePositive(frequencyMinutes, "Frequency");
    }

    public String getDaysOperating() {
        return daysOperating;
    }

    public void setDaysOperating(String daysOperating) {
        this.daysOperating = ValidationUtils.requireNotBlank(daysOperating, "Days operating");
    }
}
