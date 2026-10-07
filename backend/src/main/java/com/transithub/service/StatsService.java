package com.transithub.service;

import com.transithub.dto.response.AdminStatsResponse;
import com.transithub.dto.response.PublicStatsResponse;
import com.transithub.entity.enums.ReportStatus;
import com.transithub.entity.enums.RouteStatus;
import com.transithub.entity.enums.VehicleStatus;
import com.transithub.repository.AlertRepository;
import com.transithub.repository.ReportRepository;
import com.transithub.repository.RouteRepository;
import com.transithub.repository.StopRepository;
import com.transithub.repository.UserRepository;
import com.transithub.repository.VehicleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** The numbers shown on the home page and on the admin dashboard. */
@Service
public class StatsService {

    private final RouteRepository routeRepository;
    private final StopRepository stopRepository;
    private final VehicleRepository vehicleRepository;
    private final UserRepository userRepository;
    private final AlertRepository alertRepository;
    private final ReportRepository reportRepository;

    public StatsService(RouteRepository routeRepository,
                        StopRepository stopRepository,
                        VehicleRepository vehicleRepository,
                        UserRepository userRepository,
                        AlertRepository alertRepository,
                        ReportRepository reportRepository) {
        this.routeRepository = routeRepository;
        this.stopRepository = stopRepository;
        this.vehicleRepository = vehicleRepository;
        this.userRepository = userRepository;
        this.alertRepository = alertRepository;
        this.reportRepository = reportRepository;
    }

    @Transactional(readOnly = true)
    public PublicStatsResponse getPublicStats() {
        return new PublicStatsResponse(
                routeRepository.countByStatus(RouteStatus.ACTIVE),
                stopRepository.count(),
                vehicleRepository.countByStatus(VehicleStatus.ACTIVE),
                alertRepository.countByActiveTrue());
    }

    @Transactional(readOnly = true)
    public AdminStatsResponse getAdminStats() {
        return new AdminStatsResponse(
                routeRepository.count(),
                routeRepository.countByStatus(RouteStatus.ACTIVE),
                stopRepository.count(),
                vehicleRepository.count(),
                vehicleRepository.countByStatus(VehicleStatus.ACTIVE),
                userRepository.count(),
                alertRepository.countByActiveTrue(),
                reportRepository.countByStatus(ReportStatus.OPEN));
    }
}
