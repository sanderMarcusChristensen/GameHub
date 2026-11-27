package com.gamehub.repository.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gamehub.entity.jpa.PlayerGameStats;
import com.gamehub.entity.jpa.PlayerGameStatsId;

public interface PlayerGameStatsRepository
        extends JpaRepository<PlayerGameStats, PlayerGameStatsId> {

    List<PlayerGameStats> findByGame_Id(Long gameId);

    List<PlayerGameStats> findByPlayer_Id(Long playerId);

    List<PlayerGameStats> findByChampion_Id(Long championId);

    List<PlayerGameStats> findByTeam_Id(Long teamId);
}
