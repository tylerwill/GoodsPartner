package com.goodspartner.service.impl;

import com.goodspartner.dto.UserDto;
import com.goodspartner.entity.User;
import com.goodspartner.exception.DriverNotFoundException;
import com.goodspartner.exception.InvalidAuthenticationType;
import com.goodspartner.exception.UserNotFoundException;
import com.goodspartner.mapper.UserMapper;
import com.goodspartner.repository.UserRepository;
import com.goodspartner.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@AllArgsConstructor
@Service
public class DefaultUserService implements UserService {

    private static final String DEFAULT_SORT_FILED = "id";
    private static final String EMAIL_ATTRIBUTE = "email";
    private static final String USERNAME_ATTRIBUTE = "username";

    private final Map<UUID, UserDto> userToHeartbeatId = new ConcurrentHashMap<>();
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public List<User> findAll() {
        return userRepository.findAll(Sort.by(Sort.Direction.ASC, DEFAULT_SORT_FILED));
    }

    @Override
    public User findById(int id) {
        return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
    }

    @Override
    public User findByRouteId(long routeId) {
        return userRepository.findByRouteId(routeId).orElseThrow(() -> new DriverNotFoundException(routeId));
    }

    @Transactional
    @Override
    public User update(int id, UserDto userDto) {
        return userRepository.findById(id)
                .map(user -> userMapper.update(user, userDto))
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    @Transactional
    @Override
    public User add(UserDto userDto) {
        User user = userMapper.toUser(userDto);
        return userRepository.save(user);
    }

    @Override
    public User delete(int id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        userRepository.delete(user);
        return user;
    }

    /* --- Auth --- */

    @Override
    public User findByAuthentication() {
        String username = getUsernameFromAuthenticationContext();
        return userRepository.findByUserName(username)
                .orElseThrow(() -> new UserNotFoundException(username));
    }

    private String getUsernameFromAuthenticationContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.isAuthenticated()) {
            UserDetails principal = (UserDetails) authentication.getPrincipal();
            return principal.getUsername();
        }
        throw new InvalidAuthenticationType();
    }

    //    FIXME: Maybe unnecessary method
    @Override
    public UserDto getAuthenticatedUserDto() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .map(Authentication::getPrincipal)
                .map(principal -> (UserDetails) principal)
                .map(principal -> UserDto.builder()
                        .userName(principal.getUsername())
                        .role(principal.getAuthorities().toArray()[0].toString())
                        .enabled(true)
                        .build())
                .orElseThrow(InvalidAuthenticationType::new);
    }

    @Override
    public Optional<User> findByUserName(String username) {
        return userRepository.findByUserName(username);
    }

    @Override
    public UserDto getUserDto(UUID heartbeatId) {
        return userToHeartbeatId.get(heartbeatId);
    }

    @Override
    public void mapUserDtoToHeartbeatId(UUID heartbeatId, Authentication auth) {
        userToHeartbeatId.put(heartbeatId, toUserDto(auth));
    }

    @Override
    public void removeUserDto(UUID heartbeatId) {
        userToHeartbeatId.remove(heartbeatId);
    }

    private UserDto toUserDto(Authentication authentication) {
        return Optional.of(authentication)
                .map(Authentication::getPrincipal)
                .map(principal -> (UserDetails) principal)
                .map(principal -> UserDto.builder()
                        .userName(principal.getUsername())
                        .role(principal.getAuthorities().toArray()[0].toString())
                        .enabled(true)
                        .build())
                .orElseThrow(InvalidAuthenticationType::new);
    }
}
