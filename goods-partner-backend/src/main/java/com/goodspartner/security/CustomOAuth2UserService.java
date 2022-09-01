package com.goodspartner.security;

import lombok.AllArgsConstructor;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public CustomOAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);
        String email = (String) oAuth2User.getAttributes().get("email");

        User user = userRepository.findUserByEmail(email)
                .orElseThrow(()->new UsernameNotFoundException("This email is not found in database"));

        if(!user.isEnabled()){
            throw new DisabledException("User is disabled");
        }

        CustomOAuth2User customOAuth2User = new CustomOAuth2User();
        customOAuth2User.setUsername(user.getUserName());
        customOAuth2User.setEmail(user.getEmail());
        customOAuth2User.setAuthorities(List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole())));
        customOAuth2User.setAccountNonExpired(true);
        customOAuth2User.setAccountNonLocked(true);
        customOAuth2User.setCredentialsNonExpired(true);
        customOAuth2User.setEnabled(user.isEnabled());

        return customOAuth2User;
    }
}



