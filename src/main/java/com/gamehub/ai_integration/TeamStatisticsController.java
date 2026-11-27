package com.gamehub.ai_integration;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/players/{playerId}/teams/{teamId}")
public class TeamStatisticsController {

    private final TeamStatisticsService statisticsService;

    public TeamStatisticsController(TeamStatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping("/statistics")
    public List<TeamStatisticsService.PlayerStatistics> getStatistics(
            @PathVariable Long playerId, @PathVariable Long teamId) {
        return statisticsService.getStatistics(playerId, teamId);
    }
}
