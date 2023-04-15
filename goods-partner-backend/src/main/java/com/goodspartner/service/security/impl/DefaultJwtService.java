package com.goodspartner.service.security.impl;


import com.goodspartner.entity.User;
import com.goodspartner.service.security.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
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

    private final SecretKey jwtAccessSecret;
    private final UserDetailsService userDetailsService;

    public DefaultJwtService(@Value("${goodspartner.security.jwt.access-token.secret}") String jwtAccessSecret,
                             UserDetailsService userDetailsService) {
        this.jwtAccessSecret = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtAccessSecret));
        this.userDetailsService = userDetailsService;
    }

    public UsernamePasswordAuthenticationToken getAuthentication(String jwtAccess) {
        validateToken(jwtAccess, jwtAccessSecret);
        String username = extractUsername(jwtAccess, jwtAccessSecret);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        List<SimpleGrantedAuthority> role = extractUserRole(jwtAccess, jwtAccessSecret);

        return new UsernamePasswordAuthenticationToken(userDetails, "", role);
    }

    public String createAccessToken(User user) {
        return Jwts.builder()
                .setSubject(user.getUserName())
                .claim(ROLES_CLAIM, "ROLE_" + user.getRole())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtAccessExpires))
                .signWith(jwtAccessSecret)
                .compact();
    }

    public String createRefreshToken(String username, String role) {
        return Jwts.builder()
                .setSubject(username)
                .claim(ROLES_CLAIM, role)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtAccessExpires))
                .signWith(jwtAccessSecret)
                .compact();
    }

    private boolean validateToken(String token, Key secret) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secret)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException expEx) {
            log.error("Token expired", expEx);
        } catch (Exception exception) {
            log.error("Jwt token - ", exception);
//            throw new JwtException(exception.getMessage());
        }
        return false;
    }

    private Claims extractAllClaims(String token, Key secret) {
        return Jwts.parserBuilder()
                .setSigningKey(secret)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver, Key secret) {
        Claims claims = extractAllClaims(token, secret);
        return claimsResolver.apply(claims);
    }

    private String extractUsername(String token, Key secret) {
        return extractClaim(token, Claims::getSubject, secret);
    }

    private List<SimpleGrantedAuthority> extractUserRole(String jwtAccess, Key secret) {
        String role = extractClaim(jwtAccess, key -> key.get(ROLES_CLAIM, String.class), secret);
        return List.of(new SimpleGrantedAuthority(role));
    }
}
