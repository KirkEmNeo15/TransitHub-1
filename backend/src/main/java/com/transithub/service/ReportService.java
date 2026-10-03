package com.transithub.service;

import com.transithub.dto.request.ReportRequest;
import com.transithub.dto.response.ReportResponse;
import com.transithub.entity.Report;
import com.transithub.entity.Route;
import com.transithub.entity.User;
import com.transithub.entity.enums.ReportStatus;
import com.transithub.exception.ResourceNotFoundException;
import com.transithub.mapper.ReportMapper;
import com.transithub.repository.ReportRepository;
import com.transithub.repository.RouteRepository;
import com.transithub.repository.UserRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** Users report wrong route information; admins review the reports. */
@Service
public class ReportService {

    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final RouteRepository routeRepository;
    private final ReportMapper reportMapper;

    public ReportService(ReportRepository reportRepository,
                         UserRepository userRepository,
                         RouteRepository routeRepository,
                         ReportMapper reportMapper) {
        this.reportRepository = reportRepository;
        this.userRepository = userRepository;
        this.routeRepository = routeRepository;
        this.reportMapper = reportMapper;
    }

    @Transactional
    public ReportResponse submitReport(Long userId, ReportRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));
        Route route = routeRepository.findById(request.routeId())
                .orElseThrow(() -> new ResourceNotFoundException("Route", request.routeId()));

        Report report = new Report(user, route, request.description());
        return reportMapper.toResponse(reportRepository.save(report));
    }

    /** The reports sent by one user, newest first. */
    @Transactional(readOnly = true)
    public List<ReportResponse> getMyReports(Long userId) {
        return reportRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(reportMapper::toResponse)
                .toList();
    }

    /** For admins: all reports, or only those with a given status. Newest first. */
    @Transactional(readOnly = true)
    public List<ReportResponse> getReports(ReportStatus status) {
        List<Report> reports = (status == null)
                ? reportRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"))
                : reportRepository.findByStatusOrderByCreatedAtDesc(status);
        return reports.stream().map(reportMapper::toResponse).toList();
    }

    @Transactional
    public ReportResponse updateStatus(Long reportId, ReportStatus status) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new ResourceNotFoundException("Report", reportId));
        report.setStatus(status);
        return reportMapper.toResponse(report);
    }
}
