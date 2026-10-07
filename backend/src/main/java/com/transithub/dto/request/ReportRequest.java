package com.transithub.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ReportRequest(
        @NotNull(message = "Route is required")
        Long routeId,

        @NotBlank(message = "Description cannot be empty")
        @Size(max = 1000, message = "Description must be at most 1000 characters")
        String description) {
}
