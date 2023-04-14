package com.goodspartner.mapper;

import com.goodspartner.dto.UserDto;
import com.goodspartner.entity.User;
import com.goodspartner.web.controller.response.AuthResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto toUserDto(User user);

    /*@Mapping(target = "heartbeatId", expression = "java(java.util.UUID.randomUUID())")
    AuthResponse toAuthResponse(User user);*/

    User toUser(UserDto userdto);

    @Mapping(target = "id", ignore = true)
    User update(@MappingTarget User user, UserDto userDto);

}
