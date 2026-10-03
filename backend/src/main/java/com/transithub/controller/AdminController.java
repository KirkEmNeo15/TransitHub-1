package com.transithub.controller;

import com.transithub.dto.PageResponse;
import com.transithub.dto.request.ReportStatusRequest;
import com.transithub.dto.request.RoleChangeRequest;
import com.transithub.dto.request.UserActiveRequest;
import com.transithub.dto.response.AdminStatsResponse;
import com.transithub.dto.response.AlertResponse;
import com.transithub.dto.response.ReportResponse;
import com.transithub.dto.response.RouteResponse;
import com.transithub.dto.response.StopResponse;
import com.transithub.dto.response.UserResponse;
import com.transithub.entity.enums.ReportStatus;
import com.transithub.security.AuthenticatedUser;
import com.transithub.service.AlertService;
import com.transithub.service.ReportService;
import com.transithub.service.RouteService;
import com.transithub.service.StatsService;
import com.transithub.service.StopService;
import com.transithub.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Data for the admin dashboard and admin tables (search + pagination).
 * Everything under /api/admin is restricted to the ADMIN role in SecurityConfig.
 */
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final StatsService statsService;
    private final RouteService routeService;
    private final StopService stopService;
    private final AlertService alertService;
    private final ReportService reportService;
    private final UserService userService;

    public AdminController(StatsService statsService,
                           RouteService routeService,
                           StopService stopService,
                           AlertService alertService,
                           ReportService reportService,
                           UserService userService) {
        this.statsService = statsService;
        this.routeService = routeService;
        this.stopService = stopService;
        this.alertService = alertService;
        this.reportService = reportService;
        this.userService = userService;
    }

    @GetMapping("/stats")
    public AdminStatsResponse getStats() {
        return statsService.getAdminStats();
    }

    /** GET /api/admin/routes?search=lipa&page=0&size=10 */
    @GetMapping("/routes")
    public PageResponse<RouteResponse> getRoutes(
            @RequestParam(name = "search", required = false) String search,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {
        return routeService.searchForAdmin(search, page, size);
    }

    @GetMapping("/stops")
    public PageResponse<StopResponse> getStops(
            @RequestParam(name = "search", required = false) String search,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {
        return stopService.searchForAdmin(search, page, size);
    }

    /** Every alert, including inactive ones. */
    @GetMapping("/alerts")
    public List<AlertResponse> getAlerts() {
        return alertService.getAllAlerts();
    }

    /** GET /api/admin/reports?status=OPEN (status is optional) */
    @GetMapping("/reports")
    public List<ReportResponse> getReports(@RequestParam(name = "status", required = false) ReportStatus status) {
        return reportService.getReports(status);
    }

    @PatchMapping("/reports/{id}/status")
    public ReportResponse updateReportStatus(@PathVariable("id") Long id,
                                             @Valid @RequestBody ReportStatusRequest request) {
        return reportService.updateStatus(id, request.status());
    }

    // ---------------- users ----------------

    /** GET /api/admin/users?search=ana&page=0&size=10 */
    @GetMapping("/users")
    public PageResponse<UserResponse> getUsers(
            @RequestParam(name = "search", required = false) String search,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {
        return userService.searchUsers(search, page, size);
    }

    @PatchMapping("/users/{id}/active")
    public UserResponse setUserActive(@AuthenticationPrincipal AuthenticatedUser admin,
                                      @PathVariable("id") Long id,
                                      @Valid @RequestBody UserActiveRequest request) {
        return userService.setActive(admin.id(), id, request.active());
    }

    @PatchMapping("/users/{id}/role")
    public UserResponse changeUserRole(@AuthenticationPrincipal AuthenticatedUser admin,
                                       @PathVariable("id") Long id,
                                       @Valid @RequestBody RoleChangeRequest request) {
        return userService.changeRole(admin.id(), id, request.role());
    }
}
