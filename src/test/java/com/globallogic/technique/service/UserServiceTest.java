package com.globallogic.technique.service;

import com.globallogic.technique.dto.request.UserDTO;
import com.globallogic.technique.dto.response.UserResponseDto;
import com.globallogic.technique.dto.response.UserSigUpResponseDto;
import com.globallogic.technique.exception.user.InvalidEmailFormatException;
import com.globallogic.technique.exception.user.InvalidPasswordFormatException;
import com.globallogic.technique.exception.user.UserAlreadyExistsException;
import com.globallogic.technique.exception.user.UserNotFoundException;
import com.globallogic.technique.model.User;
import com.globallogic.technique.repository.UserRepository;
import com.globallogic.technique.util.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserMapper userMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TokenValidationService tokenValidationService;

    @Mock
    private PasswordEncoder passwordEncoder;

    private UserDTO userDTO;
    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        userDTO = UserDTO.builder()
                .email("test@example.com")
                .password("Password12")
                .name("Test User")
                .build();

        user = User.builder()
                .id(UUID.randomUUID())
                .email("test@example.com")
                .password("Password12")
                .name("Test User").build();


    }

    @Test
    void signUp_ValidData_Success() {
        // Arrange
        when(userMapper.toEntity(userDTO)).thenReturn(user);
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());
        when(tokenValidationService.generateJwtToken(user)).thenReturn("mockToken");

        // Act
        UserSigUpResponseDto result = userService.signUp(userDTO);

        // Assert
        assertEquals("mockToken", result.getToken());
        assertEquals(true, result.getIsActive());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void signUp_InvalidEmail_ThrowsException() {
        user.setEmail("bademail");

        when(userMapper.toEntity(userDTO)).thenReturn(user);

        assertThrows(InvalidEmailFormatException.class, () -> userService.signUp(userDTO));
    }

    @Test
    void signUp_InvalidPassword_ThrowsException() {
        user.setPassword("abc123");

        when(userMapper.toEntity(userDTO)).thenReturn(user);

        assertThrows(InvalidPasswordFormatException.class, () -> userService.signUp(userDTO));
    }

    @Test
    void signUp_UserAlreadyExists_ThrowsException() {
        when(userMapper.toEntity(userDTO)).thenReturn(user);
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(new User()));

        assertThrows(UserAlreadyExistsException.class, () -> userService.signUp(userDTO));
    }

    @Test
    void getUser_UserExists_ReturnsUser() {
        UUID userId = UUID.randomUUID();
        user.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        UserResponseDto result = userService.login(userId);

        assertEquals(user.getEmail(), result.getEmail());
    }

    @Test
    void getUser_UserNotFound_ThrowsException() {
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.login(userId));
    }
}