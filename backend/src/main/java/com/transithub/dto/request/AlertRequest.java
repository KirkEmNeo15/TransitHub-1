package com.transithub.dto.request;

import com.transithub.entity.enums.AlertSeverity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AlertRequest(
        @NotBlank(message = "Title cannot be empty")
        @Size(max = 150, message = "Title must be at most 150 characters")
        String title,

        @NotBlank(message = "Message cannot be empty")
        @Size(max = 1000, message = "Message must be at most 1000 characters")
        String message,

        @NotNull(message = "Severity is required (INFO, WARNING or CRITICAL)")
        AlertSeverity severity,

        Long routeId,      // optional: empty means a general announcement
        Boolean active) {  // optional: defaults to active
}
