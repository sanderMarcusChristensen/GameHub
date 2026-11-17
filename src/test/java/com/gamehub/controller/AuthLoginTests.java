package com.gamehub.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.server.ResponseStatusException;

import com.gamehub.dto.AuthenticatedUserResponse;
import com.gamehub.dto.LoginRequest;
import com.gamehub.service.RegistrationService;

@ExtendWith(MockitoExtension.class)
class AuthLoginTests {

    @Mock
    private RegistrationService registrationService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private SecurityContextRepository securityContextRepository;

    @Mock
    private SessionAuthenticationStrategy sessionAuthenticationStrategy;

    @InjectMocks
    private AuthController authController;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void successfulLoginSavesAuthenticatedContext() {
        Authentication authentication = UsernamePasswordAuthenticationToken.authenticated(
                "user@example.com", null, List.of(new SimpleGrantedAuthority("ROLE_USER")));
        when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(authentication);
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        AuthenticatedUserResponse result = authController.login(
                new LoginRequest(" User@Example.com ", "safe-password"), request, response);

        assertEquals("user@example.com", result.email());
        assertEquals(List.of("USER"), result.roles());
        ArgumentCaptor<SecurityContext> contextCaptor = ArgumentCaptor.forClass(SecurityContext.class);
        verify(securityContextRepository).saveContext(contextCaptor.capture(), same(request), same(response));
        assertEquals(authentication, contextCaptor.getValue().getAuthentication());
        verify(sessionAuthenticationStrategy).onAuthentication(authentication, request, response);
        verify(authenticationManager).authenticate(any(Authentication.class));
    }

    @Test
    void invalidCredentialsReturnGenericUnauthorizedError() {
        when(authenticationManager.authenticate(any(Authentication.class)))
                .thenThrow(new BadCredentialsException("details should not be exposed"));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> authController.login(
                        new LoginRequest("user@example.com", "wrong-password"),
                        new MockHttpServletRequest(),
                        new MockHttpServletResponse()));

        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
        assertEquals("Invalid email or password", exception.getReason());
        verify(securityContextRepository, never()).saveContext(any(), any(), any());
    }

    @Test
    void currentUserReturnsAuthenticatedEmailAndRole() {
        Authentication authentication = UsernamePasswordAuthenticationToken.authenticated(
                "user@example.com", null, List.of(new SimpleGrantedAuthority("ROLE_USER")));

        AuthenticatedUserResponse result = authController.currentUser(authentication);

        assertEquals("user@example.com", result.email());
        assertEquals(List.of("USER"), result.roles());
    }

    @Test
    void logoutInvalidatesSessionAndClearsAuthentication() {
        Authentication authentication = UsernamePasswordAuthenticationToken.authenticated(
                "user@example.com", null, List.of(new SimpleGrantedAuthority("ROLE_USER")));
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockHttpSession session = new MockHttpSession();
        request.setSession(session);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        ResponseEntity<Void> result = authController.logout(request, response, authentication);

        assertEquals(204, result.getStatusCode().value());
        assertTrue(session.isInvalid());
        assertEquals(null, SecurityContextHolder.getContext().getAuthentication());
    }
}