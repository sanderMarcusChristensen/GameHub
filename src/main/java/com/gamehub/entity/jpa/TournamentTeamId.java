package com.gamehub.entity.jpa;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Embeddable
public class TournamentTeamId implements Serializable {

    @Column(name = "tournament_id")
    private Long tournamentId;

    @Column(name = "team_id")
    private Long teamId;
}
