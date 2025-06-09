package com.globallogic.technique.service

import com.globallogic.technique.dto.request.UserDTO
import com.globallogic.technique.dto.response.UserResponseDto
import com.globallogic.technique.dto.response.UserSigUpResponseDto
import com.globallogic.technique.exception.user.InvalidEmailFormatException
import com.globallogic.technique.exception.user.InvalidPasswordFormatException
import com.globallogic.technique.exception.user.UserAlreadyExistsException
import com.globallogic.technique.exception.user.UserNotFoundException
import com.globallogic.technique.model.User
import com.globallogic.technique.repository.UserRepository
import com.globallogic.technique.util.mapper.UserMapper
import org.springframework.security.crypto.password.PasswordEncoder
import spock.lang.Specification

class UserServiceSpec extends Specification {

    def userRepository = Mock(UserRepository)
    def userMapper = Mock(UserMapper)
    def tokenService = Mock(TokenValidationService)
    def encoder = Mock(PasswordEncoder)

    def userService = new UserService(userRepository, userMapper, tokenService, encoder)

    def userDTO
    def user

    def setup() {
        userDTO = UserDTO.builder()
                .email("test@example.com")
                .password("Password12")
                .name("Test User")
                .build()

        user = User.builder()
                .id(UUID.randomUUID())
                .email("test@example.com")
                .password("Password12")
                .name("Test User")
                .build()

    }

    def "should return UserSigUpResponseDto with token and active user when signUp is successful"() {
        given:
        userMapper.toEntity(userDTO) >> user
        userRepository.findByEmail(user.email) >> Optional.empty()
        tokenService.generateJwtToken(user) >> "mockToken"

        when:
        def result = userService.signUp(userDTO)

        then:
        result instanceof UserSigUpResponseDto
        result.token == "mockToken"
        result.isActive
        1 * userRepository.save(_ as User)
    }

    def "should throw InvalidEmailFormatException when email is invalid"() {
        given:
        user.email = "bademail"
        userMapper.toEntity(userDTO) >> user

        when:
        userService.signUp(userDTO)

        then:
        thrown(InvalidEmailFormatException)
    }

    def "should throw InvalidPasswordFormatException when password is invalid"() {
        given:
        user.password = "abc123"
        userMapper.toEntity(userDTO) >> user

        when:
        userService.signUp(userDTO)

        then:
        thrown(InvalidPasswordFormatException)
    }

    def "should throw UserAlreadyExistsException when user already exists"() {
        given:
        userMapper.toEntity(userDTO) >> user
        userRepository.findByEmail(user.email) >> Optional.of(new User())

        when:
        userService.signUp(userDTO)

        then:
        thrown(UserAlreadyExistsException)
    }

    def "should return UserResponseDto when login is successful"() {
        given:
        def bearerToken = "Bearer Token"
        def userId = UUID.randomUUID().toString()

        tokenService.getUserId(bearerToken) >> userId
        userRepository.findById(UUID.fromString(userId)) >> Optional.of(user)

        when:
        def result = userService.login(bearerToken)

        then:
        result instanceof UserResponseDto
        result.email == user.email
    }

    def "should throw UserNotFoundException when user is not found during login"() {
        given:
        def bearerToken = "Bearer Token"
        def userId = UUID.randomUUID().toString()

        tokenService.getUserId(bearerToken) >> userId
        userRepository.findById(UUID.fromString(userId)) >> Optional.empty()

        when:
        userService.login(bearerToken)

        then:
        thrown(UserNotFoundException)
    }
}
