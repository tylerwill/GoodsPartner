package com.goodspartner.util;

import com.goodspartner.service.dto.CustomOAuth2User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AuditorBuilder {

    public static Map<String, String> getCurrentAuditorData() {
        Map<String, String> values = new HashMap<>();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (Objects.isNull(authentication)) {
            values.put("role", "SECURITY");
            values.put("userEmail", "OFF");
            return values;
        }

        if (authentication.getPrincipal().getClass().equals(CustomOAuth2User.class)) {
            String role = ((CustomOAuth2User) authentication.getPrincipal()).getAuthorities().toString();
            String userEmail = ((CustomOAuth2User) authentication.getPrincipal()).getEmail();
            values.put("role", role);
            values.put("userEmail", userEmail);

        } else {
            values.put("role", authentication.getAuthorities().toString());
            values.put("userEmail", authentication.getPrincipal().toString());
        }
        return values;
    }
}
