package com.globallogic.technique.util;


import com.globallogic.technique.dto.response.PhoneResponseDto;
import com.globallogic.technique.dto.response.UserResponseDto;
import com.globallogic.technique.model.Phone;
import com.globallogic.technique.model.User;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class UserValidation {

    private static final String EMAIL_REGEX = "^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$";

    public static boolean isValidEmail(String email) {
        if (email == null) return false;
        Pattern pattern = Pattern.compile(EMAIL_REGEX);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static boolean isValidPassword(String password) {
        if (password == null || password.length() < 8 || password.length() > 12) {
            return false;
        }

        int uppercaseCount = 0;
        int digitCount = 0;

        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) {
                uppercaseCount++;
            } else if (Character.isDigit(c)) {
                digitCount++;
            } else if (!Character.isLowerCase(c)) {
                return false;
            }
        }

        return uppercaseCount == 1 && digitCount == 2;
    }

    public static UserResponseDto convertToUserResponseDTO(User user) {
        return UserResponseDto.builder()
                .id(user.getId())
                .created(user.getCreated())
                .lastLogin(user.getLastLogin())
                .token(user.getToken())
                .isActive(user.isActive())
                .name(user.getName())
                .email(user.getEmail())
                .password(user.getPassword())
                .phones(mapPhones(user.getPhones()))
                .build();
    }

    private static List<PhoneResponseDto> mapPhones(List<Phone> phones) {
        return Optional.ofNullable(phones)
                .orElse(Collections.emptyList())
                .stream()
                .map(phone -> PhoneResponseDto.builder()
                        .number(phone.getNumber())
                        .citycode(phone.getCitycode())
                        .contrycode(phone.getContrycode())
                        .build())
                .collect(Collectors.toList());
    }
}