package com.goodspartner.web.controller;

import com.github.database.rider.spring.api.DBRider;
import com.goodspartner.AbstractWebITest;
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
//@SpringBootTest
//@AutoConfigureMockMvc
public class OrderControllerSecurityITest extends AbstractWebITest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Without authentication then expect to get a 401 Unauthorized.")
    void whenUserNotAuthenticatedThenStatusIsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/v1/orders")
                        .param("date", "2022-07-10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Without authorization then expect to get a 403 Forbidden.")
//    @WithMockUser(username = "mary", roles = "USER")
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
