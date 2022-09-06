package com.goodspartner.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.SecurityFilterChain;

@TestConfiguration
@EnableWebSecurity
public class TestSecurityDisableConfig {

    @Bean
    public SecurityFilterChain filterTest(HttpSecurity http) throws Exception {
        http
                // Permit all requests without authentication
                .authorizeRequests().anyRequest().permitAll();

        DefaultSecurityFilterChain build = http.build();
        return build;
    }

}
