package com.transithub.dto.response;

import java.time.Instant;
import java.util.List;

/**
 * The ONE format used for every error the API returns.
 * "errors" lists the invalid fields for validation problems and is an empty list otherwise.
 */
public record ApiErrorResponse(
        int status,
        String message,
        Instant timestamp,
        String path,
        List<FieldErrorDetail> errors) {
}
