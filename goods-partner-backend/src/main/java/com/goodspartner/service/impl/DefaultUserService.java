package com.goodspartner.service.impl;

import com.goodspartner.dto.UserDto;
import com.goodspartner.entity.User;
import com.goodspartner.exception.UserNotFoundException;
import com.goodspartner.mapper.UserMapper;
import com.goodspartner.repository.UserRepository;
import com.goodspartner.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@AllArgsConstructor
@Service
public class DefaultUserService implements UserService {

    private static final String DEFAULT_SORT_FILED = "id";
    private static final String EMAIL_ATTRIBUTE = "email";

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
        User user = userMapper.mapToDto(userDto);
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
    public User findByAuthentication(OAuth2AuthenticationToken authentication) {
        String userEmail = getUserEmail(authentication);
        return userRepository.findUserByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException(userEmail));
    }

    private String getUserEmail(OAuth2AuthenticationToken authentication) {
        OAuth2User principal = authentication.getPrincipal();
        return principal.getAttribute(EMAIL_ATTRIBUTE);
    }
}
