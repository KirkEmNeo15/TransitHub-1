package com.transithub.service;

import com.transithub.dto.CoordinateDto;
import com.transithub.dto.PageResponse;
import com.transithub.dto.request.RouteRequest;
import com.transithub.dto.request.RouteStopRequest;
import com.transithub.dto.request.ScheduleRequest;
import com.transithub.dto.response.RouteResponse;
import com.transithub.dto.response.RouteStopResponse;
import com.transithub.entity.Coordinate;
import com.transithub.entity.Fare;
import com.transithub.entity.Route;
import com.transithub.entity.Schedule;
import com.transithub.entity.Stop;
import com.transithub.entity.Transportation;
import com.transithub.entity.enums.RouteStatus;
import com.transithub.exception.DuplicateResourceException;
import com.transithub.exception.InvalidRouteException;
import com.transithub.exception.ResourceNotFoundException;
import com.transithub.mapper.RouteMapper;
import com.transithub.repository.RouteRepository;
import com.transithub.repository.StopRepository;
import com.transithub.repository.TransportationRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Business logic for routes: reading, searching, creating, updating and deleting.
 * Controllers call this class; this class calls the repositories.
 */
@Service
public class RouteService {

    private final RouteRepository routeRepository;
    private final StopRepository stopRepository;
    private final TransportationRepository transportationRepository;
    private final RouteSearchService routeSearchService; // the interface, not a specific class
    private final RouteMapper routeMapper;

    public RouteService(RouteRepository routeRepository,
                        StopRepository stopRepository,
                        TransportationRepository transportationRepository,
                        RouteSearchService routeSearchService,
                        RouteMapper routeMapper) {
        this.routeRepository = routeRepository;
        this.stopRepository = stopRepository;
        this.transportationRepository = transportationRepository;
        this.routeSearchService = routeSearchService;
        this.routeMapper = routeMapper;
    }

    // ------------------------------------------------------------------
    // Reading
    // ------------------------------------------------------------------

    /** All routes. Both filters are optional: type like "BUS", and a status. */
    @Transactional(readOnly = true)
    public List<RouteResponse> getRoutes(String type, RouteStatus status) {
        List<Route> routes = (status == null)
                ? routeRepository.findAllByOrderByRouteNameAsc()
                : routeRepository.findByStatusOrderByRouteNameAsc(status);

        return routes.stream()
                .filter(route -> type == null || type.isBlank()
                        || route.getTransportation().getTransportationType().equalsIgnoreCase(type.trim()))
                .map(routeMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public RouteResponse getRoute(Long id) {
        return routeMapper.toResponse(findRoute(id));
    }

    @Transactional(readOnly = true)
    public List<RouteStopResponse> getRouteStops(Long id) {
        return routeMapper.toStopResponses(findRoute(id));
    }

    /** Route search by origin and destination (active, direct routes, fastest first). */
    @Transactional(readOnly = true)
    public List<RouteResponse> searchRoutes(String origin, String destination) {
        return routeSearchService.findDirectRoutes(origin, destination).stream()
                .map(routeMapper::toResponse)
                .toList();
    }

    /** One page for the admin table, with an optional search text (name or code). */
    @Transactional(readOnly = true)
    public PageResponse<RouteResponse> searchForAdmin(String text, int page, int size) {
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 100), Sort.by("routeName"));
        String keyword = (text == null) ? "" : text.trim();

        Page<Route> result = routeRepository
                .findByRouteNameContainingIgnoreCaseOrRouteCodeContainingIgnoreCase(keyword, keyword, pageable);

        List<RouteResponse> items = result.getContent().stream().map(routeMapper::toResponse).toList();
        return new PageResponse<>(items, result.getNumber(), result.getSize(),
                result.getTotalElements(), result.getTotalPages());
    }

    // ------------------------------------------------------------------
    // Writing (admin)
    // ------------------------------------------------------------------

    @Transactional
    public RouteResponse createRoute(RouteRequest request) {
        String code = request.routeCode().trim();
        if (routeRepository.existsByRouteCode(code)) {
            throw new DuplicateResourceException("A route with code " + code + " already exists");
        }

        Route route = new Route(code, request.routeName(), request.origin(), request.destination(),
                request.estimatedMinutes(), request.distanceKm(), findTransportation(request.transportationId()));
        route.setStatus(request.status());
        applyStopsPathFareAndSchedules(route, request);

        return routeMapper.toResponse(routeRepository.save(route));
    }

