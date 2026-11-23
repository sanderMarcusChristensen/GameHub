
package com.gamehub.entity.document;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "matches")
public class MatchDocument {

    @Id
    private Long id;

    private Long tournamentId;
    private Long teamAId;
    private Long teamBId;

    private String format;
    private String status;
    private Long winnerTeamId;

    private LocalDateTime scheduledAt;
    private LocalDateTime createdAt;

    private List<MatchStatusHistory> statusHistory;
    private List<Game> games;

    public MatchDocument() {
    }

    public static class MatchStatusHistory {

        private Long historyId;
        private String oldStatus;
        private String newStatus;
        private LocalDateTime changedAt;

        public MatchStatusHistory() {
        }

        public Long getHistoryId() {
            return historyId;
        }

        public void setHistoryId(Long historyId) {
            this.historyId = historyId;
        }

        public String getOldStatus() {
            return oldStatus;
        }

        public void setOldStatus(String oldStatus) {
            this.oldStatus = oldStatus;
        }

        public String getNewStatus() {
            return newStatus;
        }

        public void setNewStatus(String newStatus) {
            this.newStatus = newStatus;
        }

        public LocalDateTime getChangedAt() {
            return changedAt;
        }

        public void setChangedAt(LocalDateTime changedAt) {
            this.changedAt = changedAt;
        }
    }

    public static class Game {

        private Long gameId;
        private Integer gameNumber;
        private Long winnerTeamId;
        private LocalDateTime startedAt;
        private LocalDateTime endedAt;
        private LocalDateTime createdAt;

        private List<PlayerGameStat> playerStats;

        public Game() {
        }

        public Long getGameId() {
            return gameId;
        }

        public void setGameId(Long gameId) {
            this.gameId = gameId;
        }

        public Integer getGameNumber() {
            return gameNumber;
        }

        public void setGameNumber(Integer gameNumber) {
            this.gameNumber = gameNumber;
        }

        public Long getWinnerTeamId() {
            return winnerTeamId;
        }

        public void setWinnerTeamId(Long winnerTeamId) {
            this.winnerTeamId = winnerTeamId;
        }

        public LocalDateTime getStartedAt() {
            return startedAt;
        }

        public void setStartedAt(LocalDateTime startedAt) {
            this.startedAt = startedAt;
        }

        public LocalDateTime getEndedAt() {
            return endedAt;
        }

        public void setEndedAt(LocalDateTime endedAt) {
            this.endedAt = endedAt;
        }

        public LocalDateTime getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
        }

        public List<PlayerGameStat> getPlayerStats() {
            return playerStats;
        }

        public void setPlayerStats(List<PlayerGameStat> playerStats) {
            this.playerStats = playerStats;
        }
    }

    public static class PlayerGameStat {

        private Long playerId;
        private Long teamId;
        private Long championId;

        private Integer kills;
        private Integer deaths;
        private Integer assists;

        public PlayerGameStat() {
        }

        public Long getPlayerId() {
            return playerId;
        }

        public void setPlayerId(Long playerId) {
            this.playerId = playerId;
        }

        public Long getTeamId() {
            return teamId;
        }

        public void setTeamId(Long teamId) {
            this.teamId = teamId;
        }

        public Long getChampionId() {
            return championId;
        }

        public void setChampionId(Long championId) {
            this.championId = championId;
        }

        public Integer getKills() {
            return kills;
        }

        public void setKills(Integer kills) {
            this.kills = kills;
        }

        public Integer getDeaths() {
            return deaths;
        }

        public void setDeaths(Integer deaths) {
            this.deaths = deaths;
        }

        public Integer getAssists() {
            return assists;
        }

        public void setAssists(Integer assists) {
            this.assists = assists;
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTournamentId() {
        return tournamentId;
    }

    public void setTournamentId(Long tournamentId) {
        this.tournamentId = tournamentId;
    }

    public Long getTeamAId() {
        return teamAId;
    }

    public void setTeamAId(Long teamAId) {
        this.teamAId = teamAId;
    }

    public Long getTeamBId() {
        return teamBId;
    }

    public void setTeamBId(Long teamBId) {
        this.teamBId = teamBId;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getWinnerTeamId() {
        return winnerTeamId;
    }

    public void setWinnerTeamId(Long winnerTeamId) {
        this.winnerTeamId = winnerTeamId;
    }

    public LocalDateTime getScheduledAt() {
        return scheduledAt;
    }

    public void setScheduledAt(LocalDateTime scheduledAt) {
        this.scheduledAt = scheduledAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<MatchStatusHistory> getStatusHistory() {
        return statusHistory;
    }

    public void setStatusHistory(List<MatchStatusHistory> statusHistory) {
        this.statusHistory = statusHistory;
    }

    public List<Game> getGames() {
        return games;
    }

    public void setGames(List<Game> games) {
        this.games = games;
    }
}
