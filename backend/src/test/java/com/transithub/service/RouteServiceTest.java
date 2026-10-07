package com.transithub.service;

import com.transithub.dto.request.FareRequest;
import com.transithub.dto.request.RouteRequest;
import com.transithub.dto.request.RouteStopRequest;
import com.transithub.dto.request.ScheduleRequest;
import com.transithub.dto.response.RouteResponse;
import com.transithub.entity.Stop;
import com.transithub.entity.Transportation;
import com.transithub.entity.enums.RouteStatus;
import com.transithub.exception.DuplicateResourceException;
import com.transithub.exception.InvalidRequestException;
import com.transithub.exception.InvalidRouteException;
import com.transithub.exception.ResourceNotFoundException;
import com.transithub.repository.StopRepository;
import com.transithub.repository.TransportationRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests the route business logic against the real database (Docker running + sample data loaded).
 * Every test is rolled back. The search tests rely on the demo data.
 */
@SpringBootTest
@Transactional
class RouteServiceTest {

    @Autowired
    private RouteService routeService;
    @Autowired
    private StopRepository stopRepository;
    @Autowired
    private TransportationRepository transportationRepository;

    private static void assertFare(String expected, BigDecimal actual) {
        assertNotNull(actual, "fare should not be null");
        assertEquals(0, new BigDecimal(expected).compareTo(actual), "expected " + expected + " but was " + actual);
    }

    // ---------------- search ----------------

    @Test
    void searchReturnsRoutesFastestFirstWithTheFareOfEachType() {
        List<RouteResponse> results = routeService.searchRoutes("Lipa", "Batangas");

        assertEquals(2, results.size());
        // fastest first: the express bus (45 min) before the jeepney (60 min)
        assertEquals("LB-BUS-01", results.get(0).routeCode());
        assertEquals("LB-JEEP-01", results.get(1).routeCode());

        // Bus (air-conditioned): (20.00 + 1.00 x 30 km) x 1.10 = 55.00
        assertEquals("Bus", results.get(0).transportation().type());
        assertFare("55.00", results.get(0).estimatedFare());
        // Jeepney: 13.00 + 0.85 x (30 - 4) = 35.10
        assertEquals("Jeepney", results.get(1).transportation().type());
        assertFare("35.10", results.get(1).estimatedFare());

        // the result carries everything the search page shows
        assertTrue(results.get(1).stops().size() >= 2);
        assertTrue(results.get(1).path().size() >= 2);
        assertEquals(1, results.get(1).schedules().size());
    }

    @Test
    void searchRejectsBlankAndIdenticalPlaces() {
        assertThrows(InvalidRequestException.class, () -> routeService.searchRoutes("  ", "Batangas"));
        assertThrows(InvalidRequestException.class, () -> routeService.searchRoutes("Lipa", ""));
        assertThrows(InvalidRequestException.class, () -> routeService.searchRoutes("Lipa", " lipa "));
    }

    @Test
    void getRoutesCanFilterByType() {
        List<RouteResponse> buses = routeService.getRoutes("BUS", null);
        assertTrue(buses.size() >= 2, "the demo data has 2 bus routes");
        assertTrue(buses.stream().allMatch(r -> r.transportation().type().equals("Bus")));

        List<RouteResponse> inactive = routeService.getRoutes(null, RouteStatus.INACTIVE);
        assertTrue(inactive.stream().allMatch(r -> r.status().equals("INACTIVE")));
    }

    @Test
    void unknownRouteIsNotFound() {
        assertThrows(ResourceNotFoundException.class, () -> routeService.getRoute(999_999L));
    }

    // ---------------- create, update, delete ----------------

    private RouteRequest newRequest(String code, Long transportationId, List<RouteStopRequest> stops) {
        return new RouteRequest(code, "Service Test Route", "Alpha", "Beta", RouteStatus.ACTIVE,
                30, 10.0, transportationId,
                new FareRequest(new BigDecimal("15.00"), new BigDecimal("1.00")),
                stops,
                null, // no map line given: it is drawn through the stops
                List.of(new ScheduleRequest(LocalTime.of(6, 0), LocalTime.of(18, 0), 15, "MON-SAT")));
    }

    @Test
    void createUpdateAndDeleteARoute() {
        Transportation transportation = transportationRepository.findAll().get(0);
        List<Stop> stops = stopRepository.findAll();
        Stop first = stops.get(0);
        Stop second = stops.get(1);

        // create
        RouteResponse created = routeService.createRoute(newRequest("SVC-TEST-01", transportation.getId(),
                List.of(new RouteStopRequest(first.getId(), 0), new RouteStopRequest(second.getId(), 30))));
        assertNotNull(created.id());
        assertEquals(2, created.stops().size());
        assertEquals(2, created.path().size(), "the map line should be created from the stops");
        assertEquals(1, created.schedules().size());
        assertFare("15.00", created.fareRule().baseFare());

        // update: same stops in the opposite order (this used to break on the unique stop order)
        RouteResponse updated = routeService.updateRoute(created.id(), newRequest("SVC-TEST-01", transportation.getId(),
                List.of(new RouteStopRequest(second.getId(), 0), new RouteStopRequest(first.getId(), 30))));
        assertEquals(second.getId(), updated.stops().get(0).stop().id());
        assertEquals(first.getId(), updated.stops().get(1).stop().id());

        // delete
        routeService.deleteRoute(created.id());
        assertThrows(ResourceNotFoundException.class, () -> routeService.getRoute(created.id()));
    }

    @Test
    void duplicateRouteCodeIsRejected() {
        Transportation transportation = transportationRepository.findAll().get(0);
        List<Stop> stops = stopRepository.findAll();
        List<RouteStopRequest> stopRequests = List.of(
                new RouteStopRequest(stops.get(0).getId(), 0), new RouteStopRequest(stops.get(1).getId(), 10));

        assertThrows(DuplicateResourceException.class,
                () -> routeService.createRoute(newRequest("LB-JEEP-01", transportation.getId(), stopRequests)));
    }

    @Test
    void routeNeedsAtLeastTwoDifferentExistingStops() {
        Transportation transportation = transportationRepository.findAll().get(0);
        Long stopId = stopRepository.findAll().get(0).getId();

        assertThrows(InvalidRouteException.class, () -> routeService.createRoute(
                newRequest("SVC-TEST-02", transportation.getId(), List.of(new RouteStopRequest(stopId, 0)))));
        assertThrows(InvalidRouteException.class, () -> routeService.createRoute(
                newRequest("SVC-TEST-03", transportation.getId(),
                        List.of(new RouteStopRequest(stopId, 0), new RouteStopRequest(stopId, 5)))));
        assertThrows(InvalidRouteException.class, () -> routeService.createRoute(
                newRequest("SVC-TEST-04", transportation.getId(),
                        List.of(new RouteStopRequest(stopId, 0), new RouteStopRequest(999_999L, 5)))));
    }

    @Test
    void routeStatusCanBeChanged() {
        Long id = routeService.getRoutes(null, RouteStatus.ACTIVE).get(0).id();
        assertEquals("SUSPENDED", routeService.changeStatus(id, RouteStatus.SUSPENDED).status());
    }
}
