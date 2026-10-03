package com.transithub.service;

import com.transithub.dto.request.StopRequest;
import com.transithub.dto.response.NearbyStopResponse;
import com.transithub.dto.response.StopResponse;
import com.transithub.exception.DuplicateResourceException;
import com.transithub.exception.InvalidRequestException;
import com.transithub.exception.ResourceInUseException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
class StopServiceTest {

    @Autowired
    private StopService stopService;

    @Test
    void nearbyStopsAreWithinTheRadiusAndSortedByDistance() {
        // Standing at Lipa Public Market, looking 5 km around
        List<NearbyStopResponse> nearby = stopService.findNearbyStops(13.9411, 121.1631, 5.0);

        assertEquals("Lipa Public Market", nearby.get(0).stop().name());
        assertEquals(0.0, nearby.get(0).distanceKm());
        for (int i = 0; i < nearby.size(); i++) {
            assertTrue(nearby.get(i).distanceKm() <= 5.0, "all stops must be within 5 km");
            if (i > 0) {
                assertTrue(nearby.get(i - 1).distanceKm() <= nearby.get(i).distanceKm(), "must be nearest first");
            }
        }
        assertTrue(nearby.stream().anyMatch(n -> n.stop().name().equals("SM City Lipa")), "SM City Lipa is about 1.3 km away");
        assertFalse(nearby.stream().anyMatch(n -> n.stop().name().contains("Batangas")), "Batangas is about 23 km away");
    }

    @Test
    void nearbyRejectsInvalidInput() {
        assertThrows(InvalidRequestException.class, () -> stopService.findNearbyStops(95, 121, 5));
        assertThrows(InvalidRequestException.class, () -> stopService.findNearbyStops(13.9, 200, 5));
        assertThrows(InvalidRequestException.class, () -> stopService.findNearbyStops(13.9, 121, 0));
        assertThrows(InvalidRequestException.class, () -> stopService.findNearbyStops(13.9, 121, 500));
    }

    @Test
    void stopNamesMustBeUnique() {
        assertThrows(DuplicateResourceException.class,
                () -> stopService.createStop(new StopRequest("lipa public market", null, 13.9, 121.1)));
    }

    @Test
    void aStopCanBeCreatedAndDeletedWhenNoRouteUsesIt() {
        StopResponse created = stopService.createStop(new StopRequest("Service Test Stop", "temp", 13.95, 121.16));
        assertEquals("Service Test Stop", stopService.getStop(created.id()).name());

        stopService.deleteStop(created.id());
        assertTrue(stopService.getStops("Service Test Stop").isEmpty());
    }

    @Test
    void aStopUsedByARouteCannotBeDeleted() {
        Long ibaanId = stopService.getStops("Ibaan").get(0).id();
        assertThrows(ResourceInUseException.class, () -> stopService.deleteStop(ibaanId));
    }

    @Test
    void routesThroughAStopAreListed() {
        Long ibaanId = stopService.getStops("Ibaan").get(0).id();
        assertTrue(stopService.getRoutesThroughStop(ibaanId).stream()
                .anyMatch(r -> r.routeCode().equals("LB-JEEP-01")));
    }
}
