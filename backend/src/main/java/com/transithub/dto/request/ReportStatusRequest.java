package com.transithub.dto.request;

import com.transithub.entity.enums.ReportStatus;
import jakarta.validation.constraints.NotNull;

/** Body of PATCH /api/admin/reports/{id}/status. */
public record ReportStatusRequest(
        @NotNull(message = "Status is required (OPEN, REVIEWED or RESOLVED)")
        ReportStatus status) {
}
