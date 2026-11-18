package com.gamehub.dto;

import java.time.LocalDateTime;

// The team data returned by the API.
public record TeamDto(Long id, String name, String shortName, LocalDateTime createdAt) { }
