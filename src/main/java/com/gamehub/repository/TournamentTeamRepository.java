package com.gamehub.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gamehub.entity.TournamentTeam;
import com.gamehub.entity.TournamentTeamId;

public interface TournamentTeamRepository
        extends JpaRepository<TournamentTeam, TournamentTeamId> {

    List<TournamentTeam> findByTournament_Id(Long tournamentId);

    List<TournamentTeam> findByTeam_Id(Long teamId);
}
