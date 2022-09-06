package com.goodspartner.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@TestConfiguration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class TestSecurityEnableConfig {

    @Bean
    public SecurityFilterChain filterTest(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                // Secure all requests with authentication
                .authorizeRequests().anyRequest().authenticated()
                .and().oauth2Login();

        return http.build();
    }
}
