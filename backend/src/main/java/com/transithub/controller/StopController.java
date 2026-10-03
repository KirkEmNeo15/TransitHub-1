package com.transithub.controller;

import com.transithub.dto.request.StopRequest;
import com.transithub.dto.response.NearbyStopResponse;
import com.transithub.dto.response.RouteResponse;
import com.transithub.dto.response.StopResponse;
import com.transithub.service.StopService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/stops")
public class StopController {

    private final StopService stopService;

    public StopController(StopService stopService) {
        this.stopService = stopService;
    }

    /** GET /api/stops?search=lipa (search is optional) */
    @GetMapping
    public List<StopResponse> getStops(@RequestParam(name = "search", required = false) String search) {
        return stopService.getStops(search);
    }

    /** GET /api/stops/nearby?lat=13.94&lng=121.16&radiusKm=2 (nearest first) */
    @GetMapping("/nearby")
    public List<NearbyStopResponse> getNearbyStops(
            @RequestParam("lat") double latitude,
            @RequestParam("lng") double longitude,
            @RequestParam(name = "radiusKm", defaultValue = "2") double radiusKm) {
        return stopService.findNearbyStops(latitude, longitude, radiusKm);
    }

    @GetMapping("/{id}")
    public StopResponse getStop(@PathVariable("id") Long id) {
        return stopService.getStop(id);
    }

    /** The routes passing through this stop (shown when a stop is clicked on the map). */
    @GetMapping("/{id}/routes")
    public List<RouteResponse> getRoutesThroughStop(@PathVariable("id") Long id) {
        return stopService.getRoutesThroughStop(id);
    }

    @PostMapping
    public ResponseEntity<StopResponse> createStop(@Valid @RequestBody StopRequest request) {
        StopResponse created = stopService.createStop(request);
        return ResponseEntity.created(URI.create("/api/stops/" + created.id())).body(created);
    }

    @PutMapping("/{id}")
    public StopResponse updateStop(@PathVariable("id") Long id, @Valid @RequestBody StopRequest request) {
        return stopService.updateStop(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStop(@PathVariable("id") Long id) {
        stopService.deleteStop(id);
        return ResponseEntity.noContent().build();
    }
}