    @Transactional
    public RouteResponse updateRoute(Long id, RouteRequest request) {
        Route route = findRoute(id);

        String code = request.routeCode().trim();
        if (!route.getRouteCode().equals(code) && routeRepository.existsByRouteCode(code)) {
            throw new DuplicateResourceException("A route with code " + code + " already exists");
        }

        route.setRouteCode(code);
        route.setRouteName(request.routeName());
        route.setEndpoints(request.origin(), request.destination());
        route.setStatus(request.status());
        route.setEstimatedMinutes(request.estimatedMinutes());
        route.setDistanceKm(request.distanceKm());
        route.setTransportation(findTransportation(request.transportationId()));
        applyStopsPathFareAndSchedules(route, request);

        return routeMapper.toResponse(routeRepository.save(route));
    }

    /** Admin action "change route status" (for example to SUSPENDED). */
    @Transactional
    public RouteResponse changeStatus(Long id, RouteStatus status) {
        Route route = findRoute(id);
        route.setStatus(status);
        return routeMapper.toResponse(route);
    }

    @Transactional
    public void deleteRoute(Long id) {
        // The route's stops, fare and schedules are deleted with it (cascade)
        routeRepository.delete(findRoute(id));
    }

    // ------------------------------------------------------------------
    // Helpers
    // ------------------------------------------------------------------

    private Route findRoute(Long id) {
        return routeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Route", id));
    }

    private Transportation findTransportation(Long id) {
        return transportationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transportation", id));
    }

    /** Fills in the parts of a route that come from lists: stops, map line, fare, schedules. */
    private void applyStopsPathFareAndSchedules(Route route, RouteRequest request) {
        // ----- stops -----
        List<RouteStopRequest> stopRequests = request.stops();
        if (stopRequests == null || stopRequests.size() < 2) {
            throw new InvalidRouteException("A route needs at least 2 stops");
        }
        List<Long> stopIds = stopRequests.stream().map(RouteStopRequest::stopId).toList();
        if (new HashSet<>(stopIds).size() != stopIds.size()) {
            throw new InvalidRouteException("The same stop cannot appear twice in one route");
        }
        Map<Long, Stop> stopsById = stopRepository.findAllById(stopIds).stream()
                .collect(Collectors.toMap(Stop::getId, Function.identity()));
        for (Long stopId : stopIds) {
            if (!stopsById.containsKey(stopId)) {
                throw new InvalidRouteException("Stop not found with id " + stopId);
            }
        }

        // Remove the old stops and save that first. Otherwise the database would see the new
        // stops being inserted before the old ones are deleted and reject the duplicate order numbers.
        route.clearRouteStops();
        if (route.getId() != null) {
            routeRepository.flush();
        }
        List<Coordinate> pathFromStops = new ArrayList<>();
        for (int i = 0; i < stopRequests.size(); i++) {
            Stop stop = stopsById.get(stopRequests.get(i).stopId());
            route.addRouteStop(stop, i + 1, stopRequests.get(i).minutesFromStart());
            pathFromStops.add(new Coordinate(stop.getLatitude(), stop.getLongitude()));
        }

        // ----- map line: use the given points, or draw through the stops -----
        List<CoordinateDto> requestedPath = request.path();
        if (requestedPath == null || requestedPath.isEmpty()) {
            route.setPath(pathFromStops);
        } else if (requestedPath.size() < 2) {
            throw new InvalidRouteException("The map line needs at least 2 points");
        } else {
            route.setPath(requestedPath.stream()
                    .map(point -> new Coordinate(point.latitude(), point.longitude()))
                    .toList());
        }

        // ----- fare: update the existing one, or create it -----
        Fare fare = route.getFare();
        if (fare == null) {
            route.setFare(new Fare(request.fare().baseFare(), request.fare().perKmRate()));
        } else {
            fare.setBaseFare(request.fare().baseFare());
            fare.setPerKmRate(request.fare().perKmRate());
        }

        // ----- schedules: replace them all -----
        route.clearSchedules();
        if (request.schedules() != null) {
            for (ScheduleRequest schedule : request.schedules()) {
                route.addSchedule(new Schedule(schedule.firstTrip(), schedule.lastTrip(),
                        schedule.frequencyMinutes(), schedule.daysOperating()));
            }
        }
    }
}
