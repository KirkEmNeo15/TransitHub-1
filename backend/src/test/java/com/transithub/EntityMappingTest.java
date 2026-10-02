package com.transithub;

import com.transithub.entity.Bus;
import com.transithub.entity.Jeepney;
import com.transithub.entity.Route;
import com.transithub.entity.Shuttle;
import com.transithub.entity.Transportation;
import com.transithub.entity.Van;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Checks that our Java entities really match the PostgreSQL tables and the sample data.
 * Needs the database running with database/schema.sql and sample-data.sql loaded.
 * It only READS data, and each test is rolled back.
 */
@SpringBootTest
@Transactional
class EntityMappingTest {

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    void routesLoadWithStopsPathFareAndSchedules() {
        List<Route> routes = entityManager
                .createQuery("SELECT r FROM Route r", Route.class)
                .getResultList();

        assertFalse(routes.isEmpty(), "No routes found. Did you load database/seed/sample-data.sql?");
        for (Route route : routes) {
            assertTrue(route.getRouteStops().size() >= 2, route.getRouteCode() + " needs 2+ stops");
            assertTrue(route.getPath().size() >= 2, route.getRouteCode() + " needs 2+ map points");
            assertNotNull(route.getFare(), route.getRouteCode() + " needs a fare");
            assertFalse(route.getSchedules().isEmpty(), route.getRouteCode() + " needs a schedule");
            assertNotNull(route.getTransportation().getName());
        }
    }

    @Test
    void transportationsLoadAsTheirOwnSubclasses() {
        List<Transportation> all = entityManager
                .createQuery("SELECT t FROM Transportation t", Transportation.class)
                .getResultList();

        assertTrue(all.stream().anyMatch(t -> t instanceof Bus), "expected a Bus");
        assertTrue(all.stream().anyMatch(t -> t instanceof Jeepney), "expected a Jeepney");
        assertTrue(all.stream().anyMatch(t -> t instanceof Van), "expected a Van");
        assertTrue(all.stream().anyMatch(t -> t instanceof Shuttle), "expected a Shuttle");
    }

    @Test
    void settersRejectInvalidValues() {
        Van van = new Van("Test Van", "VAN-TEST", null, 15);

        assertThrows(IllegalArgumentException.class, () -> van.setSeatingCapacity(0));
        assertThrows(IllegalArgumentException.class, () -> van.setName("   "));
    }
}
