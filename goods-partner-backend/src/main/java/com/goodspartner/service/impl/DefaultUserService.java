package com.goodspartner.service.impl;

import com.goodspartner.dto.UserDto;
import com.goodspartner.entity.User;
import com.goodspartner.exception.UserNotFoundException;
import com.goodspartner.mapper.UserMapper;
import com.goodspartner.repository.UserRepository;
import com.goodspartner.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@AllArgsConstructor
@Service
public class DefaultUserService implements UserService {

    private static final String EMAIL_ATTRIBUTE = "email";

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDto findByAuthentication(OAuth2AuthenticationToken authentication) {
        OAuth2User principal = authentication.getPrincipal();
        String email = principal.getAttribute(EMAIL_ATTRIBUTE);
        return userRepository.findUserByEmail(email)
                .map(userMapper::map)
                .orElseThrow(() -> new UserNotFoundException(email));
    }

    @Override
    public List<UserDto> findAll() {
        return userRepository.findAll().stream()
                .map(userMapper::map)
                .toList();
    }

    @Override
    public UserDto findById(int id) {
        return userRepository.findById(id)
                .map(userMapper::map)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    @Transactional
    @Override
    public UserDto update(int id, UserDto userDto) {
        return userRepository.findById(id)
                .map(user -> userMapper.update(user, userDto))
                .map(userMapper::map)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    @Transactional
    @Override
    public UserDto add(UserDto userDto) {
        User user = userMapper.map(userDto);
        User savedUser = userRepository.save(user);
        return userMapper.map(savedUser);
    }

    @Override
    public UserDto delete(int id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        userRepository.delete(user);
        return userMapper.map(user);
    }
}
