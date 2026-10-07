package com.transithub.dto.response;

import java.time.Instant;

/** A user as the frontend sees it. The password hash is never included. */
public record UserResponse(
        Long id,
        String fullName,
        String email,
        String role,
        boolean active,
        Instant createdAt) {
}
