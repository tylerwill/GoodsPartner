package com.goodspartner.configuration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Profile("!test")
@ConditionalOnProperty(prefix = "goodspartner.security", name = "enabled", havingValue = "true", matchIfMissing = true)
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filter(HttpSecurity http) throws Exception {
        // FIXME: Disable CORS only in dev mode
        http
                .csrf().disable()
                .cors().disable()
                .authorizeRequests().anyRequest().authenticated()
                .and().oauth2Login();

        return http.build();
    }
}