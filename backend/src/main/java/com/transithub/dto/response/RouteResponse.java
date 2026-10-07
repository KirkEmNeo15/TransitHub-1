package com.transithub.dto.response;

import com.transithub.dto.CoordinateDto;

import java.math.BigDecimal;
import java.util.List;

/**
 * Everything the app needs to show one route: details, stops, the map line, fare and schedules.
 * estimatedFare is calculated by the route's transportation type (polymorphism).
 */
public record RouteResponse(
        Long id,
        String routeCode,
        String routeName,
        String origin,
        String destination,
        String status,
        int estimatedMinutes,
        double distanceKm,
        boolean demoData,
        TransportationResponse transportation,
        BigDecimal estimatedFare,
        FareResponse fareRule,
        List<RouteStopResponse> stops,
        List<CoordinateDto> path,
        List<ScheduleResponse> schedules) {
}
