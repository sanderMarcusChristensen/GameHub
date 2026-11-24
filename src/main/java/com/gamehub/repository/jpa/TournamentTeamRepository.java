package com.gamehub.repository.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gamehub.entity.jpa.TournamentTeam;
import com.gamehub.entity.jpa.TournamentTeamId;

public interface TournamentTeamRepository
        extends JpaRepository<TournamentTeam, TournamentTeamId> {

    List<TournamentTeam> findByTournament_Id(Long tournamentId);

    List<TournamentTeam> findByTeam_Id(Long teamId);

    boolean existsByTournament_IdAndTeam_Id(Long tournamentId, Long teamId);
}
