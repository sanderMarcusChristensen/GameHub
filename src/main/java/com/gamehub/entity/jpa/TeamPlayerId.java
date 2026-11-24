package com.gamehub.entity.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Embeddable
public class TeamPlayerId implements Serializable {

    @Column(name = "team_id")
    private Long teamId;

    @Column(name = "player_id")
    private Long playerId;

    @Column(name = "joined_at")
    private LocalDateTime joinedAt;
}
