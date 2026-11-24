package com.gamehub.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PlayerRequest(
        @NotBlank(message = "Nickname cannot be empty")
        @Size(min = 2, max = 100, message = "Nickname must be between 2 and 100 characters")
        String nickname
) {}
