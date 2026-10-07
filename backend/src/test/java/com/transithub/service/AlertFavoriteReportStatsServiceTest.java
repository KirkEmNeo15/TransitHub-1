package com.transithub.service;

import com.transithub.dto.request.AlertRequest;
import com.transithub.dto.request.ReportRequest;
import com.transithub.dto.response.AlertResponse;
import com.transithub.dto.response.PublicStatsResponse;
import com.transithub.dto.response.ReportResponse;
import com.transithub.dto.response.RouteResponse;
import com.transithub.entity.User;
import com.transithub.entity.enums.AlertSeverity;
import com.transithub.entity.enums.ReportStatus;
import com.transithub.entity.enums.Role;
import com.transithub.exception.DuplicateResourceException;
import com.transithub.exception.ResourceNotFoundException;
import com.transithub.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Alerts, favorites, reports and statistics. Rolled back after each test. */
@SpringBootTest
@Transactional
class AlertFavoriteReportStatsServiceTest {

    @Autowired
    private AlertService alertService;
    @Autowired
    private FavoriteService favoriteService;
    @Autowired
    private ReportService reportService;
    @Autowired
    private StatsService statsService;
    @Autowired
    private RouteService routeService;
    @Autowired
    private UserRepository userRepository;

    private User newUser(String email) {
        return userRepository.save(new User("Test User", email, "not-a-real-hash", Role.USER));
    }

    private Long anyRouteId() {
        return routeService.getRoutes(null, null).get(0).id();
    }

    // ---------------- alerts ----------------

    @Test
    void activeAlertsAreListedAndInactiveOnesAreHidden() {
        AlertResponse hidden = alertService.createAlert(
                new AlertRequest("Hidden alert", "not shown to the public", AlertSeverity.INFO, null, false));

        assertTrue(alertService.getActiveAlerts().size() >= 2, "the demo data has 2 active alerts");
        assertTrue(alertService.getActiveAlerts().stream().noneMatch(a -> a.id().equals(hidden.id())));
        assertTrue(alertService.getAllAlerts().stream().anyMatch(a -> a.id().equals(hidden.id())));
    }

    @Test
    void alertCanBeLinkedToARoute() {
        Long routeId = anyRouteId();
        AlertResponse alert = alertService.createAlert(
                new AlertRequest("Route alert", "road works", AlertSeverity.WARNING, routeId, true));
        assertEquals(routeId, alert.routeId());
    }

    @Test
    void alertForAnUnknownRouteIsRejected() {
        assertThrows(ResourceNotFoundException.class, () -> alertService.createAlert(
                new AlertRequest("Bad alert", "no such route", AlertSeverity.INFO, 999_999L, true)));
    }

    // ---------------- favorites ----------------

    @Test
    void favoritesCanBeAddedListedAndRemoved() {
        User user = newUser("favorites@example.com");
        Long routeId = anyRouteId();

        favoriteService.addFavorite(user.getId(), routeId);
        List<RouteResponse> favorites = favoriteService.getFavorites(user.getId());
        assertEquals(1, favorites.size());
        assertEquals(routeId, favorites.get(0).id());

        assertThrows(DuplicateResourceException.class, () -> favoriteService.addFavorite(user.getId(), routeId));

        favoriteService.removeFavorite(user.getId(), routeId);
        assertTrue(favoriteService.getFavorites(user.getId()).isEmpty());
        assertThrows(ResourceNotFoundException.class, () -> favoriteService.removeFavorite(user.getId(), routeId));
    }

    // ---------------- reports ----------------

    @Test
    void reportsAreSubmittedAndReviewed() {
        User user = newUser("reports@example.com");

        ReportResponse report = reportService.submitReport(user.getId(),
                new ReportRequest(anyRouteId(), "The fare is wrong"));
        assertEquals("OPEN", report.status());
        assertEquals("reports@example.com", report.reportedBy());

        ReportResponse resolved = reportService.updateStatus(report.id(), ReportStatus.RESOLVED);
        assertEquals("RESOLVED", resolved.status());
        assertTrue(reportService.getReports(ReportStatus.OPEN).stream().noneMatch(r -> r.id().equals(report.id())));
    }

    @Test
    void reportForAnUnknownRouteIsRejected() {
        User user = newUser("badreport@example.com");
        assertThrows(ResourceNotFoundException.class,
                () -> reportService.submitReport(user.getId(), new ReportRequest(999_999L, "no such route")));
    }

    // ---------------- statistics ----------------

    @Test
    void statisticsCountTheDemoData() {
        PublicStatsResponse stats = statsService.getPublicStats();
        assertTrue(stats.activeRoutes() >= 1);
        assertTrue(stats.stops() >= 8);
        assertTrue(stats.availableVehicles() >= 1);
        assertTrue(stats.activeAlerts() >= 2);

        assertTrue(statsService.getAdminStats().totalRoutes() >= stats.activeRoutes());
    }
}
