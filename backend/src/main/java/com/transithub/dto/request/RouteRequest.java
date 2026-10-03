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
        @NotBlank @Size(max = 30) String routeCode,
        @NotBlank @Size(max = 150) String routeName,
        @NotBlank @Size(max = 100) String origin,
        @NotBlank @Size(max = 100) String destination,
        @NotNull RouteStatus status,
        @NotNull @Min(1) Integer estimatedMinutes,
        @NotNull @Positive Double distanceKm,
        @NotNull Long transportationId,
        @NotNull @Valid FareRequest fare,
        @NotEmpty(message = "A route needs at least 2 stops") @Valid List<RouteStopRequest> stops,
        // optional: when empty, the map line is drawn through the stops
        @Valid List<CoordinateDto> path,
        // optional
        @Valid List<ScheduleRequest> schedules) {
}
