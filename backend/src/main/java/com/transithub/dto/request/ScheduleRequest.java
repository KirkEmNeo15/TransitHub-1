package com.transithub.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalTime;

public record ScheduleRequest(
        @NotNull LocalTime firstTrip,
        @NotNull LocalTime lastTrip,
        @NotNull @Min(1) Integer frequencyMinutes,
        @NotBlank @Size(max = 50) String daysOperating) {
}
