package com.transithub.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalTime;

public record ScheduleRequest(
        @NotNull(message = "First trip time is required")
        LocalTime firstTrip,

        @NotNull(message = "Last trip time is required")
        LocalTime lastTrip,

        @NotNull(message = "Frequency is required")
        @Min(value = 1, message = "Frequency must be at least 1 minute")
        Integer frequencyMinutes,

        @NotBlank(message = "Days operating cannot be empty")
        @Size(max = 50, message = "Days operating must be at most 50 characters")
        String daysOperating) {
}
