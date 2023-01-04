package com.goodspartner;

import com.goodspartner.service.dto.GoodsPartnerOAuth2User;
import com.graphhopper.GraphHopper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static com.goodspartner.entity.User.UserRole.DRIVER;
import static com.goodspartner.entity.User.UserRole.LOGISTICIAN;

@AutoConfigureMockMvc
public class AbstractWebITest extends AbstractBaseITest {

    private static final String DEFAULT_DRIVER_NAME = "Test Driver";
    private static final String DEFAULT_DRIVER_EMAIL = "test-driver@gmail.com";
    private static final String DRIVER_ROLE = DRIVER.getName();

    private static final String DEFAULT_LOGIST_NAME = "Test Logist";
    private static final String DEFAULT_LOGIST_EMAIL = "test-logist@gmail.com";
    private static final String LOGIST_ROLE = LOGISTICIAN.getName();

    @Autowired
    protected MockMvc mockMvc;

    @MockBean
    protected GraphHopper hopper;

    protected MockHttpSession getDriverSession() {
        return getMockSession(DEFAULT_DRIVER_NAME, DEFAULT_DRIVER_EMAIL, DRIVER_ROLE);
    }

    protected MockHttpSession getLogistSession() {
        return getMockSession(DEFAULT_LOGIST_NAME, DEFAULT_LOGIST_EMAIL, LOGIST_ROLE);
    }

    private MockHttpSession getMockSession(String username, String email, String role) {
        OAuth2AuthenticationToken principal = buildPrincipal(username, email, role);
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                new SecurityContextImpl(principal));
        return session;
    }

    private OAuth2AuthenticationToken buildPrincipal(String username, String email, String role) {

        List<GrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + role));

        return new OAuth2AuthenticationToken(GoodsPartnerOAuth2User.builder()
                .username(username)
                .email(email)
                .authorities(List.of(new SimpleGrantedAuthority("ROLE_" + role)))
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .enabled(true)
                .build(),
                authorities,
                "google");
    }

}
