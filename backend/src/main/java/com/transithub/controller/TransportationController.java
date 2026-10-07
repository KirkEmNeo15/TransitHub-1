package com.transithub.controller;

import com.transithub.dto.request.TransportationRequest;
import com.transithub.dto.response.TransportationResponse;
import com.transithub.service.TransportationService;
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
@RequestMapping("/api/transportations")
public class TransportationController {

    private final TransportationService transportationService;

    public TransportationController(TransportationService transportationService) {
        this.transportationService = transportationService;
    }

    /** GET /api/transportations?type=BUS (type is optional) */
    @GetMapping
    public List<TransportationResponse> getAll(@RequestParam(name = "type", required = false) String type) {
        return transportationService.getAll(type);
    }

    @GetMapping("/{id}")
    public TransportationResponse getById(@PathVariable("id") Long id) {
        return transportationService.getById(id);
    }

    @PostMapping
    public ResponseEntity<TransportationResponse> create(@Valid @RequestBody TransportationRequest request) {
        TransportationResponse created = transportationService.create(request);
        return ResponseEntity.created(URI.create("/api/transportations/" + created.id())).body(created);
    }

    @PutMapping("/{id}")
    public TransportationResponse update(@PathVariable("id") Long id,
                                         @Valid @RequestBody TransportationRequest request) {
        return transportationService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        transportationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
