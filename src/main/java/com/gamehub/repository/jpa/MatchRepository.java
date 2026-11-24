package com.gamehub.repository.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gamehub.entity.jpa.Match;
import com.gamehub.enums.MatchStatus;

public interface MatchRepository extends JpaRepository<Match, Long> {

    List<Match> findByTournament_Id(Long tournamentId);

    List<Match> findByTeamA_IdOrTeamB_Id(Long teamAId, Long teamBId);

    List<Match> findByStatus(MatchStatus status);
}
