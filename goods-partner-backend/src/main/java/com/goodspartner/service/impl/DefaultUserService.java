package com.goodspartner.service.impl;

import com.goodspartner.dto.UserDto;
import com.goodspartner.entity.User;
import com.goodspartner.exception.InvalidAuthenticationType;
import com.goodspartner.exception.UserNotFoundException;
import com.goodspartner.mapper.UserMapper;
import com.goodspartner.repository.UserRepository;
import com.goodspartner.service.UserService;
import com.goodspartner.service.dto.GoodsPartnerOAuth2User;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class DefaultUserService implements UserService {

    private static final String DEFAULT_SORT_FILED = "id";
    private static final String EMAIL_ATTRIBUTE = "email";
    private static final String USERNAME_ATTRIBUTE = "username";

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
        return userRepository.findByRouteId(routeId).orElseThrow(() -> new UserNotFoundException(routeId));
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
        String userEmail = getUserEmailFromAuthenticationContext();
        return userRepository.findUserByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException(userEmail));
    }

    private String getUserEmailFromAuthenticationContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getPrincipal() instanceof GoodsPartnerOAuth2User principal) {
            return principal.getAttribute(EMAIL_ATTRIBUTE);
        }
        throw new InvalidAuthenticationType();
    }

    @Override
    public UserDto getAuthenticatedUserDto() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .map(Authentication::getPrincipal)
                .filter(principal -> principal instanceof GoodsPartnerOAuth2User)
                .map(principal -> (GoodsPartnerOAuth2User) principal)
                .map(principal -> UserDto.builder()
                        .userName(principal.getAttribute(USERNAME_ATTRIBUTE))
                        .email(principal.getAttribute(EMAIL_ATTRIBUTE))
                        .role(principal.getAuthorities().toArray()[0].toString())
                        .enabled(true)
                        .build())
                .orElseThrow(InvalidAuthenticationType::new);
    }
}
