package com.gamehub.ai_integration;

import java.io.IOException;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gamehub.exception.ResourceNotFoundException;

@RestController
@RequestMapping("/ai/players/{playerId}/teams/{teamId}/analysis")
@ConditionalOnProperty(name = "gamehub.ai.enabled", havingValue = "true")
public class PlayerAnalysisController {
    private final PlayerAnalysisService analysisService;
    private final PlayerAnalysisRepository analysisRepository;

    public PlayerAnalysisController(PlayerAnalysisService analysisService, PlayerAnalysisRepository analysisRepository) {
        this.analysisService = analysisService;
        this.analysisRepository = analysisRepository;
    }

    @GetMapping
    public PlayerAnalysisRepository.SavedAnalysis getLatest(@PathVariable Long playerId, @PathVariable Long teamId) {
        return analysisRepository.find(playerId, teamId)
                .orElseThrow(() -> new ResourceNotFoundException("No saved analysis for this player and team"));
    }

    @PostMapping
    public AnalysisResponse generate(@PathVariable Long playerId, @PathVariable Long teamId,
            @Valid @RequestBody(required = false) AnalysisRequest request) throws IOException {
        String question = request == null ? "Compare this player with their teammates." : request.question();
        return new AnalysisResponse(analysisService.generate(playerId, teamId, question));
    }

    public record AnalysisRequest(@NotBlank @Size(max = 1000) String question) {
    }

    public record AnalysisResponse(String analysis) {
    }
}
