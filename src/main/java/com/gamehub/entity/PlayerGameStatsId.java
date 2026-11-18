
package com.gamehub.entity;

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
public class PlayerGameStatsId implements Serializable {

    @Column(name = "game_id")
    private Long gameId;

    @Column(name = "player_id")
    private Long playerId;
}
