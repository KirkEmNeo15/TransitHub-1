package com.transithub.controller;

import com.transithub.dto.request.RouteRequest;
import com.transithub.dto.request.StatusChangeRequest;
import com.transithub.dto.response.RouteResponse;
import com.transithub.dto.response.RouteStopResponse;
import com.transithub.entity.enums.RouteStatus;
import com.transithub.service.RouteService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

/**
 * REST endpoints for routes. A controller only receives the request, checks the
 * input (@Valid), calls the service, and chooses the HTTP status. No business logic here.
 */
@RestController
@RequestMapping("/api/routes")
public class RouteController {

    private final RouteService routeService;

    public RouteController(RouteService routeService) {
        this.routeService = routeService;
    }

    /** GET /api/routes?type=BUS&status=ACTIVE (both filters are optional) */
    @GetMapping
    public List<RouteResponse> getRoutes(
            @RequestParam(name = "type", required = false) String type,
            @RequestParam(name = "status", required = false) RouteStatus status) {
        return routeService.getRoutes(type, status);
    }

    /** GET /api/routes/search?origin=Lipa&destination=Batangas */
    @GetMapping("/search")
    public List<RouteResponse> search(
            @RequestParam("origin") String origin,
            @RequestParam("destination") String destination) {
        return routeService.searchRoutes(origin, destination);
    }

    @GetMapping("/{id}")
    public RouteResponse getRoute(@PathVariable("id") Long id) {
        return routeService.getRoute(id);
    }

    @GetMapping("/{id}/stops")
    public List<RouteStopResponse> getRouteStops(@PathVariable("id") Long id) {
        return routeService.getRouteStops(id);
    }

    /** Returns 201 Created and the address of the new route in the Location header. */
    @PostMapping
    public ResponseEntity<RouteResponse> createRoute(@Valid @RequestBody RouteRequest request) {
        RouteResponse created = routeService.createRoute(request);
        return ResponseEntity.created(URI.create("/api/routes/" + created.id())).body(created);
    }

    @PutMapping("/{id}")
    public RouteResponse updateRoute(@PathVariable("id") Long id, @Valid @RequestBody RouteRequest request) {
        return routeService.updateRoute(id, request);
    }

    /** Changes only the status (for example ACTIVE to SUSPENDED). */
    @PatchMapping("/{id}/status")
    public RouteResponse changeStatus(@PathVariable("id") Long id, @Valid @RequestBody StatusChangeRequest request) {
        return routeService.changeStatus(id, request.status());
    }

    /** Returns 204 No Content: it worked and there is nothing to send back. */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoute(@PathVariable("id") Long id) {
        routeService.deleteRoute(id);
        return ResponseEntity.noContent().build();
    }
}
