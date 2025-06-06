package com.globallogic.technique.util.mapper;

import com.globallogic.technique.dto.request.UserDTO;
import com.globallogic.technique.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "lastLogin", ignore = true)
    @Mapping(target = "created", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    User toEntity(UserDTO dto);
    UserDTO toDTO(User user);
}