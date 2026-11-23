
package com.gamehub.migrationlogic.documentdb.documentmappers;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.gamehub.entity.Game;
import com.gamehub.entity.Match;
import com.gamehub.entity.MatchStatusHistory;
import com.gamehub.entity.PlayerGameStats;
import com.gamehub.entity.document.MatchDocument;

@Component
public class MatchDocumentMapper {

    public List<MatchDocument> mapAll(
            List<Match> matches,
            List<Game> games,
            List<PlayerGameStats> playerGameStats,
            List<MatchStatusHistory> statusHistory) {

        Map<Long, List<Game>> gamesByMatch =
                games.stream().collect(Collectors.groupingBy(
                        game -> game.getMatch().getId()
                ));

        Map<Long, List<PlayerGameStats>> statsByGame =
                playerGameStats.stream().collect(Collectors.groupingBy(
                        stats -> stats.getGame().getId()
                ));

        Map<Long, List<MatchStatusHistory>> historyByMatch =
                statusHistory.stream().collect(Collectors.groupingBy(
                        history -> history.getMatch().getId()
                ));

        return matches.stream()
                .map(match -> {
                    MatchDocument document = new MatchDocument();

                    document.setId(match.getId());
                    document.setTournamentId(match.getTournament().getId());
                    document.setTeamAId(match.getTeamA().getId());
                    document.setTeamBId(match.getTeamB().getId());
                    document.setFormat(match.getFormat().name());
                    document.setStatus(match.getStatus().name());
                    document.setWinnerTeamId(
                            match.getWinnerTeam() == null
                                    ? null : match.getWinnerTeam().getId()
                    );
                    document.setScheduledAt(match.getScheduledAt());
                    document.setCreatedAt(match.getCreatedAt());

                    List<MatchDocument.Game> gameDocuments =
                            gamesByMatch.getOrDefault(match.getId(), List.of())
                                    .stream()
                                    .map(game -> {
                                        MatchDocument.Game gameDocument =
                                                new MatchDocument.Game();

                                        gameDocument.setGameId(game.getId());
                                        gameDocument.setGameNumber(game.getGameNumber());
                                        gameDocument.setWinnerTeamId(
                                                game.getWinnerTeam() == null
                                                        ? null : game.getWinnerTeam().getId()
                                        );
                                        gameDocument.setStartedAt(game.getStartedAt());
                                        gameDocument.setEndedAt(game.getEndedAt());
                                        gameDocument.setCreatedAt(game.getCreatedAt());

                                        List<MatchDocument.PlayerGameStat> statDocuments =
                                                statsByGame.getOrDefault(
                                                        game.getId(), List.of())
                                                        .stream()
                                                        .map(stat -> {
                                                            MatchDocument.PlayerGameStat statDocument =
                                                                    new MatchDocument.PlayerGameStat();

                                                            statDocument.setPlayerId(
                                                                    stat.getPlayer().getId());
                                                            statDocument.setTeamId(
                                                                    stat.getTeam().getId());
                                                            statDocument.setChampionId(
                                                                    stat.getChampion().getId());
                                                            statDocument.setKills(stat.getKills());
                                                            statDocument.setDeaths(stat.getDeaths());
                                                            statDocument.setAssists(stat.getAssists());

                                                            return statDocument;
                                                        })
                                                        .toList();

                                        gameDocument.setPlayerStats(statDocuments);
                                        return gameDocument;
                                    })
                                    .toList();

                    document.setGames(gameDocuments);

                    List<MatchDocument.MatchStatusHistory> historyDocuments =
                            historyByMatch.getOrDefault(match.getId(), List.of())
                                    .stream()
                                    .map(history -> {
                                        MatchDocument.MatchStatusHistory historyDocument =
                                                new MatchDocument.MatchStatusHistory();

                                        historyDocument.setHistoryId(history.getId());
                                        historyDocument.setOldStatus(
                                                history.getOldStatus().name());
                                        historyDocument.setNewStatus(
                                                history.getNewStatus().name());
                                        historyDocument.setChangedAt(history.getChangedAt());

                                        return historyDocument;
                                    })
                                    .toList();

                    document.setStatusHistory(historyDocuments);

                    return document;
                })
                .toList();
    }
}
