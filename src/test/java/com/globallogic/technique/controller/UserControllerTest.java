package com.globallogic.technique.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.globallogic.technique.dto.request.PhoneDto;
import com.globallogic.technique.dto.request.UserDTO;
import com.globallogic.technique.dto.response.PhoneResponseDto;
import com.globallogic.technique.dto.response.UserResponseDto;
import com.globallogic.technique.dto.response.UserSigUpResponseDto;
import com.globallogic.technique.service.TokenValidationService;
import com.globallogic.technique.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private TokenValidationService tokenValidationService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserDTO userDTO;
    private UserSigUpResponseDto userSigUpResponseDto;
    private UserResponseDto userResponseDto;
    private UUID userId;

    @BeforeEach
    void setup() {
        userId = UUID.randomUUID();

        PhoneResponseDto phoneResponseDto = PhoneResponseDto.builder()
                .number(123456)
                .citycode(11)
                .contrycode("+54")
                .build();

        userDTO = UserDTO.builder()
                .email("test@example.com")
                .password("Password12")
                .name("Test User")
                .phones(Collections.singletonList(new PhoneDto(123456789, 1, "+54")))
                .build();

        userSigUpResponseDto = UserSigUpResponseDto.builder()
                .id(userId)
                .created(LocalDateTime.now())
                .lastLogin(LocalDateTime.now())
                .token("mocktoken")
                .isActive(true)
                .build();

        userResponseDto = userResponseDto.builder()
                .id(userId)
                .created(LocalDateTime.now())
                .lastLogin(LocalDateTime.now())
                .token("mocktoken")
                .email("julio@testssw.cl")
                .password("mockPassword")
                .isActive(true)
                .phones(List.of(phoneResponseDto))
                .build();
    }

    @Test
    void testSignUp() throws Exception {
        when(userService.signUp(any(UserDTO.class))).thenReturn(userSigUpResponseDto);

        mockMvc.perform(post("/users/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mocktoken"));
    }

    @Test
    void testGetUserWithToken() throws Exception {
        String token = "Bearer validtoken";

        when(tokenValidationService.validateJwtToken("validtoken")).thenReturn(true);
        when(userService.login(userId)).thenReturn(userResponseDto);

        mockMvc.perform(get("/users/login/{id}", userId)
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId.toString()));
    }

    @Test
    void testGetUserWithInvalidToken() throws Exception {
        String token = "Bearer invalidtoken";

        when(tokenValidationService.validateJwtToken("invalidtoken")).thenReturn(false);

        mockMvc.perform(get("/users/login/{id}", userId)
                        .header("Authorization", token))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetUserWithoutToken() throws Exception {
        mockMvc.perform(get("/users/login/{id}", userId))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testSignUp_MissingEmail_ShouldReturnBadRequest() throws Exception {
        UserDTO invalidUser = UserDTO.builder()
                .password("Password12")
                .name("Test User")
                .phones(Collections.emptyList())
                .build();

        mockMvc.perform(post("/users/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUser)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error[0].detail").value("email cannot be empty"));
    }

    @Test
    void testSignUp_MissingPassword_ShouldReturnBadRequest() throws Exception {
        UserDTO invalidUser = UserDTO.builder()
                .email("test@example.com")
                .name("Test User")
                .phones(Collections.emptyList())
                .build();

        mockMvc.perform(post("/users/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUser)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error[0].detail").value("password cannot be empty"));
    }

    @Test
    void testInvalidToken() throws Exception {
        UUID fakeId = UUID.randomUUID();
        when(tokenValidationService.validateJwtToken("Bearer invalidtoken")).thenReturn(false);

        mockMvc.perform(get("/users/login/" + fakeId)
                        .header("Authorization", "Bearer invalidtoken"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error[0].code").value(401))
                .andExpect(jsonPath("$.error[0].detail").value("Invalid or expired token"));
    }
}