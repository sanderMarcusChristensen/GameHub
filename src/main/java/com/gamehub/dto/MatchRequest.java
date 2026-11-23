package com.gamehub.dto;

import java.time.LocalDateTime;

import com.gamehub.enums.MatchFormat;

public record MatchRequest(
    Long teamAId,
    Long teamBId,
    MatchFormat format,
    LocalDateTime scheduledAt
) {
}