package com.goodspartner.mapper;

import com.goodspartner.dto.UserDto;
import com.goodspartner.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto toUserDto(User user);

    User toUser(UserDto userdto);

    @Mapping(target = "id", ignore = true)
    User update(@MappingTarget User user, UserDto userDto);

    default UserDto mapAuthentication(OAuth2AuthenticationToken authenticationToken) {
        OAuth2User principal = authenticationToken.getPrincipal();
        return UserDto.builder()
                .userName(principal.getAttribute("username"))
                .email(principal.getAttribute("email"))
                .role(authenticationToken.getAuthorities().toArray()[0].toString())
                .enabled(true)
                .build();
    }
}
