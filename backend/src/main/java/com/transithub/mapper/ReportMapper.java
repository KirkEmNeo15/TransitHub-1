package com.transithub.mapper;

import com.transithub.dto.response.ReportResponse;
import com.transithub.entity.Report;
import org.springframework.stereotype.Component;

@Component
public class ReportMapper {

    public ReportResponse toResponse(Report report) {
        return new ReportResponse(
                report.getId(),
                report.getRoute().getId(),
                report.getRoute().getRouteName(),
                report.getUser().getEmail(),
                report.getDescription(),
                report.getStatus().name(),
                report.getCreatedAt());
    }
}
