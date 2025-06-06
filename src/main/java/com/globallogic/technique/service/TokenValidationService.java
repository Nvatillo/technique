package com.globallogic.technique.service;

import com.globallogic.technique.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Slf4j
@Service
public class TokenValidationService {

    private final SecretKey secretKey;
    private final long expirationTimeMs;

    public TokenValidationService(@Value("${jwt.secret}") String secretKeyString,
                                  @Value("${jwt.expiration}") long expirationTimeMs) {
        this.secretKey = Keys.hmacShaKeyFor(secretKeyString.getBytes());
        this.expirationTimeMs = expirationTimeMs;
    }

    public String generateJwtToken(User user) {
        return Jwts.builder()
                .setSubject(user.getId().toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTimeMs))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateJwtToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            log.error("Invalid or expired token: " + e.getMessage());
            return false;
        }
    }
    

    public String getUserId(String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Token inválido o nulo");
        }

        String userToken = token.substring(7); // Elimina "Bearer "

        try {
            return Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(userToken)
                    .getBody()
                    .getSubject();
        } catch (JwtException e) {
            throw new IllegalArgumentException("Token inválido", e);
        }
    }
}