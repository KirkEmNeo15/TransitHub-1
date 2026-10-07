package com.transithub.dto.response;

public record RouteStopResponse(int stopOrder, int minutesFromStart, StopResponse stop) {
}
