package com.gamehub.dto;

import java.time.LocalDateTime;

public record GameResultRequest(
        Integer gameNumber,
        Long winnerTeamId,
        LocalDateTime startedAt,
        LocalDateTime endedAt
) {}
