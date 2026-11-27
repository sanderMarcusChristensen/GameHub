package com.gamehub.ai_integration;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class PlayerAnalysisRepository {
    private final JdbcTemplate jdbcTemplate;

    public PlayerAnalysisRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void save(Long playerId, Long teamId, String question, String analysis) {
        jdbcTemplate.update("""
                INSERT INTO player_ai_analysis (player_id, team_id, question, analysis)
                VALUES (?, ?, ?, ?)
                ON DUPLICATE KEY UPDATE question = VALUES(question), analysis = VALUES(analysis),
                                        generated_at = CURRENT_TIMESTAMP
                """, playerId, teamId, question, analysis);
    }

    public Optional<SavedAnalysis> find(Long playerId, Long teamId) {
        List<SavedAnalysis> results = jdbcTemplate.query("""
                SELECT player_id, team_id, question, analysis, generated_at
                FROM player_ai_analysis WHERE player_id = ? AND team_id = ?
                """, (rs, rowNum) -> new SavedAnalysis(rs.getLong("player_id"), rs.getLong("team_id"),
                rs.getString("question"), rs.getString("analysis"),
                rs.getObject("generated_at", LocalDateTime.class)), playerId, teamId);
        return results.stream().findFirst();
    }

    public record SavedAnalysis(Long playerId, Long teamId, String question, String analysis,
                                LocalDateTime generatedAt) {
    }
}
