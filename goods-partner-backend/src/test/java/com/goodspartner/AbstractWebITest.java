package com.goodspartner;

import com.graphhopper.GraphHopper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.test.web.servlet.MockMvc;

import static com.goodspartner.entity.User.UserRole.DRIVER;
import static com.goodspartner.entity.User.UserRole.LOGISTICIAN;

@AutoConfigureMockMvc
public class AbstractWebITest extends AbstractBaseITest {

    private static final String DEFAULT_DRIVER_NAME = "Test Driver";
    private static final String DEFAULT_DRIVER_EMAIL = "test-driver@gmail.com";
    private static final String DRIVER_ROLE = DRIVER.name();

    private static final String DEFAULT_LOGIST_NAME = "Test Logist";
    private static final String DEFAULT_LOGIST_EMAIL = "test-logist@gmail.com";
    private static final String LOGIST_ROLE = LOGISTICIAN.name();

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

    protected MockHttpSession getMockSession(String username, String email, String role) {
        MockHttpSession session = new MockHttpSession();
        /*session.setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                getSecurityContext(username, email, role));*/
        return session;
    }

}
