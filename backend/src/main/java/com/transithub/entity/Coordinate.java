package com.transithub.entity;

import com.transithub.util.ValidationUtils;
import jakarta.persistence.Embeddable;

/**
 * One point (latitude, longitude) of the line drawn on the map for a route.
 * A record is an immutable data class: once created, its values cannot change.
 * @Embeddable means it has no table of its own; it is stored inside route_points.
 */
@Embeddable
public record Coordinate(double latitude, double longitude) {

    public Coordinate {
        ValidationUtils.requireInRange(latitude, -90, 90, "Latitude");
        ValidationUtils.requireInRange(longitude, -180, 180, "Longitude");
    }
}
