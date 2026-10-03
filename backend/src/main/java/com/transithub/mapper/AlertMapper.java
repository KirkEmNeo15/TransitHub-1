package com.transithub.mapper;

import com.transithub.dto.response.AlertResponse;
import com.transithub.entity.Alert;
import com.transithub.entity.Route;
import org.springframework.stereotype.Component;

@Component
public class AlertMapper {

    public AlertResponse toResponse(Alert alert) {
        Route route = alert.getRoute(); // null for a general announcement
        return new AlertResponse(
                alert.getId(),
                alert.getTitle(),
                alert.getMessage(),
                alert.getSeverity().name(),
                alert.isActive(),
                alert.getCreatedAt(),
                route == null ? null : route.getId(),
                route == null ? null : route.getRouteName());
    }
}
