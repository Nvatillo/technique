package com.globallogic.technique.exception

import com.fasterxml.jackson.databind.ObjectMapper
import com.globallogic.technique.dto.request.PhoneDto
import com.globallogic.technique.dto.request.UserDTO
import com.globallogic.technique.exception.user.InvalidEmailFormatException
import com.globallogic.technique.exception.user.InvalidPasswordFormatException
import com.globallogic.technique.exception.user.UserAlreadyExistsException
import com.globallogic.technique.exception.user.UserNotFoundException
import com.globallogic.technique.service.TokenValidationService
import com.globallogic.technique.service.UserService
import org.spockframework.spring.SpringBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class UserExceptionHandlingSpec extends Specification {

    @Autowired
    MockMvc mockMvc

    @Autowired
    ObjectMapper objectMapper

    @SpringBean
    UserService userService = Mock()

    UserDTO userDTO

    def setup() {
        userDTO = UserDTO.builder()
                .email("test@example.com")
                .password("Password12")
                .name("Test User")
                .phones([new PhoneDto(123456789, 1, "+54")])
                .build()
    }

    def "Should return 409 when the user already exists"() {
        given:
        userService.signUp(_) >> { throw new UserAlreadyExistsException("User already exist") }

        expect:
        mockMvc.perform(post("/users/sign-up")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath('$.error[0].code').value(409))
                .andExpect(jsonPath('$.error[0].detail').value("User already exist"))
    }

    def "Should return 400 when the email is invalid"() {
        given:
        userService.signUp(_) >> { throw new InvalidEmailFormatException("Email format is invalid") }

        expect:
        mockMvc.perform(post("/users/sign-up")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath('$.error[0].code').value(400))
                .andExpect(jsonPath('$.error[0].detail').value("Email format is invalid"))
    }

    def "Should return 400 when the password is invalid"() {
        given:
        userService.signUp(_) >> { throw new InvalidPasswordFormatException("Password format is invalid") }

        expect:
        mockMvc.perform(post("/users/sign-up")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath('$.error[0].code').value(400))
                .andExpect(jsonPath('$.error[0].detail').value("Password format is invalid"))
    }

    def "Should return 404 when the user is not found"() {
        given:
        def token = "Bearer validtoken"
        userService.login(token) >> { throw new UserNotFoundException("User not found") }

        expect:
        mockMvc.perform(get("/users/login")
                .header("Authorization", token))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath('$.error[0].code').value(404))
                .andExpect(jsonPath('$.error[0].detail').value("User not found"))
    }

    def "Should return 500 when a generic exception occurs"() {
        given:
        userService.signUp(_) >> { throw new RuntimeException("Something unexpected") }

        expect:
        mockMvc.perform(post("/users/sign-up")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath('$.error[0].code').value(500))
                .andExpect(jsonPath('$.error[0].detail').value("Internal Server Error"))
    }
}
