package com.gamehub.entity.jpa;

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
@Table(name = "team_players")
public class TeamPlayer {

    @EmbeddedId
    private TeamPlayerId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("teamId")
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("playerId")
    @JoinColumn(name = "player_id", nullable = false)
    private Player player;

    @Column(name = "left_at")
    private LocalDateTime leftAt;

    public TeamPlayer(Team team, Player player,
                      LocalDateTime joinedAt, LocalDateTime leftAt) {
        this.team = team;
        this.player = player;
        this.id = new TeamPlayerId(
                team.getId(),
                player.getId(),
                joinedAt
        );
        this.leftAt = leftAt;
    }
}
