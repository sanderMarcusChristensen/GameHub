
package com.gamehub.migrationlogic.documentmappers;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.gamehub.entity.Player;
import com.gamehub.entity.PlayerStatistics;
import com.gamehub.entity.TeamPlayer;
import com.gamehub.entity.document.PlayerDocument;

@Component
public class PlayerDocumentMapper {

    public List<PlayerDocument> mapAll(
            List<Player> players,
            List<TeamPlayer> teamPlayers,
            List<PlayerStatistics> playerStatistics) {

        // Group team memberships by player ID.
        Map<Long, List<TeamPlayer>> membershipsByPlayer =
                teamPlayers.stream()
                        .collect(Collectors.groupingBy(
                                tp -> tp.getId().getPlayerId()
                        ));

        // Index player statistics by player ID.
        Map<Long, PlayerStatistics> statisticsByPlayer =
                playerStatistics.stream()
                        .collect(Collectors.toMap(
                                PlayerStatistics::getPlayerId,
                                stats -> stats
                        ));

        // Convert each SQL player into a MongoDB document.
        return players.stream()
                .map(player -> {

                    PlayerDocument document = new PlayerDocument();

                    document.setId(player.getId());
                    document.setNickname(player.getNickname());
                    document.setCreatedAt(player.getCreatedAt());

                    // Map team membership history.
                    List<PlayerDocument.TeamMembership> teamHistory =
                            membershipsByPlayer
                                    .getOrDefault(player.getId(), List.of())
                                    .stream()
                                    .map(tp -> new PlayerDocument.TeamMembership(
                                            tp.getId().getTeamId(),
                                            tp.getId().getJoinedAt(),
                                            tp.getLeftAt()
                                    ))
                                    .toList();

                    document.setTeamHistory(teamHistory);

                    // Map aggregated player statistics.
                    PlayerStatistics stats =
                            statisticsByPlayer.get(player.getId());

                    if (stats != null) {

                        PlayerDocument.PlayerStatistics statisticsDocument =
                                new PlayerDocument.PlayerStatistics();

                        statisticsDocument.setGamesPlayed(stats.getGamesPlayed());
                        statisticsDocument.setWins(stats.getWins());
                        statisticsDocument.setLosses(stats.getLosses());
                        statisticsDocument.setWinRate(stats.getWinRate());
                        statisticsDocument.setUpdatedAt(stats.getUpdatedAt());

                        document.setStatistics(statisticsDocument);
                    }

                    return document;

                })
                .toList();
    }
}
