package com.goodspartner.service.security;

import com.goodspartner.web.controller.request.AuthRequest;
import com.goodspartner.web.controller.response.AuthResponse;
import com.goodspartner.web.controller.response.JwtRefreshResponse;

public interface SecurityService {
    AuthResponse authenticate(AuthRequest authRequest);

    JwtRefreshResponse refresh(String token);
}
