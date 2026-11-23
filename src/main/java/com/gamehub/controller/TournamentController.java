package com.gamehub.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gamehub.dto.MatchRequest;
import com.gamehub.dto.MatchResponse;
import com.gamehub.dto.TournamentRequest;
import com.gamehub.dto.TournamentStandingResponse;
import com.gamehub.dto.TournamentTeamResponse;
import com.gamehub.entity.Tournament;
import com.gamehub.service.TournamentService;



@RestController
@RequestMapping("/api/tournaments")
public class TournamentController {
    private final TournamentService tournamentService;

public TournamentController(TournamentService tournamentService) {
this.tournamentService = tournamentService;
}


@PostMapping
public ResponseEntity<Tournament> createTournament(
        @RequestBody TournamentRequest request) {

    Tournament tournament = tournamentService.createTournament(request);

    return ResponseEntity.status(HttpStatus.CREATED).body(tournament);
}

@PostMapping("/{id}/teams/{teamId}")
public ResponseEntity<TournamentTeamResponse> registerTeam(
        @PathVariable Long id,
        @PathVariable Long teamId) {

    TournamentTeamResponse registration =
            tournamentService.registerTeam(id, teamId);

    return ResponseEntity.status(HttpStatus.CREATED).body(registration);
}

@PostMapping("/{id}/matches")
public ResponseEntity<MatchResponse> scheduleMatch(
        @PathVariable Long id,
        @RequestBody MatchRequest request) {

    MatchResponse match = tournamentService.scheduleMatch(id, request);

    return ResponseEntity.status(HttpStatus.CREATED).body(match);
}

@GetMapping("/{id}/standings")
public ResponseEntity<List<TournamentStandingResponse>> getTournamentStandings(
        @PathVariable Long id) {

    return ResponseEntity.ok(
            tournamentService.getTournamentStandings(id)
    );
}

    
}
