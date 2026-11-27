package com.gamehub.repository.graph;

import com.gamehub.dto.PlayerTeamHistory;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Value;

import java.util.Map;
import java.util.Optional;

public class PlayerTeamHistoryReaderImpl implements PlayerTeamHistoryReader {
    private final Driver driver;

    public PlayerTeamHistoryReaderImpl(Driver driver) {
        this.driver = driver;
    }

    @Override
    public Optional<PlayerTeamHistory> findTeamHistory(long playerId) {
        // OPTIONAL MATCH also returns players who have never joined a team.
        String query = """
                MATCH (p:Player {id: $playerId})
                OPTIONAL MATCH (p)-[m:MEMBER_OF]->(t:Team)
                WITH p, m, t ORDER BY m.joined_at, t.id
                RETURN p.nickname AS nickname,
                       collect(t { .id, .name, joinedAt: m.joined_at, leftAt: m.left_at }) AS memberships
                """;
        try (var session = driver.session()) {
            return session.executeRead(tx -> tx.run(query, Map.of("playerId", playerId))
                    .list(row -> new PlayerTeamHistory(playerId, row.get("nickname").asString(),
                            row.get("memberships").asList(PlayerTeamHistoryReaderImpl::membership)))
                    .stream().findFirst());
        }
    }

    private static PlayerTeamHistory.Membership membership(Value value) {
        var leftAt = value.get("leftAt");
        return new PlayerTeamHistory.Membership(value.get("id").asLong(), value.get("name").asString(),
                value.get("joinedAt").asLocalDateTime(), leftAt.isNull() ? null : leftAt.asLocalDateTime());
    }
}
