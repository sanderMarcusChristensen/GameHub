package com.gamehub.document;

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

    public PlayerDocument() {
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
}
