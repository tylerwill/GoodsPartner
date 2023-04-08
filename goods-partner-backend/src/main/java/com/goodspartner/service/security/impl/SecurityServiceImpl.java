package com.goodspartner.service.security.impl;

import com.goodspartner.dto.AuthenticationRequestDto;
import com.goodspartner.dto.AuthenticationResponseDto;
import com.goodspartner.entity.User;
import com.goodspartner.service.security.SecurityService;
import com.goodspartner.service.security.impl.JwtServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SecurityServiceImpl implements SecurityService {

    private final AuthenticationManager authManager;
    private final JwtServiceImpl jwtService;

    @Override
    public AuthenticationResponseDto authenticate(AuthenticationRequestDto authRequest) {
        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword()));

        User user = (User) auth.getPrincipal();
        String jwtAcceess = jwtService.createAccessToken(user);
        String jwtRefresh = jwtService.createRefreshToken(user);
        return AuthenticationResponseDto.builder()
                .accessToken(jwtAcceess)
                .refreshToken(jwtRefresh)
                .username(user.getUsername())
                .email(user.getEmail())
                .build();
    }

}
