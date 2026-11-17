package com.gamehub.dto;

import com.gamehub.entity.UserRole;

/** Public account details returned after registration; excludes the password hash. */
public record RegistrationResponse(Long id, String email, UserRole role) {
}