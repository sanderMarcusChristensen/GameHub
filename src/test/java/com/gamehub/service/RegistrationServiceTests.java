package com.gamehub.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import com.gamehub.entity.jpa.AppUser;
import com.gamehub.enums.UserRole;
import com.gamehub.repository.jpa.AppUserRepository;

@ExtendWith(MockitoExtension.class)
class RegistrationServiceTests {

    @Mock
    private AppUserRepository appUserRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private RegistrationService registrationService;

    @Test
    void registersNormalizedEmailWithHashedPasswordAndUserRole() {
        when(appUserRepository.existsByEmail("player@example.com")).thenReturn(false);
        when(passwordEncoder.encode("safe-password")).thenReturn("bcrypt-hash");
        when(appUserRepository.saveAndFlush(any(AppUser.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        AppUser registered = registrationService.register(" Player@Example.com ", "safe-password");

        assertEquals("player@example.com", registered.getEmail());
        assertEquals("bcrypt-hash", registered.getPasswordHash());
        assertEquals(UserRole.USER, registered.getRole());
        verify(passwordEncoder).encode("safe-password");
    }

    @Test
    void rejectsDuplicateEmail() {
        when(appUserRepository.existsByEmail("player@example.com")).thenReturn(true);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> registrationService.register("player@example.com", "safe-password"));

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        verify(passwordEncoder, never()).encode(any());
    }

    @Test
    void rejectsPasswordOverBcryptByteLimit() {
        when(appUserRepository.existsByEmail("player@example.com")).thenReturn(false);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> registrationService.register("player@example.com", "é".repeat(37)));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        verify(passwordEncoder, never()).encode(any());
    }
}