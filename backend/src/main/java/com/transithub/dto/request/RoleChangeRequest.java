package com.transithub.dto.request;

import com.transithub.entity.enums.Role;
import jakarta.validation.constraints.NotNull;

/** Body of PATCH /api/admin/users/{id}/role. */
public record RoleChangeRequest(
        @NotNull(message = "Role is required (USER or ADMIN)") Role role) {
}
