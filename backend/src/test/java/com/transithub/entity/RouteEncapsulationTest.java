package com.transithub.entity;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Demonstrates encapsulation: private fields, validated setters and controlled access.
 * Plain unit test: no database needed.
 */
class RouteEncapsulationTest {

    private Route newRoute() {
        Jeepney jeepney = new Jeepney("Test Jeepney", "JEEP-T", null, false);
        return new Route("R-1", "Lipa - Batangas City", "Lipa City", "Batangas City", 60, 30.0, jeepney);
    }

    @Test
    void routeStopsCanOnlyBeChangedThroughTheRouteItself() {
        Route route = newRoute();
        route.addRouteStop(new Stop("Lipa Terminal", null, 13.938, 121.162), 1, 0);

        assertEquals(1, route.getRouteStops().size());
        // The list we get back is read-only, so outside code cannot bypass the Route
        assertThrows(UnsupportedOperationException.class, () -> route.getRouteStops().clear());
    }

    @Test
    void originAndDestinationMustBeDifferent() {
        Route route = newRoute();
        assertThrows(IllegalArgumentException.class, () -> route.setEndpoints("Lipa City", "  lipa city "));
    }

    @Test
    void emptyOrInvalidValuesAreRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> new Route(" ", "Name", "A", "B", 10, 5.0, new Jeepney("J", "J-1", null, false)));
        assertThrows(IllegalArgumentException.class, () -> newRoute().setEstimatedMinutes(0));
        assertThrows(IllegalArgumentException.class, () -> new Stop("X", null, 95, 121));
        assertThrows(IllegalArgumentException.class, () -> new Coordinate(10, 200));
        assertThrows(IllegalArgumentException.class,
                () -> new Fare(new BigDecimal("-1"), new BigDecimal("1")));
    }

    @Test
    void settingTheFareLinksBothSides() {
        Route route = newRoute();
        Fare fare = new Fare(new BigDecimal("13.00"), new BigDecimal("0.85"));

        route.setFare(fare);

        assertSame(fare, route.getFare());
        assertSame(route, fare.getRoute());
    }

    @Test
    void routeAsksItsTransportationForTheFare() {
        Route route = newRoute();
        route.setFare(new Fare(new BigDecimal("13.00"), new BigDecimal("0.85")));

        // The route is a Jeepney route: 13.00 + 0.85 x (30 - 4) = 35.10
        assertEquals(0, new BigDecimal("35.10").compareTo(route.calculateFare()));
    }

    @Test
    void fareCannotBeCalculatedWithoutAFareRule() {
        assertThrows(IllegalStateException.class, () -> newRoute().calculateFare());
    }
}
