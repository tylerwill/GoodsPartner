package com.goodspartner.service.dto;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class CustomOAuth2User implements OAuth2User {

    private String username;
    private String email;
    private List<? extends GrantedAuthority> authorities;
    private boolean accountNonExpired;
    private boolean accountNonLocked;
    private boolean credentialsNonExpired;
    private boolean enabled;

    @Override
    public Map<String, Object> getAttributes() {
        Map<String, Object> attributes = new HashMap<>();

        attributes.put("username", username);
        attributes.put("email", email);
        attributes.put("authorities", authorities);
        attributes.put("accountNonExpired", accountNonExpired);
        attributes.put("accountNonLocked", accountNonLocked);
        attributes.put("credentialsNonExpired", credentialsNonExpired);
        attributes.put("enabled", enabled);

        return attributes;
    }

    @Override
    public String getName() {
        return username;
    }
}
