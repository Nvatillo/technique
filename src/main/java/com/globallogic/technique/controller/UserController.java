package com.globallogic.technique.controller;

import com.globallogic.technique.dto.request.UserDTO;
import com.globallogic.technique.dto.response.UserSigUpResponseDto;
import com.globallogic.technique.dto.response.UserResponseDto;
import com.globallogic.technique.service.TokenValidationService;
import com.globallogic.technique.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;


@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private TokenValidationService tokenValidation;

    @Autowired
    private UserService userService;

    @PostMapping("/sign-up")
    public UserSigUpResponseDto signUp(@Valid @RequestBody UserDTO userRequest) {
        return userService.signUp(userRequest);
    }

    @GetMapping("/login/{id}")
    public UserResponseDto getUser(@PathVariable UUID id, @RequestHeader("Authorization") String token) {
        return userService.login(id);
    }
}