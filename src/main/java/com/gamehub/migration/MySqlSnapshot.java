package com.gamehub.migration;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Rows for both target mappers. Column names and decimal values belong to the SQL source. */
public record MySqlSnapshot(Map<String, List<Map<String, Object>>> tables) {
    private record Source(String table, String columns) {}

    // Explicit column lists keep password hashes out of the migration snapshot.
    private static final List<Source> SOURCES = List.of(
        new Source("app_users", "id,email,role,is_enabled,created_at"),
        new Source("players", "id,nickname,created_at"),
        new Source("teams", "id,name,short_name,created_at"),
        new Source("champions", "id,name,released_at"),
        new Source("champion_roles", "id,name"),
        new Source("tournaments", "id,name,start_date,end_date,created_at"),
        new Source("matches", "id,tournament_id,team_a_id,team_b_id,format,status,winner_team_id,scheduled_at,created_at"),
        new Source("games", "id,match_id,game_number,winner_team_id,started_at,ended_at,created_at"),
        new Source("player_game_stats", "game_id,player_id,team_id,champion_id,kills,deaths,assists"),
        new Source("match_status_history", "id,match_id,old_status,new_status,changed_at"),
        new Source("player_statistics", "player_id,games_played,wins,losses,win_rate,updated_at"),
        new Source("team_players", "team_id,player_id,joined_at,left_at"),
        new Source("champions_has_champion_roles", "champions_id,champion_roles_id"),
        new Source("tournament_teams", "tournament_id,team_id,registered_at")
    );

    /** The caller supplies a dedicated connection. Finish reading before either target starts writing. */
    public static MySqlSnapshot read(Connection connection) throws SQLException {
        connection.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
        connection.setReadOnly(true);
        connection.setAutoCommit(false);
        Map<String, List<Map<String, Object>>> tables = new LinkedHashMap<>();
        try (Statement statement = connection.createStatement()) {
            statement.execute("START TRANSACTION WITH CONSISTENT SNAPSHOT");
            for (Source source : SOURCES) {
                tables.put(source.table, readTable(statement, source));
            }
        } finally {
            connection.rollback();
        }
        return new MySqlSnapshot(Map.copyOf(tables));
    }

    private static List<Map<String, Object>> readTable(Statement statement, Source source) throws SQLException {
        List<Map<String, Object>> rows = new ArrayList<>();
        try (ResultSet result = statement.executeQuery("SELECT " + source.columns + " FROM " + source.table)) {
            var metadata = result.getMetaData();
            while (result.next()) {
                Map<String, Object> row = new LinkedHashMap<>();
                for (int i = 1; i <= metadata.getColumnCount(); i++) {
                    String column = metadata.getColumnLabel(i);
                    Object value = result.getObject(i);
                    if (value == null) continue;
                    // DATETIME represents local clock time. TIMESTAMP is read in the UTC session.
                    value = switch (metadata.getColumnTypeName(i)) {
                        case "TIMESTAMP" -> result.getTimestamp(i).toInstant().atOffset(ZoneOffset.UTC);
                        case "DATETIME" -> result.getObject(i, LocalDateTime.class);
                        case "DATE" -> result.getObject(i, LocalDate.class);
                        default -> value;
                    };
                    row.put(column, value);
                }
                rows.add(Map.copyOf(row));
            }
        } catch (SQLException e) {
            throw new SQLException("Could not read " + source.table + ": " + e.getMessage(), e);
        }
        return List.copyOf(rows);
    }
}
