package com.gamehub.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gamehub.entity.TeamPlayer;
import com.gamehub.entity.TeamPlayerId;

public interface TeamPlayerRepository
        extends JpaRepository<TeamPlayer, TeamPlayerId> {

    List<TeamPlayer> findByPlayer_Id(Long playerId);

    List<TeamPlayer> findByTeam_Id(Long teamId);
}
