package com.transithub.service;

import com.transithub.entity.Route;
import com.transithub.entity.enums.RouteStatus;
import com.transithub.exception.InvalidRequestException;
import com.transithub.repository.RouteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * First implementation of RouteSearchService: finds DIRECT routes only
 * (no transfers). Only ACTIVE routes are returned.
 */
@Service
public class DirectRouteSearchService implements RouteSearchService {

    private final RouteRepository routeRepository;

    public DirectRouteSearchService(RouteRepository routeRepository) {
        this.routeRepository = routeRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Route> findDirectRoutes(String origin, String destination) {
        if (origin == null || origin.isBlank()) {
            throw new InvalidRequestException("Origin cannot be empty");
        }
        if (destination == null || destination.isBlank()) {
            throw new InvalidRequestException("Destination cannot be empty");
        }
        String from = origin.trim();
        String to = destination.trim();
        if (from.equalsIgnoreCase(to)) {
            throw new InvalidRequestException("Origin and destination must be different");
        }
        return routeRepository.findDirectRoutes(from, to, RouteStatus.ACTIVE);
    }
}
