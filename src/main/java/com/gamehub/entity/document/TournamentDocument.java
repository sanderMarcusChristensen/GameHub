package com.gamehub.entity.document;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "tournaments")
public class TournamentDocument {

    @Id
    private Long id;

    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDateTime createdAt;

    private List<RegisteredTeam> registeredTeams;

    public TournamentDocument() {
    }

    public TournamentDocument(Long id, String name, LocalDate startDate,
                               LocalDate endDate, LocalDateTime createdAt,
                               List<RegisteredTeam> registeredTeams) {
        this.id = id;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.createdAt = createdAt;
        this.registeredTeams = registeredTeams;
    }

    public static class RegisteredTeam {

        private Long teamId;
        private LocalDateTime registeredAt;

        public RegisteredTeam() {
        }

        public RegisteredTeam(Long teamId, LocalDateTime registeredAt) {
            this.teamId = teamId;
            this.registeredAt = registeredAt;
        }

        public Long getTeamId() {
            return teamId;
        }

        public void setTeamId(Long teamId) {
            this.teamId = teamId;
        }

        public LocalDateTime getRegisteredAt() {
            return registeredAt;
        }

        public void setRegisteredAt(LocalDateTime registeredAt) {
            this.registeredAt = registeredAt;
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<RegisteredTeam> getRegisteredTeams() {
        return registeredTeams;
    }

    public void setRegisteredTeams(List<RegisteredTeam> registeredTeams) {
        this.registeredTeams = registeredTeams;
    }
}
