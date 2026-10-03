package com.transithub.dto.response;

import java.time.Instant;

public record AlertResponse(
        Long id,
        String title,
        String message,
        String severity,
        boolean active,
        Instant createdAt,
        Long routeId,
        String routeName) {
}
