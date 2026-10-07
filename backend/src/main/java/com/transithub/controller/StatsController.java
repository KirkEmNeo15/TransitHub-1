package com.transithub.controller;

import com.transithub.dto.response.PublicStatsResponse;
import com.transithub.service.StatsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** The numbers on the home page. Nothing private. */
@RestController
@RequestMapping("/api/stats")
public class StatsController {

    private final StatsService statsService;

    public StatsController(StatsService statsService) {
        this.statsService = statsService;
    }

    @GetMapping
    public PublicStatsResponse getStats() {
        return statsService.getPublicStats();
    }
}
