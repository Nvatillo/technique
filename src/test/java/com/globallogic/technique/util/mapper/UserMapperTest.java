package com.globallogic.technique.util.mapper;

import com.globallogic.technique.dto.request.PhoneDto;
import com.globallogic.technique.dto.request.UserDTO;
import com.globallogic.technique.model.Phone;
import com.globallogic.technique.model.User;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class UserMapperTest {

    private final UserMapper mapper = new UserMapperImpl();

    @Test
    void testToEntity() {
        PhoneDto phoneDto = new PhoneDto();
        phoneDto.setNumber(123456);
        phoneDto.setCitycode(1);
        phoneDto.setContrycode("56");

        UserDTO dto = UserDTO.builder()
                .name("Juan")
                .email("juan@example.com")
                .password("1234")
                .phones(Collections.singletonList(phoneDto))
                .build();

        User user = mapper.toEntity(dto);

        assertNotNull(user);
        assertEquals("Juan", user.getName());
        assertEquals("juan@example.com", user.getEmail());
        assertEquals("1234", user.getPassword());

        List<Phone> phones = user.getPhones();
        assertEquals(1, phones.size());
        assertEquals(123456, phones.get(0).getNumber());
    }

    @Test
    void testToDTO() {
        Phone phone = new Phone();
        phone.setNumber(78910);
        phone.setCitycode(9);
        phone.setContrycode("56");

        User user = User.builder()
                .name("Maria")
                .email("maria@example.com")
                .password("abcd")
                .phones(Collections.singletonList(phone))
                .build();

        UserDTO dto = mapper.toDTO(user);

        assertNotNull(dto);
        assertEquals("Maria", dto.getName());
        assertEquals("maria@example.com", dto.getEmail());
        assertEquals("abcd", dto.getPassword());

        List<PhoneDto> phones = dto.getPhones();
        assertEquals(1, phones.size());
        assertEquals(78910, phones.get(0).getNumber());
    }
}