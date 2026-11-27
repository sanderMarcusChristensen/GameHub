package com.gamehub.ai_integration;

import java.math.BigDecimal;
import java.util.List;

import com.gamehub.exception.ResourceNotFoundException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class TeamStatisticsService {

    private final JdbcTemplate jdbcTemplate;

    public TeamStatisticsService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<PlayerStatistics> getStatistics(Long playerId, Long teamId) {
        Integer membershipCount = jdbcTemplate.queryForObject("""
                SELECT COUNT(*) FROM team_players
                WHERE player_id = ? AND team_id = ? AND left_at IS NULL
                """, Integer.class, playerId, teamId);
        if (membershipCount == null || membershipCount == 0) {
            throw new ResourceNotFoundException("Player is not an active member of this team");
        }

        return jdbcTemplate.query("""
                SELECT p.id, p.nickname, COUNT(s.game_id) AS games_played,
                       AVG(s.kills) AS avg_kills, AVG(s.deaths) AS avg_deaths,
                       AVG(s.assists) AS avg_assists
                FROM players p
                JOIN (SELECT DISTINCT player_id FROM team_players
                      WHERE team_id = ? AND left_at IS NULL) members ON members.player_id = p.id
                LEFT JOIN player_game_stats s ON s.player_id = p.id AND s.team_id = ?
                GROUP BY p.id, p.nickname
                ORDER BY p.id
                """, (rs, rowNum) -> new PlayerStatistics(
                rs.getLong("id"), rs.getString("nickname"), rs.getLong("games_played"),
                rs.getObject("avg_kills", Double.class), rs.getObject("avg_deaths", Double.class),
                rs.getObject("avg_assists", Double.class)), teamId, teamId);
    }

    public TeamWinRates getTeamWinRates(Long teamId) {
        return jdbcTemplate.queryForObject("""
                SELECT fn_team_match_win_rate(?) AS match_win_rate,
                       fn_team_game_win_rate(?) AS game_win_rate
                """, (rs, rowNum) -> new TeamWinRates(
                rs.getBigDecimal("match_win_rate"), rs.getBigDecimal("game_win_rate")), teamId, teamId);
    }

    public record TeamWinRates(BigDecimal matchWinRatePercent, BigDecimal gameWinRatePercent) {
    }

    public record PlayerStatistics(Long playerId, String nickname, long gamesPlayed,
                                   Double averageKills, Double averageDeaths, Double averageAssists) {
    }
}
