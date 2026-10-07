package com.transithub.dto.request;

import com.transithub.entity.enums.RouteStatus;
import jakarta.validation.constraints.NotNull;

/** Body of PATCH /api/routes/{id}/status. */
public record StatusChangeRequest(
        @NotNull(message = "Status is required (ACTIVE, INACTIVE or SUSPENDED)")
        RouteStatus status) {
}
