package com.transithub.controller;

import com.transithub.dto.request.ReportRequest;
import com.transithub.dto.response.ReportResponse;
import com.transithub.security.AuthenticatedUser;
import com.transithub.service.ReportService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** A logged-in user reports incorrect route information. Admins review reports in /api/admin/reports. */
@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @PostMapping
    public ResponseEntity<ReportResponse> submitReport(@AuthenticationPrincipal AuthenticatedUser currentUser,
                                                       @Valid @RequestBody ReportRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reportService.submitReport(currentUser.id(), request));
    }

    /** The reports I sent. */
    @GetMapping("/mine")
    public List<ReportResponse> getMyReports(@AuthenticationPrincipal AuthenticatedUser currentUser) {
        return reportService.getMyReports(currentUser.id());
    }
}
