package com.goodspartner.service.security;

import lombok.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;

public interface JwtService {
    boolean validateAccessToken(@NonNull String accessToken);

    boolean validateRefreshToken(@NonNull String refreshToken);

    UsernamePasswordAuthenticationToken getAuthenticationFromJwtAccessTocken(String jwtAccess);

    String createAccessToken(UserDetails user);

    String createRefreshToken(UserDetails user);

    String getUserLoginFromJwtRefresh(String refreshToken);
}
