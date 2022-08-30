package com.goodspartner.web.controller;

import com.github.database.rider.spring.api.DBRider;
import com.goodspartner.dto.OrderDto;
import com.goodspartner.dto.ProductDto;
import com.goodspartner.service.OrderService;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//@ActiveProfiles(profiles = "test")
@SpringBootTest
@AutoConfigureMockMvc
public class OrderControllerSecurityITest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("When calling the /orders endpoint without authentication then expect to get a 401 Unauthorized.")
    void whenUserNotAuthenticatedThenStatusIsUnauthorized() throws Exception {

        mockMvc.perform(get("/api/v1/orders")
                        .param("date", "2022-07-10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("When calling the /orders endpoint without authorization then expect to get a 403 Forbidden.")
    @WithMockUser
    void whenUserAuthenticatedButNotAuthorizedThenStatusIsForbidden() throws Exception {
        mockMvc.perform(get("/api/v1/orders")
                        .param("date", "2022-07-10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("When calling the /orders endpoint authorized and authenticated then expect to get a 200 OK.")
    @WithMockUser(username = "mary", roles = "ADMIN")
    void whenUserAuthenticatedAndAuthorizedThenStatusIsOk() throws Exception {
        mockMvc.perform(get("/api/v1/orders")
                        .param("date", "2022-07-10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void testAuthenticatedWithProperAuthDemoEndpoint() throws Exception {
        mockMvc.perform(get("/api/v1/orders")
                        .param("date", "2022-07-10")
                        .with(httpBasic("john", null))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

}
