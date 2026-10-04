package com.transithub.repository;

import com.transithub.entity.Coordinate;
import com.transithub.entity.Fare;
import com.transithub.entity.FavoriteRoute;
import com.transithub.entity.Route;
import com.transithub.entity.Schedule;
import com.transithub.entity.Stop;
import com.transithub.entity.User;
import com.transithub.entity.Van;
import com.transithub.entity.enums.Role;
import com.transithub.entity.enums.RouteStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Checks the repositories against the real database (Docker must be running with the
 * sample data loaded). Every test is rolled back, so nothing it saves stays in the database.
 * The route-search tests rely on the demo data in database/seed/sample-data.sql.
 */
@SpringBootTest
@Transactional
class RepositoryTest {

    @Autowired
    private RouteRepository routeRepository;
    @Autowired
    private StopRepository stopRepository;
    @Autowired
    private TransportationRepository transportationRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private FavoriteRouteRepository favoriteRouteRepository;
    @Autowired
    private AlertRepository alertRepository;

    @PersistenceContext
    private EntityManager entityManager;

    private Set<String> routeCodes(List<Route> routes) {
        return routes.stream().map(Route::getRouteCode).collect(Collectors.toSet());
    }

    // ---------------- route search ----------------

    @Test
    void searchFindsRoutesInTheRightDirectionOnly() {
        Set<String> lipaToBatangas = routeCodes(
                routeRepository.findDirectRoutes("Lipa", "Batangas", RouteStatus.ACTIVE));
        assertEquals(Set.of("LB-JEEP-01", "LB-BUS-01"), lipaToBatangas);

        Set<String> batangasToLipa = routeCodes(
                routeRepository.findDirectRoutes("Batangas", "Lipa", RouteStatus.ACTIVE));
        assertEquals(Set.of("BL-VAN-01", "BT-BUS-01"), batangasToLipa);
    }

    @Test
    void searchIgnoresUpperAndLowerCase() {
        Set<String> result = routeCodes(
                routeRepository.findDirectRoutes("LIPA", "tanauan", RouteStatus.ACTIVE));
        assertEquals(Set.of("LT-VAN-01", "BT-BUS-01"), result);
    }

    @Test
    void searchSkipsInactiveRoutes() {
        assertTrue(routeRepository.findDirectRoutes("Lipa", "Tambo", RouteStatus.ACTIVE).isEmpty(),
                "Lipa - Tambo is INACTIVE in the demo data");
        assertEquals(1, routeRepository.findDirectRoutes("Lipa", "Tambo", RouteStatus.INACTIVE).size());
    }

    @Test
    void searchWithUnknownPlaceReturnsNothing() {
        assertTrue(routeRepository.findDirectRoutes("Lipa", "Atlantis", RouteStatus.ACTIVE).isEmpty(),
                "unknown destination should return no routes");
    }

    // ---------------- other queries ----------------

    @Test
    void routesThroughAStopAreFound() {
        Stop ibaan = stopRepository.findByNameContainingIgnoreCase("Ibaan").get(0);
        Set<String> codes = routeCodes(routeRepository.findRoutesThroughStop(ibaan.getId()));
        assertTrue(codes.contains("LB-JEEP-01"), "Ibaan Junction is on the Lipa - Batangas jeepney route");
        assertFalse(codes.contains("LS-JEEP-01"), "the Lipa - SM City jeepney does not go to Ibaan");
    }

    @Test
    void stopsInsideARectangleAreFound() {
        // A small box around Lipa city center
        List<Stop> stops = stopRepository.findInBoundingBox(13.93, 13.96, 121.15, 121.17);
        assertTrue(stops.stream().anyMatch(s -> s.getName().equals("Lipa Public Market")),
                "Lipa Public Market should be inside the box");
        assertFalse(stops.stream().anyMatch(s -> s.getName().contains("Batangas")),
                "Batangas is far outside the box");
    }

    @Test
    void activeAlertsAreReturned() {
        assertTrue(alertRepository.findByActiveTrueOrderByCreatedAtDesc().size() >= 2,
                "the demo data has 2 active alerts");
    }

    // ---------------- saving ----------------

    @Test
    void savedUserCanBeFoundByEmail() {
        userRepository.save(new User("Test User", "Test.User@Example.com", "not-a-real-hash", Role.USER));

        // the entity stores the email in lowercase
        assertTrue(userRepository.findByEmail("test.user@example.com").isPresent());
        assertTrue(userRepository.existsByEmail("test.user@example.com"));
        assertFalse(userRepository.existsByEmail("nobody@example.com"));
    }

    @Test
    void savingARouteAlsoSavesItsStopsPathFareAndSchedule() {
        Van van = transportationRepository.save(new Van("Phase 6 Test Van", "VAN-P6-TEST", null, 12));
        Stop first = stopRepository.save(new Stop("P6 Test Stop A", null, 13.94, 121.16));
        Stop second = stopRepository.save(new Stop("P6 Test Stop B", null, 13.95, 121.17));

        Route route = new Route("P6-TEST-01", "P6 Test A - B", "Test A", "Test B", 20, 5.0, van);
        route.addRouteStop(first, 1, 0);
        route.addRouteStop(second, 2, 20);
        route.setPath(List.of(new Coordinate(13.94, 121.16), new Coordinate(13.95, 121.17)));
        route.setFare(new Fare(new BigDecimal("30.00"), new BigDecimal("1.50")));
        route.addSchedule(new Schedule(LocalTime.of(6, 0), LocalTime.of(20, 0), 30, "MON-SUN"));

        Long id = routeRepository.saveAndFlush(route).getId();
        entityManager.clear(); // forget everything in memory, so the next lines really read the database

        Route loaded = routeRepository.findById(id).orElseThrow();
        assertEquals(2, loaded.getRouteStops().size());
        assertEquals("P6 Test Stop A", loaded.getRouteStops().get(0).getStop().getName());
        assertEquals(2, loaded.getPath().size());
        assertEquals(0, new BigDecimal("30.00").compareTo(loaded.getFare().getBaseFare()));
        assertEquals(1, loaded.getSchedules().size());
        assertEquals("Van", loaded.getTransportation().getTransportationType());
        // Van fare: 30.00 + 1.50 x 5 km = 37.50, rounded up to a whole peso = 38.00
        assertEquals(0, new BigDecimal("38.00").compareTo(loaded.calculateFare()));
    }

    @Test
    void aUserCanSaveAFavoriteRoute() {
        User user = userRepository.save(new User("Fav User", "fav@example.com", "not-a-real-hash", Role.USER));
        Route route = routeRepository.findAll().get(0);

        favoriteRouteRepository.save(new FavoriteRoute(user, route));

        assertTrue(favoriteRouteRepository.existsByUserIdAndRouteId(user.getId(), route.getId()));
        assertEquals(1, favoriteRouteRepository.findByUserIdOrderByCreatedAtDesc(user.getId()).size());
    }
}
