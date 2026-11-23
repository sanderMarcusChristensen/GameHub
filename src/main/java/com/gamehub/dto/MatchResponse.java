package com.gamehub.dto;

import java.time.LocalDateTime;

import com.gamehub.enums.MatchFormat;
import com.gamehub.enums.MatchStatus;

public record MatchResponse(
    Long id,
    Long tournamentId,
    Long teamAId,
    String teamAName,
    Long teamBId,
    String teamBName,
    MatchFormat format,
    MatchStatus status,
    LocalDateTime scheduledAt
) {
}