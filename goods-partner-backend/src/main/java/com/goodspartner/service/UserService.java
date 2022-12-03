package com.goodspartner.service;

import com.goodspartner.dto.UserDto;
import com.goodspartner.entity.User;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;

import java.util.List;

public interface UserService {

    List<User> findAll();

    User update(int id, UserDto userDto);

    User add(UserDto userDto);

    User delete(int id);

    User findById(int id);

    User findByRouteId(long routeId);

    /* --- Auth --- */

    User findByAuthentication(OAuth2AuthenticationToken authentication);

}
