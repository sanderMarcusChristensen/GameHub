package com.gamehub;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.gamehub.repository.AppUserRepository;

/** Checks application startup, account lookup, and password hashing. */
@SpringBootTest(properties = "cito.api-key=test-key")
class GameHubApplicationTests {

	@Autowired
	private AppUserRepository appUserRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Test
	void contextLoads() {
	}

	@Test
	void canLookUpUserByEmail() {
		assertTrue(appUserRepository.findByEmail("missing-user@gamehub.invalid").isEmpty());
	}

	// Verifies BCrypt hashes a password and can match it without creating an account.
	@Test
	void passwordEncoderHashesAndVerifiesPasswords() {
		String rawPassword = "example-password";
		String passwordHash = passwordEncoder.encode(rawPassword);

		assertTrue(passwordEncoder.matches(rawPassword, passwordHash));
		assertTrue(!passwordHash.equals(rawPassword));
	}

}
