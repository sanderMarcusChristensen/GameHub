package com.gamehub.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// Fields a client can provide when creating or updating a team.
public record TeamRequestDto(
        @NotBlank(message = "Name cannot be empty")
        @Size(max = 100, message = "Name must be at most 100 characters")
        String name,
        @Size(max = 20, message = "Short name must be at most 20 characters")
        String shortName
) { }
