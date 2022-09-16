package com.goodspartner.web.controller;

import com.github.database.rider.spring.api.DBRider;
import com.goodspartner.AbstractWebITest;
import com.goodspartner.config.TestSecurityEnableConfig;
import com.goodspartner.service.RouteService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import({TestSecurityEnableConfig.class})
@AutoConfigureMockMvc
public class RouteControllerSecurityITest extends AbstractWebITest {

    public static final String URL_TEMPLATE = "/api/v1/routes/calculate";
    public static final String URL_PARAM_MANE = "date";

    @Autowired
    protected MockMvc mockMvc;

    @MockBean
    private RouteService routeService;

    @Test
    @DisplayName("given date with orders when Calculate Routers After Auth then Ok Status Returned")
    @WithMockUser(username = "mary", roles = "ADMIN")
    void givenDateWithOrdersWhenCalculateRoutersAfterAuthThenOkStatusReturned() throws Exception {

        mockMvc.perform(get(URL_TEMPLATE)
                        .param(URL_PARAM_MANE, "2022-08-07")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("given date with orders when Calculate Routers Without Auth then Redirected Status Returned")
    void givenDateWithOrdersWhenCalculateRoutersWithoutAuthThenRedirectedStatusReturned() throws Exception {

        mockMvc.perform(get(URL_TEMPLATE)
                        .param(URL_PARAM_MANE, "2022-08-07")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @DisplayName("given date with orders when Calculate Routers Without Rights Then Forbidden Status Returned")
    @WithMockUser(username = "mary", roles = "NORIGHTS")
    void givenDateWithOrdersWhenCalculateRoutersWithoutRightsThenForbiddenStatusReturned() throws Exception {

        mockMvc.perform(get(URL_TEMPLATE)
                        .param(URL_PARAM_MANE, "2022-08-07")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }
}
