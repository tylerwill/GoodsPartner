package com.goodspartner.web.config;

import org.junit.jupiter.api.Order;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.ActiveProfiles;

@TestConfiguration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
//@ActiveProfiles(profiles = "test")
public class TestSecurityConfig {

    @Bean
    public SecurityFilterChain filterTest(HttpSecurity http) throws Exception {
        // Disable CSRF
        http
//                .csrf().disable()
                // Permit all requests without authentication
                .authorizeRequests().anyRequest().permitAll();

        return http.build();
    }

}
