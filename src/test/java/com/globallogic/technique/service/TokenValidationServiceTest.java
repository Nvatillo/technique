package com.globallogic.technique.service;

import com.globallogic.technique.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;


public class TokenValidationServiceTest {

    private TokenValidationService tokenService;
    private final String secret = "12345678901234567890123456789012"; // 32 chars para HS256
    private final long expirationMs = 1000 * 60 * 10; // 10 minutos

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
}