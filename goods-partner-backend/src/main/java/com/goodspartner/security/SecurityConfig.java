package com.goodspartner.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filter(HttpSecurity http) throws Exception {

        http
                .authorizeRequests().anyRequest().authenticated()

//                If we have login page then we accept -> pertmitAll
//                .and()
//                .authorizeRequests().antMatchers("**/api/v1/login").permitAll()

                .and().oauth2Login();

        return http.build();
    }
}
