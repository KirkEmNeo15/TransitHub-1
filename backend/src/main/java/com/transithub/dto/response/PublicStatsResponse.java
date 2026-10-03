package com.transithub.dto.response;

/** Numbers shown on the home page. Nothing private here. */
public record PublicStatsResponse(
        long activeRoutes,
        long stops,
        long availableVehicles,
        long activeAlerts) {
}
