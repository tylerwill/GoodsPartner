package com.goodspartner.service.security.impl;

import com.goodspartner.repository.UserRepository;
import com.goodspartner.service.security.SecurityService;
import com.goodspartner.web.controller.request.AuthRequest;
import com.goodspartner.web.controller.response.AuthResponse;
import com.goodspartner.web.controller.response.JwtRefreshResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DefaultSecurityService implements SecurityService {

    private final AuthenticationManager authManager;
    private final DefaultJwtService jwtService;
    private final UserRepository userRepository;

    @Override
    public AuthResponse authenticate(AuthRequest authRequest) {
        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));

        String username = User.class.cast(auth.getPrincipal()).getUsername();
        com.goodspartner.entity.User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new UsernameNotFoundException("Unknown user: " + username));

        String jwtAccess = jwtService.createAccessToken(user);
        return AuthResponse.builder()
                .accessToken(jwtAccess)
                .user(AuthResponse.User.builder()
                        .id(user.getId())
                        .username(user.getUserName())
                        .role(user.getRole().getName())
                        .enabled(user.isEnabled())
                        .heartbeatId(UUID.randomUUID())
                        .build()
                )
                .build();
    }

    @Override
    public JwtRefreshResponse refresh(String token) {
        return null;
    }

}
