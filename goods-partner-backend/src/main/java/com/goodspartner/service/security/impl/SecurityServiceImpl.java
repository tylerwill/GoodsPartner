package com.goodspartner.service.security.impl;

import com.goodspartner.entity.User;
import com.goodspartner.service.dto.AuthenticationRequest;
import com.goodspartner.service.dto.AuthenticationResponse;
import com.goodspartner.service.security.SecurityService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class SecurityServiceImpl implements SecurityService {

    private final AuthenticationManager authManager;
    private final JwtServiceImpl jwtService;
    private final UserDetailsService userService;
    private final Map<String, String> tokenRepository = new ConcurrentHashMap<>();

    @Override
    public AuthenticationResponse authenticate(AuthenticationRequest authRequest) {
        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword()));

        User user = (User) auth.getPrincipal();
        String accessToken = jwtService.createAccessToken(user);
        String refreshToken = jwtService.createRefreshToken(user);
        tokenRepository.put(user.getEmail(), refreshToken);
        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .username(user.getUsername())
                .email(user.getEmail())
                .build();
    }

    @Override
    public Optional<AuthenticationResponse> getAccessToken(String jwtRefresh) {
        Optional<User> user = getAuthenticatedUser(jwtRefresh);
        if (user.isPresent()) {
            String accessToken = jwtService.createAccessToken(user.get());
            return Optional.of(AuthenticationResponse.builder()
                    .accessToken(accessToken)
                    .build());
        }
        return Optional.empty();
    }

    @Override
    public Optional<AuthenticationResponse> getRefreshToken(String refreshToken) {
        Optional<User> user = getAuthenticatedUser(refreshToken);
        if (user.isPresent()) {
            String accessToken = jwtService.createAccessToken(user.get());
            String newRefreshToken = jwtService.createRefreshToken(user.get());
            tokenRepository.put(user.get().getEmail(), newRefreshToken);
            return Optional.of(AuthenticationResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(newRefreshToken)
                    .email(user.get().getEmail())
                    .username(user.get().getUsername())
                    .build());
        }
        return Optional.empty();
    }

    private Optional<User> getAuthenticatedUser(String jwtToken) {
        if (jwtService.validateRefreshToken(jwtToken)) {
            String login = jwtService.getUserLogin(jwtToken);
            String saveRefreshToken = tokenRepository.get(login);
            if (saveRefreshToken != null && saveRefreshToken.equals(jwtToken)) {
                return Optional.of((User) userService.loadUserByUsername(login));
            }
        }
        return Optional.empty();
    }
}


