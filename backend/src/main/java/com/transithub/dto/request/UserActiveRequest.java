package com.transithub.dto.request;

import jakarta.validation.constraints.NotNull;

/** Body of PATCH /api/admin/users/{id}/active. */
public record UserActiveRequest(
        @NotNull(message = "Active is required (true or false)") Boolean active) {
}
