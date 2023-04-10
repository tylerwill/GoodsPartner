package com.goodspartner.configuration.security;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.http.HttpMethod.POST;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Value("${goodspartner.security.enabled}")
    private boolean securityEnabled;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain filter(HttpSecurity http) throws Exception {
        if (securityEnabled) {
            http
                    .csrf(AbstractHttpConfigurer::disable)
                    .cors(AbstractHttpConfigurer::disable)
                    .authorizeRequests(auth -> auth
                            .antMatchers(POST,
                                    "/api/v1/auth/login",
                                    "/api/v1/auth/token",
                                    "/api/v1/auth/refresh"
                            ).permitAll()
                            .anyRequest().authenticated())
                    .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                    .authenticationProvider(authenticationProvider)
                    .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
            return http.build();
        } else {
            return http
                    .csrf(AbstractHttpConfigurer::disable)
                    .authorizeRequests(auth -> auth.anyRequest().permitAll())
                    .build();

        }
    }

    @ConditionalOnProperty(prefix = "goodspartner.security", name = "enabled", havingValue = "true", matchIfMissing = true)
    @EnableGlobalMethodSecurity(prePostEnabled = true)
    static class SecureServices {
    }
}