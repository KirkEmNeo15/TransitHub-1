package com.transithub.entity;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Demonstrates polymorphism: ONE parent type (Transportation), MANY behaviors.
 * Plain unit test: no database and no Spring needed, so it runs in a second.
 */
class TransportationPolymorphismTest {

    // The SAME fare rule is used for every type: base P13.00, P0.85 per km
    private final Fare fareRule = new Fare(new BigDecimal("13.00"), new BigDecimal("0.85"));

    private List<Transportation> allTypes() {
        List<Transportation> services = new ArrayList<>();
        services.add(new Bus("Test Bus", "BUS-T", null, false));
        services.add(new Jeepney("Test Jeepney", "JEEP-T", null, false));
        services.add(new Van("Test Van", "VAN-T", null, 15));
        services.add(new Shuttle("Test Shuttle", "SHT-T", null, "Lipa City"));
        services.add(new Train("Test Train", "TRN-T", null, 4));
        return services;
    }

    @Test
    void sameParentTypeAnswersWithItsOwnTypeName() {
        List<String> typeNames = new ArrayList<>();
        for (Transportation transportation : allTypes()) {
            typeNames.add(transportation.getTransportationType()); // dynamic method dispatch
        }
        assertEquals(List.of("Bus", "Jeepney", "Van", "Shuttle", "Train"), typeNames);
    }

    @Test
    void sameCallGivesDifferentFaresForEachType() {
        double distanceKm = 30;

        // Jeepney: 13.00 + 0.85 x (30 - 4 free km) = 35.10
        // Bus (no aircon): 13.00 + 0.85 x 30 = 38.50
        // Van: 38.50 rounded UP to a whole peso = 39.00
        // Shuttle: flat base fare = 13.00
        // Train: max(13.00, 0.85 x 30 = 25.50) = 25.50
        List<String> expectedFares = List.of("38.50", "35.10", "39.00", "13.00", "25.50");

        List<Transportation> services = allTypes();
        for (int i = 0; i < services.size(); i++) {
            Transportation transportation = services.get(i);
            BigDecimal actual = transportation.calculateFare(distanceKm, fareRule);
            assertFare(expectedFares.get(i), actual, transportation.getTransportationType());
        }
    }

    @Test
    void airConditionedBusCharges10PercentMore() {
        Bus airConditioned = new Bus("Aircon Bus", "BUS-AC", null, true);
        assertFare("42.35", airConditioned.calculateFare(30, fareRule), "Aircon Bus");
    }

    @Test
    void jeepneyBaseFareCoversTheFirstFourKilometers() {
        Jeepney jeepney = new Jeepney("Test Jeepney", "JEEP-T2", null, false);
        assertFare("13.00", jeepney.calculateFare(3, fareRule), "Jeepney 3 km");
    }

    @Test
    void trainNeverChargesLessThanTheMinimumFare() {
        Train train = new Train("Test Train", "TRN-T2", null, 3);
        assertFare("13.00", train.calculateFare(5, fareRule), "Train 5 km");
    }

    @Test
    void vanRoundsUpToTheNextWholePeso() {
        Fare vanFare = new Fare(new BigDecimal("30.00"), new BigDecimal("1.50"));
        Van van = new Van("Test Van", "VAN-T2", null, 15);
        assertFare("57.00", van.calculateFare(17.5, vanFare), "Van 17.5 km"); // 56.25 -> 57
    }

    @Test
    void invalidFareInputIsRejected() {
        Jeepney jeepney = new Jeepney("Test Jeepney", "JEEP-T3", null, false);
        assertThrows(IllegalArgumentException.class, () -> jeepney.calculateFare(0, fareRule));
        assertThrows(IllegalArgumentException.class, () -> jeepney.calculateFare(10, null));
    }

    // BigDecimal("35.1") and BigDecimal("35.10") are not equal(), so compare numerically
    private static void assertFare(String expected, BigDecimal actual, String label) {
        assertEquals(0, new BigDecimal(expected).compareTo(actual),
                label + ": expected " + expected + " but was " + actual);
    }
}
