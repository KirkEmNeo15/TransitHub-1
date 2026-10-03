package com.transithub.dto.response;

/** Numbers shown on the admin dashboard. */
public record AdminStatsResponse(
        long totalRoutes,
        long activeRoutes,
        long totalStops,
        long totalVehicles,
        long availableVehicles,
        long totalUsers,
        long activeAlerts,
        long openReports) {
}
