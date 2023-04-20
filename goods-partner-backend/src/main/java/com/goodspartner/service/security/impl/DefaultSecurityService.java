package com.goodspartner.service.security.impl;

import com.goodspartner.service.security.JwtService;
import com.goodspartner.service.security.SecurityService;
import com.goodspartner.web.controller.request.AuthRequest;
import com.goodspartner.web.controller.response.AuthResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class DefaultSecurityService implements SecurityService {

    private final AuthenticationManager authManager;
    private final JwtService jwtService;
    private final UserDetailsService userService;
    private final Map<String, String> tokenRepository = new ConcurrentHashMap<>();

    @Override
    public AuthResponse authenticate(AuthRequest authRequest) {
        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getLogin(), authRequest.getPassword()));
        UserDetails user = (UserDetails) auth.getPrincipal();

        String accessToken = jwtService.createAccessToken(user);
        String refreshToken = jwtService.createRefreshToken(user);
        tokenRepository.put(user.getUsername(), refreshToken);
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .user(AuthResponse.User.builder()
                        .username(user.getUsername())
                        .role(user.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList().get(0))
                        .enabled(user.isEnabled())
                        .heartbeatId(UUID.randomUUID())
                        .build()
                )
                .build();
    }

    @Override
    public Optional<AuthResponse> getAccessToken(String jwtRefresh) {
        Optional<UserDetails> user = getAuthenticatedUserFromRefreshToken(jwtRefresh);

        if (user.isPresent()) {
            String accessToken = jwtService.createAccessToken(user.get());
            return Optional.of(AuthResponse.builder()
                    .accessToken(accessToken)
                    .build());
        }
        return Optional.empty();
    }

    @Override
    public Optional<AuthResponse> getRefreshToken(String refreshToken) {
        Optional<UserDetails> user = getAuthenticatedUserFromRefreshToken(refreshToken);
        if (user.isPresent()) {
            String accessToken = jwtService.createAccessToken(user.get());
            String newRefreshToken = jwtService.createRefreshToken(user.get());
            tokenRepository.put(user.get().getUsername(), newRefreshToken);
            return Optional.of(AuthResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(newRefreshToken)
                    .build());
        }
        return Optional.empty();
    }

    private Optional<UserDetails> getAuthenticatedUserFromRefreshToken(String jwtToken) {
        if (jwtService.validateRefreshToken(jwtToken)) {
            String login = jwtService.getUserLoginFromJwtRefresh(jwtToken);
            String saveRefreshToken = tokenRepository.get(login);
            if (saveRefreshToken != null && saveRefreshToken.equals(jwtToken)) {
                return Optional.of(userService.loadUserByUsername(login));
            }
        }
        return Optional.empty();
    }

}
