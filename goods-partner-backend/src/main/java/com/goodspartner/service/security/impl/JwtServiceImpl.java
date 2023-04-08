package com.goodspartner.service.security.impl;


import com.goodspartner.entity.User;
import com.goodspartner.service.security.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

@Service
@Slf4j
public class JwtServiceImpl implements JwtService {
    private static final String ROLES_CLAIM = "roles";
    private static final String USERNAME_CLAIM = "username";

    @Value("${goodspartner.security.jwt.access-token.expiration}")
    private long jwtAccessExpires;
    @Value("${goodspartner.security.jwt.refresh-token.expiration}")
    private long jwtRefreshExpires;
    private final SecretKey jwtAccessSecret;
    private final SecretKey jwtRefreshSecret;

    public JwtServiceImpl(
            @Value("${goodspartner.security.jwt.access-token.secret}")
            String jwtAccessSecret,
            @Value("${goodspartner.security.jwt.refresh-token.secret}")
            String jwtRefreshSecret) {
        this.jwtAccessSecret = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtAccessSecret));
        this.jwtRefreshSecret = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtRefreshSecret));
    }

    @Override
    public boolean validateAccessToken(@NonNull String accessToken) {
        return validateToken(accessToken, jwtAccessSecret);
    }

    @Override
    public boolean validateRefreshToken(@NonNull String refreshToken) {
        return validateToken(refreshToken, jwtRefreshSecret);
    }

    @Override
    public UsernamePasswordAuthenticationToken getAuthentication(String jwtAccess) {
        validateAccessToken(jwtAccess);
        String userEmail = extractUserEmail(jwtAccess, jwtAccessSecret);
        List<SimpleGrantedAuthority> role = extractUserRole(jwtAccess, jwtAccessSecret);
        return new UsernamePasswordAuthenticationToken(userEmail, null, role);
    }

    @Override
    public String getUserLogin(String refreshToken) {
        return extractUserEmail(refreshToken, jwtRefreshSecret);
    }

    @Override
    public String createAccessToken(User user) {
        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim(ROLES_CLAIM, user.getRole().getName())
                .claim(USERNAME_CLAIM, user.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtAccessExpires))
                .signWith(jwtAccessSecret)
                .compact();
    }

    @Override
    public String createRefreshToken(@NonNull User user) {
        return Jwts.builder()
                .setSubject(user.getEmail())
                .setExpiration(new Date(System.currentTimeMillis() + jwtRefreshExpires))
                .signWith(jwtRefreshSecret)
                .compact();
    }

    private Claims extractAllClaims(String token, Key secret) {
        return Jwts.parserBuilder()
                .setSigningKey(secret)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private <T> T extractClaim(String token, Key secret, Function<Claims, T> clainmsResolver) {
        Claims claims = extractAllClaims(token, secret);
        return clainmsResolver.apply(claims);
    }

    private String extractUserEmail(String token, Key secret) {
        return extractClaim(token, secret, Claims::getSubject);
    }

    private List<SimpleGrantedAuthority> extractUserRole(String jwtAccess, Key secret) {
        String role = extractClaim(jwtAccess, secret, key -> key.get(ROLES_CLAIM, String.class));
        return List.of(new SimpleGrantedAuthority(role));
    }

    private boolean validateToken(String token, Key secret) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secret)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            log.error("Invalid token", e);
            return false;
        }
    }
}
