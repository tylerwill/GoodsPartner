package com.goodspartner.service;

import com.goodspartner.dto.UserDto;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;

import java.util.List;

public interface UserService {


    UserDto findByAuthentication(OAuth2AuthenticationToken authentication);

    List<UserDto> findAll();

    UserDto update(int id, UserDto userDto);

    UserDto add(UserDto userDto);

    UserDto delete(int id);

    UserDto findById(int id);
}
