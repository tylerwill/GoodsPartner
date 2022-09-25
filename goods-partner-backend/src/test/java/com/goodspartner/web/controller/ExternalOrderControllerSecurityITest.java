package com.goodspartner.web.controller;

import com.goodspartner.AbstractWebITest;
import com.goodspartner.config.TestSecurityEnableConfig;
import com.goodspartner.service.OrderService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import({TestSecurityEnableConfig.class})
class ExternalOrderControllerSecurityITest extends AbstractWebITest {

    @MockBean
    private OrderService orderService;

    @Test
    @DisplayName("When try to reach endpoint without authentication then expect to get a 302 Redirect to google login.")
    void whenUserNotAuthenticatedThenStatusIsRedirection() throws Exception {
        mockMvc.perform(get("/api/v1/orders")
                .param("date", "2022-07-10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @DisplayName("Without authorization then expect to get a 403 Forbidden.")
    @WithMockUser(username = "mary", roles = "USER")
    void whenUserAuthenticatedButNotAuthorizedThenStatusIsForbidden() throws Exception {
        mockMvc.perform(get("/api/v1/orders")
                .param("date", "2022-07-10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("When authorized and authenticated then expect to get a 200 OK.")
    @WithMockUser(username = "mary", roles = "ADMIN")
    void whenUserAuthenticatedAndAuthorizedThenStatusIsOk() throws Exception {
        mockMvc.perform(get("/api/v1/orders")
                .param("date", "2022-07-10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
