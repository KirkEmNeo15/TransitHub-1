package com.transithub.dto;

/**
 * Data sent back by GET /api/health.
 * A record is a short way to write a class that only carries data:
 * Java creates the constructor and getters for us.
 */
public record HealthResponse(
        String application,
        String status,
        String database,
        Long routesInDatabase) {
}
