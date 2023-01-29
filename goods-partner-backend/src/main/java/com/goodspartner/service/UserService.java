package com.goodspartner.service;

import com.goodspartner.dto.UserDto;
import com.goodspartner.entity.User;

import java.util.List;

public interface UserService {

    List<User> findAll();

    User update(int id, UserDto userDto);

    User add(UserDto userDto);

    User delete(int id);

    User findById(int id);

    User findByRouteId(long routeId);

    /* --- Auth --- */

    User findByAuthentication();

    UserDto getAuthenticatedUserDto();
}
