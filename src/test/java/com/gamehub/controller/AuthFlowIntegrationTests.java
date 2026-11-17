package com.gamehub.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import com.gamehub.repository.AppUserRepository;
import com.gamehub.service.RegistrationService;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "cito.api-key=test-key")
class AuthFlowIntegrationTests {

    private static final String TEST_PASSWORD = "integration-test-password";

    @Value("${local.server.port}")
    private int port;

    @Autowired
    private RegistrationService registrationService;

    @Autowired
    private AppUserRepository appUserRepository;

    private final String email = "auth-test-" + UUID.randomUUID() + "@example.com";
    private HttpClient client;

    @BeforeEach
    void createTemporaryAccount() {
        registrationService.register(email, TEST_PASSWORD);
        CookieManager cookies = new CookieManager(null, CookiePolicy.ACCEPT_ALL);
        client = HttpClient.newBuilder().cookieHandler(cookies).build();
    }

    @AfterEach
    void removeTemporaryAccount() {
        appUserRepository.findByEmail(email).ifPresent(appUserRepository::delete);
    }

    @Test
    void loginMeAndLogoutUseTheSameSession() throws Exception {
        String csrfToken = getCsrfToken();
        HttpResponse<String> login = post("/api/auth/login", csrfToken,
                "{\"email\":\"" + email + "\",\"password\":\"" + TEST_PASSWORD + "\"}");

        assertEquals(200, login.statusCode());
        assertTrue(login.body().contains(email));
        assertTrue(login.body().contains("USER"));

        HttpResponse<String> currentUser = get("/api/auth/me");
        assertEquals(200, currentUser.statusCode());
        assertTrue(currentUser.body().contains(email));

        HttpResponse<String> logout = post("/api/auth/logout", csrfToken, "");
        assertEquals(204, logout.statusCode());

        HttpResponse<String> afterLogout = get("/api/auth/me");
        assertTrue(afterLogout.statusCode() == 401 || afterLogout.statusCode() == 403);
    }

    @Test
    void wrongPasswordDoesNotStartASession() throws Exception {
        String csrfToken = getCsrfToken();
        HttpResponse<String> login = post("/api/auth/login", csrfToken,
                "{\"email\":\"" + email + "\",\"password\":\"wrong-password\"}");

        assertEquals(401, login.statusCode());
    }

    private String getCsrfToken() throws IOException, InterruptedException {
        HttpResponse<String> response = get("/api/auth/csrf");
        assertEquals(200, response.statusCode());
        Matcher matcher = Pattern.compile("\\\"token\\\"\\s*:\\s*\\\"([^\\\"]+)\\\"")
                .matcher(response.body());
        assertTrue(matcher.find(), "CSRF response should contain a token");
        return matcher.group(1);
    }

    private HttpResponse<String> get(String path) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder(uri(path)).GET().build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> post(String path, String csrfToken, String body)
            throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder(uri(path))
                .header("Content-Type", "application/json")
                .header("X-CSRF-TOKEN", csrfToken)
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private URI uri(String path) {
        return URI.create("http://127.0.0.1:" + port + path);
    }
}