package com.goods.partner.web.controller;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.spring.api.DBRider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@DBRider
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class StoresEndpointIntegrationTest {

    @Container
    private static final PostgreSQLContainer<?> container = new PostgreSQLContainer<>("postgres:alpine")
            .withDatabaseName("test")
            .withUsername("test")
            .withPassword("test")
            .withReuse(true);

    @DynamicPropertySource
    public static void overrideProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", container::getJdbcUrl);
        registry.add("spring.datasource.username", container::getUsername);
        registry.add("spring.datasource.password", container::getPassword);
    }

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DataSet(value = "common/dataset.yml",
            disableConstraints = true)
    @DisplayName("Check Json returned after calculate stores")
    void givenStores_whenCalculateStores_thenJsonReturned() throws Exception {

        mockMvc.perform(get("/calculate/stores")
                        .param("date", "2022-07-12")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))

                .andExpect(status().isOk())
                .andExpect(content()
                        .json(
                                "{\"date\":\"2022-07-12\",\"stores\":" +
                                        "[{\"storeId\":1,\"storeName\":\"Склад №1\",\"orders\":" +
                                        "[{\"orderId\":6,\"orderNumber\":356325,\"totalOrderWeight\":59.32}]}]}"));
    }

    @Test
    @DataSet(value = "common/dataset.yml",
            disableConstraints = true)
    @DisplayName("Check empty Json returned after calculate stores")
    void givenNoStoresForSpecifiedDate_whenCalculateStores_thenJsonWithEmptyStoresFieldReturned() throws Exception {

        mockMvc.perform(get("/calculate/stores")
                        .param("date", "2002-07-12")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))

                .andExpect(status().isOk())
                .andExpect(content()
                        .json("{\"date\":\"2002-07-12\",\"stores\":[]}"));
    }
}
