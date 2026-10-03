package com.transithub.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ReportRequest(
        @NotNull Long routeId,
        @NotBlank @Size(max = 1000) String description) {
}
