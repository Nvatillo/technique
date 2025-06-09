package com.globallogic.technique.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.globallogic.technique.dto.request.PhoneDto
import com.globallogic.technique.dto.request.UserDTO
import com.globallogic.technique.dto.response.PhoneResponseDto
import com.globallogic.technique.dto.response.UserResponseDto
import com.globallogic.technique.dto.response.UserSigUpResponseDto
import com.globallogic.technique.service.TokenValidationService
import com.globallogic.technique.service.UserService
import org.spockframework.spring.SpringBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification

import java.time.LocalDateTime

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerSpec extends Specification {

    @Autowired
    MockMvc mockMvc

    @Autowired
    ObjectMapper objectMapper

    @SpringBean
    UserService userService = Mock()

    @SpringBean
    TokenValidationService tokenValidationService = Mock()

    def userDTO
    def userSigUpResponseDto
    def userResponseDto
    def userId

    def setup() {
        userId = UUID.randomUUID()

        def phoneResponseDto = PhoneResponseDto.builder()
                .number(123456)
                .citycode(11)
                .contrycode("+54")
                .build()

        userDTO = UserDTO.builder()
                .email("test@example.com")
                .password("Password12")
                .name("Test User")
                .phones([new PhoneDto(123456789, 1, "+54")])
                .build()

        userSigUpResponseDto = UserSigUpResponseDto.builder()
                .id(userId)
                .created(LocalDateTime.now())
                .lastLogin(LocalDateTime.now())
                .token("mocktoken")
                .isActive(true)
                .build()

        userResponseDto = UserResponseDto.builder()
                .id(userId)
                .created(LocalDateTime.now())
                .lastLogin(LocalDateTime.now())
                .token("mocktoken")
                .email("julio@testssw.cl")
                .password("mockPassword")
                .isActive(true)
                .phones([phoneResponseDto])
                .build()
    }

    def "Sign up user successful returns token"() {
        given:
        userService.signUp(_) >> userSigUpResponseDto

        expect:
        mockMvc.perform(post("/users/sign-up")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath('$.token').value("mocktoken"))
    }

    def "Login with valid token returns user"() {
        given:
        def token = "Bearer validtoken"
        tokenValidationService.validateJwtToken("validtoken") >> true
        userService.login(token) >> userResponseDto

        expect:
        mockMvc.perform(get("/users/login/")
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath('$.id').value(userId.toString()))
    }

    def "Login with invalid token returns 401"() {
        given:
        def token = "Bearer invalidtoken"
        tokenValidationService.validateJwtToken("invalidtoken") >> false

        expect:
        mockMvc.perform(get("/users/login/{id}", userId)
                .header("Authorization", token))
                .andExpect(status().isUnauthorized())
    }

    def "Login without token returns 401"() {
        expect:
        mockMvc.perform(get("/users/login/{id}", userId))
                .andExpect(status().isUnauthorized())
    }

    def "Sign up without email returns BadRequest"() {
        given:
        def invalidUser = UserDTO.builder()
                .password("Password12")
                .name("Test User")
                .phones([])
                .build()

        expect:
        mockMvc.perform(post("/users/sign-up")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidUser)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath('$.error[0].detail').value("email cannot be empty"))
    }

    def "Sign up without password returns BadRequest"() {
        given:
        def invalidUser = UserDTO.builder()
                .email("test@example.com")
                .name("Test User")
                .phones([])
                .build()

        expect:
        mockMvc.perform(post("/users/sign-up")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidUser)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath('$.error[0].detail').value("password cannot be empty"))
    }

    def "Invalid token returns 401 error with message"() {
        given:
        def fakeId = UUID.randomUUID()
        tokenValidationService.validateJwtToken("Bearer invalidtoken") >> false

        expect:
        mockMvc.perform(get("/users/login/$fakeId")
                .header("Authorization", "Bearer invalidtoken"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath('$.error[0].code').value(401))
                .andExpect(jsonPath('$.error[0].detail').value("Invalid or expired token"))
    }
}
