//package com.goodspartner.web.config;
//
//
//import com.goodspartner.security.CustomOAuth2User;
//import org.junit.jupiter.api.Order;
//import org.springframework.boot.test.context.TestConfiguration;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
//import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
//import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
//import org.springframework.test.context.ActiveProfiles;
//
//import java.util.List;
//
//@TestConfiguration
//@Order(1)
////@ActiveProfiles(profiles = "test")
//public class TestOAuth2UserService extends DefaultOAuth2UserService {
//
//    @Override
//    public CustomOAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
//
//        CustomOAuth2User customOAuth2User = new CustomOAuth2User();
//        customOAuth2User.setUsername("testUser");
//        customOAuth2User.setEmail("testEmail");
//        customOAuth2User.setAuthorities(List.of(new SimpleGrantedAuthority("ROLE_" + "ADMIN")));
//        customOAuth2User.setAccountNonExpired(true);
//        customOAuth2User.setAccountNonLocked(true);
//        customOAuth2User.setCredentialsNonExpired(true);
//        customOAuth2User.setEnabled(true);
//
//        return customOAuth2User;
//    }
//}
