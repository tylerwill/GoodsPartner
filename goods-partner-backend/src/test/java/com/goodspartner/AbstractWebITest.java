package com.goodspartner;

import com.goodspartner.configuration.security.JwtAuthenticationFilter;
import com.goodspartner.service.security.JwtService;
import com.goodspartner.service.security.SecurityUser;
import com.graphhopper.GraphHopper;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.List;

import static com.goodspartner.entity.User.UserRole.DRIVER;
import static com.goodspartner.entity.User.UserRole.LOGISTICIAN;

@AutoConfigureMockMvc
public class AbstractWebITest extends AbstractBaseITest {
    protected static final String DEFAULT_DRIVER_NAME = "Test Driver";
    protected static final String DEFAULT_DRIVER_LOGIN = "driver-login";
    protected static final String ANOTHER_DRIVER_LOGIN = "another-driver-login";
    protected static final String DEFAULT_DRIVER_EMAIL = "test-driver@gmail.com";
    protected static final String DRIVER_ROLE = DRIVER.name();

    protected static final String DEFAULT_LOGIST_NAME = "Test Logist";
    protected static final String DEFAULT_LOGIST_LOGIN = "logist-login";
    protected static final String DEFAULT_LOGIST_EMAIL = "test-logist@gmail.com";
    protected static final String LOGIST_ROLE = LOGISTICIAN.name();

    @Autowired
    protected MockMvc mockMvc;

    @MockBean
    protected GraphHopper hopper;

    @MockBean
    private JwtService jwtService;

    protected UsernamePasswordAuthenticationToken buildPrincipal(String username, String role) {
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role));
        String password = "pass";
        UserDetails principal = new SecurityUser(username, password, authorities, true);
        return new UsernamePasswordAuthenticationToken(principal, password, authorities);
    }

    protected MockHttpServletRequestBuilder asDriver(MockHttpServletRequestBuilder builder) {
        String driverToken = "driver";
        Mockito.when(jwtService.getAuthenticationFromJwtAccessTocken(driverToken))
                .thenReturn(buildPrincipal(DEFAULT_DRIVER_LOGIN, DRIVER_ROLE));
        builder.header(HttpHeaders.AUTHORIZATION, JwtAuthenticationFilter.AUTH_SCHEME + driverToken);
        return builder;
    }

    protected MockHttpServletRequestBuilder asAnotherDriver(MockHttpServletRequestBuilder builder) {
        String anotherDriverToken = "another-driver";
        Mockito.when(jwtService.getAuthenticationFromJwtAccessTocken(anotherDriverToken))
                .thenReturn(buildPrincipal(ANOTHER_DRIVER_LOGIN, DRIVER_ROLE));
        builder.header(HttpHeaders.AUTHORIZATION, JwtAuthenticationFilter.AUTH_SCHEME + anotherDriverToken);
        return builder;
    }

    protected MockHttpServletRequestBuilder asLogist(MockHttpServletRequestBuilder builder) {
        String logistToken = "logist";
        Mockito.when(jwtService.getAuthenticationFromJwtAccessTocken(logistToken))
                .thenReturn(buildPrincipal(DEFAULT_LOGIST_LOGIN, LOGIST_ROLE));
        builder.header(HttpHeaders.AUTHORIZATION, JwtAuthenticationFilter.AUTH_SCHEME + logistToken);
        return builder;
    }
}
