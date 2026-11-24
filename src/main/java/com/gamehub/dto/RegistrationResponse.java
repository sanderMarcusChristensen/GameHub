package com.gamehub.dto;

import com.gamehub.enums.UserRole;

/** Public account details returned after registration; excludes the password hash. */
public record RegistrationResponse(Long id, String email, UserRole role) {
}