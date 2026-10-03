package com.transithub.dto.response;

public record StopResponse(
        Long id,
        String name,
        String description,
        double latitude,
        double longitude,
        boolean demoData) {
}
