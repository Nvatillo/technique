package com.globallogic.technique.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.globallogic.technique.dto.request.PhoneDto;
import com.globallogic.technique.dto.request.UserDTO;
import com.globallogic.technique.exception.user.InvalidEmailFormatException;
import com.globallogic.technique.exception.user.InvalidPasswordFormatException;
import com.globallogic.technique.exception.user.UserAlreadyExistsException;
import com.globallogic.technique.exception.user.UserNotFoundException;
import com.globallogic.technique.service.TokenValidationService;
import com.globallogic.technique.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class UserExceptionHandlingTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private TokenValidationService tokenValidationService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserDTO userDTO;


    @BeforeEach
    void setup() {
        userDTO = UserDTO.builder()
                .email("test@example.com")
                .password("Password12")
                .name("Test User")
                .phones(Collections.singletonList(new PhoneDto(123456789, 1, "+54")))
                .build();
    }

    @Test
    void testUserAlreadyExistsException() throws Exception {
        Mockito.when(userService.signUp(any(UserDTO.class)))
                .thenThrow(new UserAlreadyExistsException("User already exist"));

        mockMvc.perform(post("/users/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error[0].codigo").value(409))
                .andExpect(jsonPath("$.error[0].detail").value("User already exist"));
    }

    @Test
    void testInvalidEmailFormatException() throws Exception {
        Mockito.when(userService.signUp(any(UserDTO.class)))
                .thenThrow(new InvalidEmailFormatException("Email format is invalid"));

        mockMvc.perform(post("/users/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error[0].codigo").value(400))
                .andExpect(jsonPath("$.error[0].detail").value("Email format is invalid"));
    }

    @Test
    void testInvalidPasswordFormatException() throws Exception {
        Mockito.when(userService.signUp(any(UserDTO.class)))
                .thenThrow(new InvalidPasswordFormatException("Password format is invalid"));

        mockMvc.perform(post("/users/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error[0].codigo").value(400))
                .andExpect(jsonPath("$.error[0].detail").value("Password format is invalid"));
    }

    @Test
    void testUserNotFoundException() throws Exception {
        String fakeId = "Bearer validtoken";
        Mockito.when(userService.login(fakeId))
                .thenThrow(new UserNotFoundException("User not found"));

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/users/login")
                        .header("Authorization", fakeId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error[0].codigo").value(404))
                .andExpect(jsonPath("$.error[0].detail").value("User not found"));
    }

    @Test
    void testGenericException() throws Exception {
        Mockito.when(userService.signUp(any(UserDTO.class)))
                .thenThrow(new RuntimeException("Something unexpected"));

        mockMvc.perform(post("/users/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error[0].codigo").value(500))
                .andExpect(jsonPath("$.error[0].detail").value("Error interno del servidor"));
    }

}