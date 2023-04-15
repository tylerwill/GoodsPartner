package com.goodspartner.service;

import com.goodspartner.dto.UserDto;
import com.goodspartner.entity.User;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

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

    Optional<User> findByUserName(String username);

    UserDto getUserDto(UUID heartbeatId);

    void mapUserDtoToHeartbeatId(UUID heartbeatId, Authentication auth);
}
