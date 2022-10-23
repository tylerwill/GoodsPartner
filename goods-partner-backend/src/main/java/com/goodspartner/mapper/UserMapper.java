package com.goodspartner.mapper;

import com.goodspartner.dto.UserDto;
import com.goodspartner.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto map(User user);

    User map(UserDto userdto);

    @Mapping(target = "id", ignore = true)
    User update(@MappingTarget User user, UserDto userDto);
}
