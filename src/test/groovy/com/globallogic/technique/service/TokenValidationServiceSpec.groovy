package com.globallogic.technique.service

import com.globallogic.technique.model.User
import spock.lang.Specification


class TokenValidationServiceSpec extends Specification {

    TokenValidationService tokenService
    final String secret = "12345678901234567890123456789012"
    final long expirationMs = 1000 * 60 * 10

    def setup() {
        tokenService = new TokenValidationService(secret, expirationMs)
    }

    def "You must generate and validate JWT token correctly"() {
        given:
        UUID id = UUID.randomUUID()
        def user = User.builder()
                .id(id)
                .email("test@example.com")
                .password("Password12")
                .name("Test User")
                .build()

        when:
        def token = tokenService.generateJwtToken(user)

        then:
        token != null
        tokenService.validateJwtToken(token)
        tokenService.getUserId("Bearer " + token) == id.toString()
    }

    def "Should return false for an invalid token"() {
        expect:
        !tokenService.validateJwtToken("invalid.token.value")
    }

    def "Should return false for an expired token"() {
        given:
        def shortLivedService = new TokenValidationService(secret, 100)
        def user = User.builder()
                .id(UUID.randomUUID())
                .email("expire@example.com")
                .build()

        def token = shortLivedService.generateJwtToken(user)
        sleep(200)

        expect:
        !shortLivedService.validateJwtToken(token)
    }

    def "Must correctly extract userId from a valid token with Bearer prefix"() {
        given:
        def userId = UUID.randomUUID()
        def user = User.builder()
                .id(userId)
                .email("user@example.com")
                .build()

        def jwt = tokenService.generateJwtToken(user)
        def bearerToken = "Bearer " + jwt

        expect:
        tokenService.getUserId(bearerToken) == userId.toString()
    }

    def "Must throw exception with invalid token"() {
        when:
        tokenService.getUserId("Bearer invalid.token")

        then:
        thrown(IllegalArgumentException)
    }

    def "Should throw exception when there is no Bearer prefix"() {
        when:
        tokenService.getUserId("invalidPrefixToken")

        then:
        thrown(IllegalArgumentException)
    }

    def "Should throw exception when token is null"() {
        when:
        tokenService.getUserId(null)

        then:
        thrown(IllegalArgumentException)
    }
}
