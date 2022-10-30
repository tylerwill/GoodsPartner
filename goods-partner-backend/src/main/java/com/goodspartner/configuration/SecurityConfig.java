package com.goodspartner.configuration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.filter.CharacterEncodingFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Profile("prod")
@ConditionalOnProperty(prefix = "goodspartner.security", name = "enabled", havingValue = "true", matchIfMissing = true)
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filter(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .cors().disable()
//                .addFilter(characterEncodingFilter())
                .authorizeRequests().anyRequest().authenticated()
                .and()
                .oauth2Login();

        return http.build();
    }

    /*@Bean
    public CharacterEncodingFilter characterEncodingFilter() {
        CharacterEncodingFilter characterEncodingFilter = new CharacterEncodingFilter();
        characterEncodingFilter.setForceEncoding(true);
        characterEncodingFilter.setEncoding("UTF-8");

        return characterEncodingFilter;
    }*/
}