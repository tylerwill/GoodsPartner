package com.goodspartner.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.SecurityFilterChain;

@TestConfiguration
@EnableWebSecurity
//@EnableGlobalMethodSecurity(prePostEnabled = true)
public class TestSecurityConfig {

    @Bean
    public SecurityFilterChain filterTest(HttpSecurity http) throws Exception {
        // Disable CSRF
        http
//                .csrf().disable()
                // Permit all requests without authentication
                .authorizeRequests().anyRequest().permitAll();

        DefaultSecurityFilterChain build = http.build();
        return build;
    }

}
