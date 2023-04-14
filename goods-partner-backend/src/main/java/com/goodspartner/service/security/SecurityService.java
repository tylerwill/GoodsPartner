package com.goodspartner.service.security;

import com.goodspartner.web.controller.request.AuthRequest;
import com.goodspartner.web.controller.response.AuthResponse;
import com.goodspartner.web.controller.response.JwtRefreshResponse;

import java.util.Optional;

public interface SecurityService {
    AuthResponse authenticate(AuthRequest authRequest);

    Optional<AuthResponse> getAccessToken(String refreshToken);

    Optional<AuthResponse> getRefreshToken(String refreshToken);

}
