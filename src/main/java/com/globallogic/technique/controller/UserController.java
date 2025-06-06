package com.globallogic.technique.controller;

import com.globallogic.technique.dto.request.UserDTO;
import com.globallogic.technique.dto.response.UserResponseDto;
import com.globallogic.technique.dto.response.UserSigUpResponseDto;
import com.globallogic.technique.service.TokenValidationService;
import com.globallogic.technique.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @GetMapping("/login")
    public UserResponseDto getUser(@RequestHeader("Authorization") String token) {
        return userService.login(token);
    }
}