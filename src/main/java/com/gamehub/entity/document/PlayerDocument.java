
package com.gamehub.entity.document;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "players")
public class PlayerDocument {

    @Id
    private Long id;

    private String nickname;
    private LocalDateTime createdAt;

    private List<TeamMembership> teamHistory;
    private PlayerStatistics statistics;

    public PlayerDocument() {
    }

    public static class PlayerStatistics {

        private Integer gamesPlayed;
        private Integer wins;
        private Integer losses;
        private BigDecimal winRate;
        private LocalDateTime updatedAt;

        public PlayerStatistics() {
        }

        public Integer getGamesPlayed() {
            return gamesPlayed;
        }

        public void setGamesPlayed(Integer gamesPlayed) {
            this.gamesPlayed = gamesPlayed;
        }

        public Integer getWins() {
            return wins;
        }

        public void setWins(Integer wins) {
            this.wins = wins;
        }

        public Integer getLosses() {
            return losses;
        }

        public void setLosses(Integer losses) {
            this.losses = losses;
        }

        public BigDecimal getWinRate() {
            return winRate;
        }

        public void setWinRate(BigDecimal winRate) {
            this.winRate = winRate;
        }

        public LocalDateTime getUpdatedAt() {
            return updatedAt;
        }

        public void setUpdatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
        }
    }

    public static class TeamMembership {

        private Long teamId;
        private LocalDateTime joinedAt;
        private LocalDateTime leftAt;

        public TeamMembership() {
        }

        public TeamMembership(Long teamId, LocalDateTime joinedAt, LocalDateTime leftAt) {
            this.teamId = teamId;
            this.joinedAt = joinedAt;
            this.leftAt = leftAt;
        }

        public Long getTeamId() {
            return teamId;
        }

        public void setTeamId(Long teamId) {
            this.teamId = teamId;
        }

        public LocalDateTime getJoinedAt() {
            return joinedAt;
        }

        public void setJoinedAt(LocalDateTime joinedAt) {
            this.joinedAt = joinedAt;
        }

        public LocalDateTime getLeftAt() {
            return leftAt;
        }

        public void setLeftAt(LocalDateTime leftAt) {
            this.leftAt = leftAt;
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<TeamMembership> getTeamHistory() {
        return teamHistory;
    }

    public void setTeamHistory(List<TeamMembership> teamHistory) {
        this.teamHistory = teamHistory;
    }

    public PlayerStatistics getStatistics() {
        return statistics;
    }

    public void setStatistics(PlayerStatistics statistics) {
        this.statistics = statistics;
    }
}
