package com.goodspartner.service.security;

import com.goodspartner.service.dto.AuthenticationRequest;
import com.goodspartner.service.dto.AuthenticationResponse;

import java.util.Optional;

public interface SecurityService {
    AuthenticationResponse authenticate(AuthenticationRequest authRequest);

    Optional<AuthenticationResponse> getAccessToken(String refreshToken);

    Optional<AuthenticationResponse> getRefreshToken(String refreshToken);

}
