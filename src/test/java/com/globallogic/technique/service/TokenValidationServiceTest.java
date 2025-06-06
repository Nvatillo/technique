package com.globallogic.technique.service;

import com.globallogic.technique.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
    void generateJwtToken_And_ValidateToken_Success() {
        UUID id = UUID.randomUUID();
        User user = User.builder()
                .id(id)
                .email("test@example.com")
                .password("Password12")
                .name("Test User")
                .build();

        String token = tokenService.generateJwtToken(user);
        assertNotNull(token);

        assertTrue(tokenService.validateJwtToken(token));

        String extractedId = tokenService.getUserId("Bearer " + token);
        assertEquals(id.toString(), extractedId);
    }

    @Test
    void validateJwtToken_WithInvalidToken_ReturnsFalse() {
        String invalidToken = "invalid.token.value";
        assertFalse(tokenService.validateJwtToken(invalidToken));
    }

    @Test
    void validateJwtToken_WithExpiredToken_ReturnsFalse() throws InterruptedException {
        TokenValidationService shortLivedService = new TokenValidationService(secret, 100); // 100 ms

        User user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail("expire@example.com");

        String token = shortLivedService.generateJwtToken(user);
        assertNotNull(token);

        Thread.sleep(200); // Wait for expiration

        assertFalse(shortLivedService.validateJwtToken(token));
    }

    @Test
    void getUserId_WithValidBearerToken_ReturnsCorrectUserId() {
        UUID userId = UUID.randomUUID();
        User user = User.builder()
                .id(userId)
                .email("user@example.com")
                .build();

        String jwt = tokenService.generateJwtToken(user);
        String bearerToken = "Bearer " + jwt;

        String extractedUserId = tokenService.getUserId(bearerToken);
        assertEquals(userId.toString(), extractedUserId);
    }

    @Test
    void getUserId_WithInvalidToken_ThrowsException() {
        String malformedToken = "Bearer invalid.token";
        assertThrows(IllegalArgumentException.class, () -> tokenService.getUserId(malformedToken));
    }

    @Test
    void getUserId_WithNoBearerPrefix_ThrowsException() {
        String token = "invalidPrefixToken";
        assertThrows(IllegalArgumentException.class, () -> tokenService.getUserId(token));
    }

    @Test
    void getUserId_WithNullToken_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> tokenService.getUserId(null));
    }
}