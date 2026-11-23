package com.gamehub.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.gamehub.dto.MatchRequest;
import com.gamehub.dto.MatchResponse;
import com.gamehub.dto.TournamentRequest;
import com.gamehub.dto.TournamentStandingResponse;
import com.gamehub.dto.TournamentTeamResponse;
import com.gamehub.entity.Match;
import com.gamehub.entity.Team;
import com.gamehub.entity.Tournament;
import com.gamehub.entity.TournamentTeam;
import com.gamehub.enums.MatchStatus;
import com.gamehub.repository.MatchRepository;
import com.gamehub.repository.TeamRepository;
import com.gamehub.repository.TournamentRepository;
import com.gamehub.repository.TournamentTeamRepository;

@Service
public class TournamentService {

    private final TournamentRepository tournamentRepository;
    private final TournamentTeamRepository tournamentTeamRepository;
    private final TeamRepository teamRepository;
    private final MatchRepository matchRepository;
    private final JdbcTemplate jdbcTemplate;

  public TournamentService(
    TournamentRepository tournamentRepository,
    TournamentTeamRepository tournamentTeamRepository,
    TeamRepository teamRepository,
    MatchRepository matchRepository,
    JdbcTemplate jdbcTemplate) {

    this.tournamentRepository = tournamentRepository;
    this.tournamentTeamRepository = tournamentTeamRepository;
    this.teamRepository = teamRepository;
    this.matchRepository = matchRepository;
    this.jdbcTemplate = jdbcTemplate;
}

    public Tournament createTournament(TournamentRequest request) {

        if (request.name() == null || request.name().isBlank()) {
            throw new IllegalArgumentException("Tournament name cannot be empty");
        }

        if (request.startDate() != null && request.endDate() != null
                && request.endDate().isBefore(request.startDate())) {
            throw new IllegalArgumentException("End date cannot be before start date");
        }

        Tournament tournament = new Tournament(
            request.name(),
            request.startDate(),
            request.endDate()
        );

        return tournamentRepository.save(tournament);
    }

   public TournamentTeamResponse registerTeam(Long tournamentId, Long teamId) {

    var tournament = tournamentRepository.findById(tournamentId)
            .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Tournament not found"));

    Team team = teamRepository.findById(teamId)
            .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Team not found"));

    if (tournamentTeamRepository
            .existsByTournament_IdAndTeam_Id(tournamentId, teamId)) {
        throw new ResponseStatusException(
                HttpStatus.CONFLICT, "Team is already registered");
    }

    TournamentTeam registration = new TournamentTeam(
            tournament,
            team,
            LocalDateTime.now()
    );

    TournamentTeam savedRegistration =
            tournamentTeamRepository.save(registration);

    return new TournamentTeamResponse(
            tournament.getId(),
            tournament.getName(),
            team.getId(),
            team.getName(),
            savedRegistration.getRegisteredAt()
    );
}

public MatchResponse scheduleMatch(Long tournamentId, MatchRequest request) {

    // Find tournament
    Tournament tournament = tournamentRepository.findById(tournamentId)
            .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Tournament not found"));

    // Prevent a team from playing against itself
    if (request.teamAId().equals(request.teamBId())) {
        throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "A team cannot play against itself");
    }

    // Find both teams
    Team teamA = teamRepository.findById(request.teamAId())
            .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Team A not found"));

    Team teamB = teamRepository.findById(request.teamBId())
            .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Team B not found"));

    // Check that both teams are registered in the tournament
    if (!tournamentTeamRepository.existsByTournament_IdAndTeam_Id(
            tournamentId, teamA.getId())) {
        throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "Team A is not registered in this tournament");
    }

    if (!tournamentTeamRepository.existsByTournament_IdAndTeam_Id(
            tournamentId, teamB.getId())) {
        throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "Team B is not registered in this tournament");
    }

    

    // Create match
    Match match = new Match(
            tournament,
            teamA,
            teamB,
            request.format(),
            MatchStatus.SCHEDULED,
            request.scheduledAt()
    );

    Match savedMatch = matchRepository.save(match);

    return new MatchResponse(
            savedMatch.getId(),
            tournament.getId(),
            teamA.getId(),
            teamA.getName(),
            teamB.getId(),
            teamB.getName(),
            savedMatch.getFormat(),
            savedMatch.getStatus(),
            savedMatch.getScheduledAt()
    );
}

public List<TournamentStandingResponse> getTournamentStandings(Long tournamentId) {

    return jdbcTemplate.query(
        "CALL sp_get_tournament_standings(?)",
        (rs, rowNum) -> new TournamentStandingResponse(
            rs.getLong("team_id"),
            rs.getString("team_name"),
            rs.getLong("matches_played"),
            rs.getLong("wins"),
            rs.getLong("losses"),
            rs.getBigDecimal("match_win_rate")
        ),
        tournamentId
    );
}

}