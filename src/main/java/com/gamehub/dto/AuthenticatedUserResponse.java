package com.gamehub.dto;

import java.util.List;

/** Public identity and roles returned after successful login. */
public record AuthenticatedUserResponse(String email, List<String> roles) {
}