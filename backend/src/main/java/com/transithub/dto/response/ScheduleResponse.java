package com.transithub.dto.response;

import java.time.LocalTime;

public record ScheduleResponse(
        LocalTime firstTrip,
        LocalTime lastTrip,
        int frequencyMinutes,
        String daysOperating) {
}
