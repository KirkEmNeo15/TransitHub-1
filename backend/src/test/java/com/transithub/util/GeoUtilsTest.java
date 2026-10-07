package com.transithub.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GeoUtilsTest {

    @Test
    void distanceToTheSamePointIsZero() {
        assertEquals(0.0, GeoUtils.distanceKm(13.9411, 121.1631, 13.9411, 121.1631));
    }

    @Test
    void lipaMarketToBatangasTerminalIsAbout23Km() {
        double km = GeoUtils.distanceKm(13.9411, 121.1631, 13.7620, 121.0590);
        assertTrue(Math.abs(km - 22.87) < 0.1, "expected about 22.87 km but was " + km);
    }

    @Test
    void distanceIsTheSameInBothDirections() {
        double there = GeoUtils.distanceKm(13.9411, 121.1631, 14.0449, 121.1584);
        double back = GeoUtils.distanceKm(14.0449, 121.1584, 13.9411, 121.1631);
        assertTrue(Math.abs(there - back) < 0.0001, "distance should not depend on direction");
    }

    @Test
    void kilometersConvertToDegrees() {
        // 111 km is about 1 degree of latitude
        assertTrue(Math.abs(GeoUtils.kmToLatitudeDegrees(111.0) - 1.0) < 0.0001, "111 km = 1 degree");
        // longitude degrees are wider than latitude degrees away from the equator
        assertTrue(GeoUtils.kmToLongitudeDegrees(10, 14.0) > GeoUtils.kmToLatitudeDegrees(10),
                "longitude degrees shrink away from the equator, so 10 km needs MORE of them");
    }
}
