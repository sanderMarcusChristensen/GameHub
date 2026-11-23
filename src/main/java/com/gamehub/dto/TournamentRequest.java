package com.gamehub.dto;

import java.time.LocalDateTime;

public record TournamentRequest(
    String name,
    LocalDateTime startDate,
    LocalDateTime endDate
) {
}