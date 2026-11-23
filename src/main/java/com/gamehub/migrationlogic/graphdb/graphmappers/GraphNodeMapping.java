package com.gamehub.migrationlogic.graphdb.graphmappers;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/** One SQL row becomes one node identified by the listed source keys. */
public record GraphNodeMapping(String table, String label, String keys) {
    public String keyPattern() {
        return Arrays.stream(keys.split(",")).map(key -> key + ": row." + key)
            .collect(Collectors.joining(", "));
    }

    public static final List<GraphNodeMapping> ALL = List.of(
        new GraphNodeMapping("app_users", "AppUser", "id"),
        new GraphNodeMapping("players", "Player", "id"),
        new GraphNodeMapping("teams", "Team", "id"),
        new GraphNodeMapping("champions", "Champion", "id"),
        new GraphNodeMapping("champion_roles", "ChampionRole", "id"),
        new GraphNodeMapping("tournaments", "Tournament", "id"),
        new GraphNodeMapping("matches", "Match", "id"),
        new GraphNodeMapping("games", "Game", "id"),
        new GraphNodeMapping("player_game_stats", "PlayerGameStats", "game_id,player_id"),
        new GraphNodeMapping("match_status_history", "MatchStatusChange", "id"),
        new GraphNodeMapping("player_statistics", "PlayerStatistics", "player_id")
    );
}
