package com.globallogic.technique.service;


import com.globallogic.technique.dto.request.UserDTO;
import com.globallogic.technique.dto.response.UserSigUpResponseDto;
import com.globallogic.technique.dto.response.UserResponseDto;
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

import static com.globallogic.technique.util.UserValidation.*;


@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenValidationService tokenValidationService;

    public UserSigUpResponseDto signUp(UserDTO userRequest) {
        User user = userMapper.toEntity(userRequest);
        validateUserData(user);
        user.setPassword(hashPassword(user.getPassword()));
        setUserDefaults(user);
        String token = generateUserToken(user);
        user.setToken(token);
        userRepository.save(user);
        return convertToSignUpResponseDTO(user);
    }

    public UserResponseDto login(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        String newToken = generateUserToken(user);
        user.setToken(newToken);
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);

        return convertToLoginResponseDTO(user);
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