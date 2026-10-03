package com.transithub.service;

import com.transithub.dto.request.AlertRequest;
import com.transithub.dto.response.AlertResponse;
import com.transithub.entity.Alert;
import com.transithub.entity.Route;
import com.transithub.exception.ResourceNotFoundException;
import com.transithub.mapper.AlertMapper;
import com.transithub.repository.AlertRepository;
import com.transithub.repository.RouteRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AlertService {

    private final AlertRepository alertRepository;
    private final RouteRepository routeRepository;
    private final AlertMapper alertMapper;

    public AlertService(AlertRepository alertRepository, RouteRepository routeRepository, AlertMapper alertMapper) {
        this.alertRepository = alertRepository;
        this.routeRepository = routeRepository;
        this.alertMapper = alertMapper;
    }

    /** Alerts the public sees: active only, newest first. */
    @Transactional(readOnly = true)
    public List<AlertResponse> getActiveAlerts() {
        return alertRepository.findByActiveTrueOrderByCreatedAtDesc().stream()
                .map(alertMapper::toResponse)
                .toList();
    }

    /** Every alert, including inactive ones (for admins). */
    @Transactional(readOnly = true)
    public List<AlertResponse> getAllAlerts() {
        return alertRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt")).stream()
                .map(alertMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public AlertResponse getAlert(Long id) {
        return alertMapper.toResponse(find(id));
    }

    @Transactional
    public AlertResponse createAlert(AlertRequest request) {
        Alert alert = new Alert(request.title(), request.message(), request.severity());
        alert.setActive(request.active() == null || request.active());
        alert.setRoute(findRouteOrNull(request.routeId()));
        return alertMapper.toResponse(alertRepository.save(alert));
    }

    @Transactional
    public AlertResponse updateAlert(Long id, AlertRequest request) {
        Alert alert = find(id);
        alert.setTitle(request.title());
        alert.setMessage(request.message());
        alert.setSeverity(request.severity());
        alert.setActive(request.active() == null || request.active());
        alert.setRoute(findRouteOrNull(request.routeId()));
        return alertMapper.toResponse(alertRepository.save(alert));
    }

    @Transactional
    public void deleteAlert(Long id) {
        alertRepository.delete(find(id));
    }

    private Alert find(Long id) {
        return alertRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Alert", id));
    }

    private Route findRouteOrNull(Long routeId) {
        if (routeId == null) {
            return null;
        }
        return routeRepository.findById(routeId)
                .orElseThrow(() -> new ResourceNotFoundException("Route", routeId));
    }
}
