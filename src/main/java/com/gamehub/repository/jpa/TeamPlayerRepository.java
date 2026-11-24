package com.gamehub.repository.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gamehub.entity.jpa.TeamPlayer;
import com.gamehub.entity.jpa.TeamPlayerId;

public interface TeamPlayerRepository
        extends JpaRepository<TeamPlayer, TeamPlayerId> {

    List<TeamPlayer> findByPlayer_Id(Long playerId);

    List<TeamPlayer> findByTeam_Id(Long teamId);
}
