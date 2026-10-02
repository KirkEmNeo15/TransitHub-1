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

/**
 * Links a Route to a Stop and remembers the ORDER of the stop on that route.
 * This is how the many-to-many relationship between Route and Stop is modeled.
 */
@Entity
@Table(name = "route_stops")
public class RouteStop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "route_id", nullable = false)
    private Route route;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "stop_id", nullable = false)
    private Stop stop;

    @Column(name = "stop_order", nullable = false)
    private int stopOrder;

    @Column(name = "minutes_from_start", nullable = false)
    private int minutesFromStart;

    protected RouteStop() {
    }

    // Package-private: only Route.addRouteStop(...) creates these, so both sides stay in sync.
    RouteStop(Route route, Stop stop, int stopOrder, int minutesFromStart) {
        this.route = ValidationUtils.requireNonNull(route, "Route");
        this.stop = ValidationUtils.requireNonNull(stop, "Stop");
        setStopOrder(stopOrder);
        setMinutesFromStart(minutesFromStart);
    }

    public Long getId() {
        return id;
    }

    public Route getRoute() {
        return route;
    }

    public Stop getStop() {
        return stop;
    }

    public int getStopOrder() {
        return stopOrder;
    }

    public void setStopOrder(int stopOrder) {
        this.stopOrder = ValidationUtils.requirePositive(stopOrder, "Stop order");
    }

    public int getMinutesFromStart() {
        return minutesFromStart;
    }

    public void setMinutesFromStart(int minutesFromStart) {
        this.minutesFromStart = ValidationUtils.requireNonNegative(minutesFromStart, "Minutes from start");
    }
}
