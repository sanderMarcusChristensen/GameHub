package com.gamehub.ai_integration;

import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

import com.gamehub.exception.ResourceConflictException;
import com.gamehub.exception.ResourceNotFoundException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(name = "gamehub.ai.enabled", havingValue = "true")
public class PlayerChatService {
    private final JdbcTemplate jdbcTemplate;
    private final PlayerAnalysisService analysisService;

    public PlayerChatService(JdbcTemplate jdbcTemplate, PlayerAnalysisService analysisService) {
        this.jdbcTemplate = jdbcTemplate;
        this.analysisService = analysisService;
    }

    public String answer(String question) throws IOException {
        List<Player> candidates = jdbcTemplate.query("""
                SELECT id, nickname FROM players
                WHERE LOCATE(LOWER(nickname), LOWER(?)) > 0
                """, (rs, rowNum) -> new Player(rs.getLong("id"), rs.getString("nickname")), question);
        List<Player> matches = candidates.stream()
                .filter(player -> Pattern.compile("(?<![\\p{L}\\p{N}_])" + Pattern.quote(player.nickname())
                                + "(?![\\p{L}\\p{N}_])", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE)
                        .matcher(question).find())
                .toList();
        if (matches.isEmpty()) {
            throw new ResourceNotFoundException("No player found. Include a player's full nickname from the GameHub database in your question.");
        }
        if (matches.size() > 1) {
            throw new ResourceConflictException("More than one player was found. Please ask about one player at a time.");
        }

        Player player = matches.get(0);
        List<Long> teams = jdbcTemplate.queryForList("""
                SELECT DISTINCT team_id FROM team_players
                WHERE player_id = ? AND left_at IS NULL
                """, Long.class, player.id());
        if (teams.isEmpty()) {
            throw new ResourceNotFoundException("No active team found for " + player.nickname() + ".");
        }
        if (teams.size() > 1) {
            throw new ResourceConflictException("More than one active team was found for " + player.nickname() + ".");
        }
        return analysisService.generate(player.id(), teams.get(0), question);
    }

    private record Player(Long id, String nickname) {
    }
}
