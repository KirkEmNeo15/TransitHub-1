package com.transithub.dto.request;

import com.transithub.dto.CoordinateDto;
import com.transithub.entity.enums.RouteStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.List;

/** Data an admin sends to create or update a route. */
public record RouteRequest(
        @NotBlank(message = "Route code cannot be empty")
        @Size(max = 30, message = "Route code must be at most 30 characters")
        String routeCode,

        @NotBlank(message = "Route name cannot be empty")
        @Size(max = 150, message = "Route name must be at most 150 characters")
        String routeName,

        @NotBlank(message = "Origin cannot be empty")
        @Size(max = 100, message = "Origin must be at most 100 characters")
        String origin,

        @NotBlank(message = "Destination cannot be empty")
        @Size(max = 100, message = "Destination must be at most 100 characters")
        String destination,

        @NotNull(message = "Status is required (ACTIVE, INACTIVE or SUSPENDED)")
        RouteStatus status,

        @NotNull(message = "Estimated travel time is required")
        @Min(value = 1, message = "Estimated travel time must be at least 1 minute")
        Integer estimatedMinutes,

        @NotNull(message = "Distance is required")
        @Positive(message = "Distance must be greater than 0")
        Double distanceKm,

        @NotNull(message = "Transportation is required")
        Long transportationId,

        @NotNull(message = "Fare is required")
        @Valid
        FareRequest fare,

        @NotEmpty(message = "A route needs at least 2 stops")
        @Valid
        List<RouteStopRequest> stops,

        // optional: when empty, the map line is drawn through the stops
        @Valid
        List<CoordinateDto> path,

        // optional
        @Valid
        List<ScheduleRequest> schedules) {
}
