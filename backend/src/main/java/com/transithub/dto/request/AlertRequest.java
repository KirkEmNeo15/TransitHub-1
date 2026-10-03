package com.transithub.dto.request;

import com.transithub.entity.enums.AlertSeverity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AlertRequest(
        @NotBlank @Size(max = 150) String title,
        @NotBlank @Size(max = 1000) String message,
        @NotNull AlertSeverity severity,
        Long routeId,      // optional: empty means a general announcement
        Boolean active) {  // optional: defaults to active
}
