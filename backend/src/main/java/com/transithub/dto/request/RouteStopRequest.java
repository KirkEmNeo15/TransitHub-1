package com.transithub.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/** One stop of a route. The ORDER of the list in the request is the order of the stops. */
public record RouteStopRequest(
        @NotNull(message = "Stop id is required")
        Long stopId,

        @NotNull(message = "Minutes from start is required")
        @Min(value = 0, message = "Minutes from start cannot be negative")
        Integer minutesFromStart) {
}
