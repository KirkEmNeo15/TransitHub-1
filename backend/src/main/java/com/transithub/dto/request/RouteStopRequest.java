package com.transithub.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/** One stop of a route. The ORDER of the list in the request is the order of the stops. */
public record RouteStopRequest(
        @NotNull Long stopId,
        @NotNull @Min(0) Integer minutesFromStart) {
}
