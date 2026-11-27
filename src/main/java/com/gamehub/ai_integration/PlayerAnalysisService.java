package com.gamehub.ai_integration;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(name = "gamehub.ai.enabled", havingValue = "true")
public class PlayerAnalysisService {
    private final TeamStatisticsService statisticsService;
    private final OllamaChatModel chatModel;
    private final PlayerAnalysisRepository analysisRepository;
    private final Resource systemPrompt;
    private final Resource taskPrompt;
    private final Resource gameInfo;

    public PlayerAnalysisService(TeamStatisticsService statisticsService, OllamaChatModel chatModel,
            PlayerAnalysisRepository analysisRepository,
            @Value("classpath:ai/player-analysis.txt") Resource systemPrompt,
            @Value("classpath:ai/player-analysis-task.txt") Resource taskPrompt,
            @Value("classpath:ai/general_leage_info.txt") Resource gameInfo) {
        this.statisticsService = statisticsService;
        this.chatModel = chatModel;
        this.analysisRepository = analysisRepository;
        this.systemPrompt = systemPrompt;
        this.taskPrompt = taskPrompt;
        this.gameInfo = gameInfo;
    }

    public String generate(Long playerId, Long teamId, String question) throws IOException {
        List<TeamStatisticsService.PlayerStatistics> statistics = statisticsService.getStatistics(playerId, teamId);
        TeamStatisticsService.TeamWinRates winRates = statisticsService.getTeamWinRates(teamId);
        String systemInstructions = systemPrompt.getContentAsString(StandardCharsets.UTF_8);
        String task = taskPrompt.getContentAsString(StandardCharsets.UTF_8);
        String gameContext = gameInfo.getContentAsString(StandardCharsets.UTF_8);
        String userMessage = task + "\nGeneral game information:\n" + gameContext
                + "\nPlayer ID to analyse: " + playerId
                + "\nPlayer statistics: " + statistics + "\nTeam win rates: " + winRates
                + "\nUser question: " + question;
        Prompt request = new Prompt(List.of(new SystemMessage(systemInstructions), new UserMessage(userMessage)));
        String analysis = chatModel.call(request).getResult().getOutput().getText();
        if (analysis == null || analysis.isBlank()) {
            throw new IllegalStateException("Ollama returned an empty analysis");
        }
        analysisRepository.save(playerId, teamId, question, analysis);
        return analysis;
    }
}
