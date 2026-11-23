package com.gamehub.dto;

import java.math.BigDecimal;

public record TournamentStandingResponse(
        Long teamId,
        String teamName,
        Long matchesPlayed,
        Long wins,
        Long losses,
        BigDecimal matchWinRate
) {}
