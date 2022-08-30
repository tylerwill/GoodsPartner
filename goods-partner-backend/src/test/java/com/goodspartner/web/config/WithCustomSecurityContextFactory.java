package com.goodspartner.web.config;

import com.goodspartner.security.CustomOAuth2User;
import com.goodspartner.security.CustomOAuth2UserService;
import com.goodspartner.security.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.util.List;

@AllArgsConstructor
public class WithCustomSecurityContextFactory implements WithSecurityContextFactory<WithTestUser> {

    private final UserRepository userRepository;

    @Override
    public SecurityContext createSecurityContext(WithTestUser annotation) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        Authentication authentication =
                new OAuth2AuthenticationToken(new CustomOAuth2User(),
                        List.of(new SimpleGrantedAuthority(annotation.authority())), "");

        context.setAuthentication(authentication);


        return context;
    }

    CustomOAuth2UserService customOAuth2UserService;


}
