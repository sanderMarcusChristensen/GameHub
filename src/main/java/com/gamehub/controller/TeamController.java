package com.gamehub.controller;

import com.gamehub.dto.TeamDto;
import com.gamehub.dto.TeamRequestDto;
import com.gamehub.service.TeamService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/teams")
public class TeamController {
    private final TeamService teamService;

    public TeamController(TeamService teamService) {
        this.teamService = teamService;
    }

    // Return all teams.
    @GetMapping
    public List<TeamDto> getAllTeams() {
        return teamService.getAll();
    }

    // Return a single team by its ID.
    @GetMapping("/{id}")
    public TeamDto getTeamById(@PathVariable Long id) {
        return teamService.getById(id);
    }

    // Create a team and return its new URL.
    @PostMapping
    public ResponseEntity<TeamDto> createTeam(@Valid @RequestBody TeamRequestDto dto) {
        TeamDto team = teamService.create(dto);
        return ResponseEntity.created(URI.create("/api/teams/" + team.id())).body(team);
    }

    // Replace the team's editable fields.
    @PutMapping("/{id}")
    public TeamDto updateTeam(@PathVariable Long id, @Valid @RequestBody TeamRequestDto dto) {
        return teamService.update(id, dto);
    }

    // Delete a team and return an empty response.
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTeam(@PathVariable Long id) {
        teamService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // Return the first validation error as a clear 400 response.
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleInvalidInput(MethodArgumentNotValidException ex) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problem.setTitle("Invalid team data");
        problem.setDetail(ex.getBindingResult().getFieldErrors().stream()
                .findFirst().map(error -> error.getDefaultMessage()).orElse("Invalid input."));
        return problem;
    }
}
