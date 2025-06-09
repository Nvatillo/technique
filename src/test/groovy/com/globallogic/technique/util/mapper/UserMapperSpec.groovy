package com.globallogic.technique.util.mapper

import com.globallogic.technique.dto.request.PhoneDto
import com.globallogic.technique.dto.request.UserDTO
import com.globallogic.technique.model.Phone
import com.globallogic.technique.model.User
import org.mapstruct.factory.Mappers
import spock.lang.Specification

class UserMapperSpec extends Specification {

    def mapper = Mappers.getMapper(UserMapper)

    def "toEntity correctly converts from DTO to entity"() {
        given:
        def phoneDto = new PhoneDto()
        phoneDto.number = 123456
        phoneDto.citycode = 1
        phoneDto.contrycode = "56"

        def dto = UserDTO.builder()
                .name("Juan")
                .email("juan@example.com")
                .password("1234")
                .phones([phoneDto])
                .build()

        when:
        def user = mapper.toEntity(dto)

        then:
        user != null
        user.name == "Juan"
        user.email == "juan@example.com"
        user.password == "1234"
        user.phones.size() == 1
        user.phones[0].number == 123456
        user.phones[0].citycode == 1
        user.phones[0].contrycode == "56"
    }

    def "toDTO correctly converts from entity to DTO"() {
        given:
        def phone = new Phone()
        phone.number = 78910
        phone.citycode = 9
        phone.contrycode = "56"

        def user = User.builder()
                .name("Maria")
                .email("maria@example.com")
                .password("abcd")
                .phones([phone])
                .build()

        when:
        def dto = mapper.toDTO(user)

        then:
        dto != null
        dto.name == "Maria"
        dto.email == "maria@example.com"
        dto.password == "abcd"
        dto.phones.size() == 1
        dto.phones[0].number == 78910
        dto.phones[0].citycode == 9
        dto.phones[0].contrycode == "56"
    }
}
