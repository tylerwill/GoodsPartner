package com.goodspartner.util;

//import com.goodspartner.service.dto.GoodsPartnerOAuth2User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

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

        if (authentication.getPrincipal().getClass().equals(User.class)) {
            // TODO change user here to GP User
            User user = (User) authentication.getPrincipal();

            String role = user.getAuthorities()
                    .stream()
                    .findFirst()
                    .map(GrantedAuthority::getAuthority)
                    .orElse(ROLE_ANONYMOUS); // We always set only 1 role
            values.put(ROLE_KEY, role);
            values.put(ROLE_TRANSLATED_KEY, ROLES_TRANSLATION.get(role));


            String userName = user.getUsername();
            values.put(USER_NAME_KEY, userName);
            values.put(USER_EMAIL_KEY, userName); // TODO fix me to map with user Id

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
