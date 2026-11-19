package com.gamehub.mapper.graph;

import java.util.List;
import java.util.Map;

/** Each endpoint uses fields from the same SQL row to identify its node. */
public record GraphRelationshipMapping(String table, String from, String type, String to, boolean storesRow) {

    private static final String STATS = "PlayerGameStats {game_id: row.game_id, player_id: row.player_id}";
    public static final List<GraphRelationshipMapping> ALL = List.of(
        new GraphRelationshipMapping("team_players", "Player {id: row.player_id}", "MEMBER_OF", "Team {id: row.team_id}", true),
        new GraphRelationshipMapping("tournament_teams", "Team {id: row.team_id}", "REGISTERED_FOR", "Tournament {id: row.tournament_id}", true),
        new GraphRelationshipMapping("champions_has_champion_roles", "Champion {id: row.champions_id}", "HAS_ROLE", "ChampionRole {id: row.champion_roles_id}", true),
        new GraphRelationshipMapping("matches", "Tournament {id: row.tournament_id}", "HAS_MATCH", "Match {id: row.id}", false),
        new GraphRelationshipMapping("matches", "Match {id: row.id}", "TEAM_A", "Team {id: row.team_a_id}", false),
        new GraphRelationshipMapping("matches", "Match {id: row.id}", "TEAM_B", "Team {id: row.team_b_id}", false),
        new GraphRelationshipMapping("matches", "Match {id: row.id}", "WON_BY", "Team {id: row.winner_team_id}", false),
        new GraphRelationshipMapping("games", "Match {id: row.match_id}", "HAS_GAME", "Game {id: row.id}", false),
        new GraphRelationshipMapping("games", "Game {id: row.id}", "WON_BY", "Team {id: row.winner_team_id}", false),
        new GraphRelationshipMapping("player_game_stats", "Player {id: row.player_id}", "HAS_GAME_STATS", STATS, false),
        new GraphRelationshipMapping("player_game_stats", STATS, "FOR_GAME", "Game {id: row.game_id}", false),
        new GraphRelationshipMapping("player_game_stats", STATS, "REPRESENTED", "Team {id: row.team_id}", false),
        new GraphRelationshipMapping("player_game_stats", STATS, "PLAYED_CHAMPION", "Champion {id: row.champion_id}", false),
        new GraphRelationshipMapping("match_status_history", "Match {id: row.match_id}", "HAS_STATUS_CHANGE", "MatchStatusChange {id: row.id}", false),
        new GraphRelationshipMapping("player_statistics", "Player {id: row.player_id}", "HAS_STATISTICS", "PlayerStatistics {player_id: row.player_id}", false)
    );

    public List<Map<String, Object>> rowsFor(List<Map<String, Object>> rows) {
        if (type.equals("WON_BY")) return rows.stream().filter(row -> row.containsKey("winner_team_id")).toList();
        return rows;
    }

    public String identityPattern() {
        // A player can join the same team in several separate periods.
        return type.equals("MEMBER_OF") ? " {joined_at: row.joined_at}" : "";
    }
}
