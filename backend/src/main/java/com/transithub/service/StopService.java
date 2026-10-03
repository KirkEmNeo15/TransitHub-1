package com.transithub.service;

import com.transithub.dto.PageResponse;
import com.transithub.dto.request.StopRequest;
import com.transithub.dto.response.NearbyStopResponse;
import com.transithub.dto.response.RouteResponse;
import com.transithub.dto.response.StopResponse;
import com.transithub.entity.Stop;
import com.transithub.exception.DuplicateResourceException;
import com.transithub.exception.InvalidRequestException;
import com.transithub.exception.ResourceInUseException;
import com.transithub.exception.ResourceNotFoundException;
import com.transithub.mapper.RouteMapper;
import com.transithub.mapper.StopMapper;
import com.transithub.repository.RouteRepository;
import com.transithub.repository.StopRepository;
import com.transithub.util.GeoUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class StopService {

    private static final double MAX_NEARBY_RADIUS_KM = 50;

    private final StopRepository stopRepository;
    private final RouteRepository routeRepository;
    private final StopMapper stopMapper;
    private final RouteMapper routeMapper;

    public StopService(StopRepository stopRepository,
                       RouteRepository routeRepository,
                       StopMapper stopMapper,
                       RouteMapper routeMapper) {
        this.stopRepository = stopRepository;
        this.routeRepository = routeRepository;
        this.stopMapper = stopMapper;
        this.routeMapper = routeMapper;
    }

    /** All stops, or only those whose name contains the search text. */
    @Transactional(readOnly = true)
    public List<StopResponse> getStops(String search) {
        List<Stop> stops = (search == null || search.isBlank())
                ? stopRepository.findAll(Sort.by("name"))
                : stopRepository.findByNameContainingIgnoreCase(search.trim());
        return stops.stream().map(stopMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public StopResponse getStop(Long id) {
        return stopMapper.toResponse(findStop(id));
    }

    /** "Routes passing through this stop" (shown when a stop is clicked on the map). */
    @Transactional(readOnly = true)
    public List<RouteResponse> getRoutesThroughStop(Long stopId) {
        findStop(stopId); // 404 if the stop does not exist
        return routeRepository.findRoutesThroughStop(stopId).stream()
                .map(routeMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public PageResponse<StopResponse> searchForAdmin(String text, int page, int size) {
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 100), Sort.by("name"));
        Page<Stop> result = stopRepository.findByNameContainingIgnoreCase(text == null ? "" : text.trim(), pageable);

        List<StopResponse> items = result.getContent().stream().map(stopMapper::toResponse).toList();
        return new PageResponse<>(items, result.getNumber(), result.getSize(),
                result.getTotalElements(), result.getTotalPages());
    }

    /**
     * Stops within radiusKm of a point, nearest first.
     * Step 1: the database returns the stops inside a rectangle (fast).
     * Step 2: Java measures the exact distance, keeps the close ones and sorts them.
     */
    @Transactional(readOnly = true)
    public List<NearbyStopResponse> findNearbyStops(double latitude, double longitude, double radiusKm) {
        if (latitude < -90 || latitude > 90) {
            throw new InvalidRequestException("Latitude must be between -90 and 90");
        }
        if (longitude < -180 || longitude > 180) {
            throw new InvalidRequestException("Longitude must be between -180 and 180");
        }
        if (radiusKm <= 0 || radiusKm > MAX_NEARBY_RADIUS_KM) {
            throw new InvalidRequestException("Radius must be more than 0 and at most " + MAX_NEARBY_RADIUS_KM + " km");
        }

        double latitudeMargin = GeoUtils.kmToLatitudeDegrees(radiusKm);
        double longitudeMargin = GeoUtils.kmToLongitudeDegrees(radiusKm, latitude);
        List<Stop> candidates = stopRepository.findInBoundingBox(
                latitude - latitudeMargin, latitude + latitudeMargin,
                longitude - longitudeMargin, longitude + longitudeMargin);

        List<NearbyStopResponse> nearby = new ArrayList<>();
        for (Stop stop : candidates) {
            double distance = GeoUtils.distanceKm(latitude, longitude, stop.getLatitude(), stop.getLongitude());
            if (distance <= radiusKm) {
                nearby.add(new NearbyStopResponse(stopMapper.toResponse(stop), Math.round(distance * 100) / 100.0));
            }
        }
        nearby.sort(Comparator.comparingDouble(NearbyStopResponse::distanceKm));
        return nearby;
    }

    @Transactional
    public StopResponse createStop(StopRequest request) {
        String name = request.name().trim();
        if (stopRepository.existsByNameIgnoreCase(name)) {
            throw new DuplicateResourceException("A stop named \"" + name + "\" already exists");
        }
        Stop stop = new Stop(name, request.description(), request.latitude(), request.longitude());
        return stopMapper.toResponse(stopRepository.save(stop));
    }

    @Transactional
    public StopResponse updateStop(Long id, StopRequest request) {
        Stop stop = findStop(id);

        String name = request.name().trim();
        if (!stop.getName().equalsIgnoreCase(name) && stopRepository.existsByNameIgnoreCase(name)) {
            throw new DuplicateResourceException("A stop named \"" + name + "\" already exists");
        }
        stop.setName(name);
        stop.setDescription(request.description());
        stop.setLatitude(request.latitude());
        stop.setLongitude(request.longitude());
        return stopMapper.toResponse(stopRepository.save(stop));
    }

    @Transactional
    public void deleteStop(Long id) {
        Stop stop = findStop(id);
        if (!routeRepository.findRoutesThroughStop(id).isEmpty()) {
            throw new ResourceInUseException("This stop is used by one or more routes. Remove it from them first.");
        }
        stopRepository.delete(stop);
    }

    private Stop findStop(Long id) {
        return stopRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Stop", id));
    }
}
