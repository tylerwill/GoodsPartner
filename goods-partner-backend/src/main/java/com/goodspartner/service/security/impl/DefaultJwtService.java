package com.goodspartner.service.security.impl;


import com.goodspartner.entity.User;
import com.goodspartner.service.security.JwtService;
import com.goodspartner.service.security.SecurityUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

@Service
@Slf4j
public class DefaultJwtService implements JwtService {
    private static final String ROLES_CLAIM = "role";

    @Value("${goodspartner.security.jwt.access-token.expiration}")
    private long jwtAccessExpires;
    @Value("${goodspartner.security.jwt.refresh-token.expiration}")
    private long jwtRefreshExpires;
    private final SecretKey jwtAccessSecret;
    private final SecretKey jwtRefreshSecret;
    private final UserDetailsService userDetailsService;

    public DefaultJwtService(
            @Value("${goodspartner.security.jwt.access-token.secret}")
            String jwtAccessSecret,
            @Value("${goodspartner.security.jwt.refresh-token.secret}")
            String jwtRefreshSecret,
            UserDetailsService userDetailsService) {
        this.jwtAccessSecret = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtAccessSecret));
        this.jwtRefreshSecret = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtRefreshSecret));
        this.userDetailsService = userDetailsService;
    }

    @Override
    public boolean validateAccessToken(@NonNull String accessToken) {
        return validateToken(accessToken, jwtAccessSecret);
    }

    @Override
    public boolean validateRefreshToken(@NonNull String refreshToken) {
        return validateToken(refreshToken, jwtRefreshSecret);
    }

    public String createAccessToken(UserDetails user) {
        return Jwts.builder()
                .setSubject(user.getUsername())
                .claim(ROLES_CLAIM, User.UserRole.valueOf(user.getAuthorities()
                        .stream().toList().get(0).getAuthority()))
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtAccessExpires))
                .signWith(jwtAccessSecret)
                .compact();
    }

    @Override
    public String createRefreshToken(@NonNull UserDetails user) {
        return Jwts.builder()
                .setSubject(user.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtRefreshExpires))
                .signWith(jwtRefreshSecret)
                .compact();
    }


    @Override
    public UsernamePasswordAuthenticationToken getAuthenticationFromJwtAccessTocken(String jwtAccess) {
        validateAccessToken(jwtAccess);
        String username = extractUsername(jwtAccess, jwtAccessSecret);
        List<SimpleGrantedAuthority> role = extractUserRole(jwtAccess, jwtAccessSecret);
        UserDetails userDetails = SecurityUser.builder().username(username).authorities(role).build();
        return new UsernamePasswordAuthenticationToken(userDetails, null, role);
    }

    @Override
    public String getUserLoginFromJwtRefresh(String refreshToken) {
        return extractUsername(refreshToken, jwtRefreshSecret);
    }

    // TODO Username -> Subj ???
    private String extractUsername(String token, Key secret) {
        return extractClaim(token, secret, Claims::getSubject);
    }

    private List<SimpleGrantedAuthority> extractUserRole(String jwtAccess, Key secret) {
        User.UserRole role = User.UserRole
                .valueOf(extractClaim(jwtAccess, secret, key -> key.get(ROLES_CLAIM, String.class)));
        return List.of(new SimpleGrantedAuthority(role.getName()));
    }

    private <T> T extractClaim(String token, Key secret, Function<Claims, T> clainmsResolver) {
        Claims claims = extractAllClaims(token, secret);
        return clainmsResolver.apply(claims);
    }

    /**
     * TODO where is time validation ???
     */
    private boolean validateToken(String token, Key secret) {
        try {
            extractAllClaims(token, secret);
            return true;
        } catch (Exception e) {
            log.warn("Invalid token {}", e.getMessage());
            return false;
        }
    }

    private Claims extractAllClaims(String token, Key secret) {
        return Jwts.parserBuilder()
                .setSigningKey(secret)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
