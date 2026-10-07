package com.transithub.dto.response;

import java.util.Map;

/** "type" is the name each subclass reports, for example "Bus". "details" holds the type-specific values. */
public record TransportationResponse(
        Long id,
        String name,
        String code,
        String type,
        String description,
        Map<String, Object> details) {
}
