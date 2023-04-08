package com.goodspartner.service.security;

import com.goodspartner.entity.User;
import lombok.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

public interface JwtService {
    boolean validateAccessToken(@NonNull String accessToken);

    boolean validateRefreshToken(@NonNull String refreshToken);

    UsernamePasswordAuthenticationToken getAuthentication(String jwtAccess);

    String createAccessToken(User user);

    String createRefreshToken(@NonNull User user);

    String getUserLogin(String refreshToken);
}
