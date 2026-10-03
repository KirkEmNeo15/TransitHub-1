package com.transithub.controller;

import com.transithub.dto.request.AlertRequest;
import com.transithub.dto.response.AlertResponse;
import com.transithub.service.AlertService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/alerts")
public class AlertController {

    private final AlertService alertService;

    public AlertController(AlertService alertService) {
        this.alertService = alertService;
    }

    /** The alerts everyone can see: active ones, newest first. */
    @GetMapping
    public List<AlertResponse> getActiveAlerts() {
        return alertService.getActiveAlerts();
    }

    @GetMapping("/{id}")
    public AlertResponse getAlert(@PathVariable("id") Long id) {
        return alertService.getAlert(id);
    }

    @PostMapping
    public ResponseEntity<AlertResponse> createAlert(@Valid @RequestBody AlertRequest request) {
        AlertResponse created = alertService.createAlert(request);
        return ResponseEntity.created(URI.create("/api/alerts/" + created.id())).body(created);
    }

    @PutMapping("/{id}")
    public AlertResponse updateAlert(@PathVariable("id") Long id, @Valid @RequestBody AlertRequest request) {
        return alertService.updateAlert(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAlert(@PathVariable("id") Long id) {
        alertService.deleteAlert(id);
        return ResponseEntity.noContent().build();
    }
}
