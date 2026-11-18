package com.gamehub.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "tournament_teams")
public class TournamentTeam {

    @EmbeddedId
    private TournamentTeamId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("tournamentId")
    @JoinColumn(name = "tournament_id", nullable = false)
    private Tournament tournament;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("teamId")
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @Column(name = "registered_at", nullable = false)
    private LocalDateTime registeredAt;

    public TournamentTeam(Tournament tournament, Team team,
                          LocalDateTime registeredAt) {
        this.tournament = tournament;
        this.team = team;
        this.id = new TournamentTeamId(
                tournament.getId(),
                team.getId()
        );
        this.registeredAt = registeredAt;
    }
}
