package com.globallogic.technique.service;

import com.globallogic.technique.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class TokenValidationServiceTest {

    private TokenValidationService tokenService;
    private final String secret = "12345678901234567890123456789012";
    private final long expirationMs = 1000 * 60 * 10;

    @BeforeEach
    void setUp() {
        tokenService = new TokenValidationService(secret, expirationMs);
    }

    @Test
    void generateAndValidateToken_success() {
        User user = User.builder()
                .id(UUID.randomUUID())
                .email("test@example.com")
                .password("Password12")
                .name("Test User").build();

        String token = tokenService.generateJwtToken(user);
        assertNotNull(token);

        boolean valid = tokenService.validateJwtToken(token);
        assertTrue(valid);

        String email = tokenService.getEmailFromToken(token);
        assertEquals("test@example.com", email);
    }

    @Test
    void validateToken_invalidToken() {
        String invalidToken = "token-invalido";

        boolean valid = tokenService.validateJwtToken(invalidToken);
        assertFalse(valid);
    }

    @Test
    void validateToken_expiredToken() throws InterruptedException {
        TokenValidationService shortExpiryService = new TokenValidationService(secret, 100);

        User user = new User();
        user.setEmail("expire@example.com");

        String token = shortExpiryService.generateJwtToken(user);
        assertNotNull(token);

        Thread.sleep(200);

        boolean valid = shortExpiryService.validateJwtToken(token);
        assertFalse(valid);
    }

    @Test
    void clearToken_ValidBearerToken_ReturnsTokenWithoutPrefix() {
        String token = "Bearer abcdef12345";
        String result = tokenService.clearToken(token);
        assertEquals("abcdef12345", result);
    }

    @Test
    void clearToken_NullToken_ReturnsNull() {
        String result = tokenService.clearToken(null);
        assertNull(result);
    }

    @Test
    void clearToken_EmptyToken_ReturnsNull() {
        String result = tokenService.clearToken("");
        assertNull(result);
    }

    @Test
    void clearToken_TokenWithoutBearerPrefix_ReturnsNull() {
        String token = "Token abcdef12345";
        String result = tokenService.clearToken(token);
        assertNull(result);
    }

    @Test
    void clearToken_TokenWithOnlyBearerPrefix_ReturnsEmptyString() {
        String token = "Bearer ";
        String result = tokenService.clearToken(token);
        assertEquals("", result);
    }
}