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
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Value("${goodspartner.security.enabled:true}")
    private boolean securityEnabled;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain filter(HttpSecurity http) throws Exception {
        if (securityEnabled) {
            http
                    .csrf().disable()
                    .cors().configurationSource(corsConfigurationSource())
                    .and()
                    .authorizeRequests(auth -> auth
                            .antMatchers(POST,"/api/v1/auth/login").permitAll()
                            .antMatchers(POST,"/api/v1/auth/token").permitAll()
                            .antMatchers(POST,"/api/v1/auth/refresh").permitAll()
                            .antMatchers(GET, "/api/v1/live-event/**").permitAll()
                            .antMatchers( "/api/v1/**").authenticated()
                            .antMatchers(GET, "/**").permitAll()
                            .anyRequest().authenticated()
                    )
                    .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                    .authenticationProvider(authenticationProvider)
                    .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
            return http.build();
        } else {
            return http
                    .csrf().disable()
                    .cors().configurationSource(corsConfigurationSource())
                    .and()
                    .authorizeRequests(auth -> auth.anyRequest().permitAll())
                    .build();

        }
    }

    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:3000"));
        configuration.setAllowedMethods(List.of("GET", "POST", "DELETE", "PUT"));
        configuration.addAllowedHeader("*");
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @ConditionalOnProperty(prefix = "goodspartner.security", name = "enabled", havingValue = "true", matchIfMissing = true)
    @EnableGlobalMethodSecurity(prePostEnabled = true)
    static class SecureServices {
    }
}