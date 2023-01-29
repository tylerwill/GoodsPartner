package com.goodspartner.util;

import com.goodspartner.service.dto.GoodsPartnerOAuth2User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AuditorBuilder {

    // Keys
    private static final String ROLE_TRANSLATED_KEY = "roleTranslated";
    private static final String ROLE_KEY = "role";
    private static final String USER_NAME_KEY = "userName";
    private static final String USER_EMAIL_KEY = "userEmail";

    // Anonymous
    private static final String ROLE_ANONYMOUS = "ROLE_ANONYMOUS";
    private static final String USER_EMAIL_ANONYMOUS = "anonymous@mail";
    private static final String USER_NAME_ANONYMOUS = "Anonymous";

    // TODO move to localisation
    private static final Map<String, String> ROLES_TRANSLATION = Map.ofEntries(
            Map.entry("ROLE_ADMIN", "Адміністратор"),
            Map.entry("ROLE_DRIVER", "Водій"),
            Map.entry("ROLE_LOGISTICIAN", "Логіст"),
            Map.entry("ROLE_ANONYMOUS", "Анонім")
    );

    public static Map<String, String> getCurrentAuditorData() {
        Map<String, String> values = new HashMap<>();

        Authentication authentication = getAuthentication();
        if (Objects.isNull(authentication)) {
            values.put(ROLE_KEY, ROLE_ANONYMOUS);
            values.put(ROLE_TRANSLATED_KEY, ROLES_TRANSLATION.get(ROLE_ANONYMOUS));
            values.put(USER_EMAIL_KEY, USER_EMAIL_ANONYMOUS);
            values.put(USER_NAME_KEY, USER_NAME_ANONYMOUS);
            return values;
        }

        if (authentication.getPrincipal().getClass().equals(GoodsPartnerOAuth2User.class)) {
            GoodsPartnerOAuth2User user = (GoodsPartnerOAuth2User) authentication.getPrincipal();

            String role = user.getAuthorities().get(0).toString(); // We always set only 1 role
            values.put(ROLE_KEY, role);
            values.put(ROLE_TRANSLATED_KEY, ROLES_TRANSLATION.get(role));

            String userEmail = user.getEmail();
            values.put(USER_EMAIL_KEY, userEmail);

            String userName = user.getUsername();
            values.put(USER_NAME_KEY, userName);

        } else {
            values.put(ROLE_KEY, authentication.getAuthorities().toString());
            values.put(ROLE_TRANSLATED_KEY, authentication.getAuthorities().toString());
            values.put(USER_EMAIL_KEY, authentication.getPrincipal().toString());
            values.put(USER_NAME_KEY, authentication.getPrincipal().toString());
        }
        return values;
    }

    public static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }
}
