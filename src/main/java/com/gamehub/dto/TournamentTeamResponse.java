package com.gamehub.dto;

import java.time.LocalDateTime;

public record TournamentTeamResponse(
    Long tournamentId,
    String tournamentName,
    Long teamId,
    String teamName,
    LocalDateTime registeredAt
) {
}