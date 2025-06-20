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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.globallogic.technique.util.UserValidation.convertToLoginResponseDTO;
import static com.globallogic.technique.util.UserValidation.convertToSignUpResponseDTO;
import static com.globallogic.technique.util.UserValidation.isValidEmail;
import static com.globallogic.technique.util.UserValidation.isValidPassword;


@Service
public class UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final TokenValidationService tokenValidationService;

    public UserService(UserRepository userRepository,
                       UserMapper userMapper,
                       TokenValidationService tokenValidationService,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.tokenValidationService = tokenValidationService;
        this.passwordEncoder = passwordEncoder;
    }

    public UserSigUpResponseDto signUp(UserDTO userRequest) {
        User user = userMapper.toEntity(userRequest);

        validateUserData(user);
        user.setPassword(hashPassword(user.getPassword()));
        setUserDefaults(user);

        userRepository.save(user);

        String token = generateUserToken(user);
        return convertToSignUpResponseDTO(user, token);
    }

    public UserResponseDto login(String token) {
        String userId = tokenValidationService.getUserId(token);

        User user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);

        String newToken = generateUserToken(user);

        return convertToLoginResponseDTO(user, newToken);
    }

    private void validateUserData(User user) {
        if (!isValidEmail(user.getEmail())) {
            throw new InvalidEmailFormatException("Email format is invalid: " + user.getEmail());
        }

        if (!isValidPassword(user.getPassword())) {
            throw new InvalidPasswordFormatException("Password format is invalid: must contain exactly 1 uppercase letter, 2 digits, only lowercase letters, and be 8–12 characters long.");
        }

        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException("User already exist");
        }
    }

    private String hashPassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    private void setUserDefaults(User user) {
        LocalDateTime now = LocalDateTime.now();
        user.setCreated(now);
        user.setLastLogin(now);
        user.setActive(true);
    }

    private String generateUserToken(User user) {
        return tokenValidationService.generateJwtToken(user);
    }


}