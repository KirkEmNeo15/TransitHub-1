package com.transithub.dto.response;

import java.time.Instant;

public record ReportResponse(
        Long id,
        Long routeId,
        String routeName,
        String reportedBy,
        String description,
        String status,
        Instant createdAt) {
}
