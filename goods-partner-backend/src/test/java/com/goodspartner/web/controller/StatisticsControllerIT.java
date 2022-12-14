package com.goodspartner.web.controller;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.spring.api.DBRider;
import com.goodspartner.AbstractWebITest;
import com.goodspartner.config.TestSecurityEnableConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DBRider
@AutoConfigureMockMvc
@Import({TestSecurityEnableConfig.class})
class StatisticsControllerIT extends AbstractWebITest {

    public static final String MOCK_STATISTICS_PATH = "response/statistics/statistics.json";
    public static final String MOCK_NO_STATISTICS_PATH = "response/statistics/no-statistics.json";
    public static final String MOCK_CAR_STATISTICS_PATH = "response/statistics/car-statistics.json";
    public static final String MOCK_CAR_NO_STATISTICS_PATH = "response/statistics/no-car-statistics.json";
    public static final String MOCK_DAILY_CAR_STATISTICS_PATH = "response/statistics/daily-car-statistics.json";

    @Test
    @DataSet(value = "datasets/statistics/dataset.json")
    @DisplayName("getStatistics returns statistics for date range specified")
    void testGetStatistics() throws Exception {
        mockMvc.perform(get("/api/v1/statistics/deliveries")
                        .param("dateFrom", "2021-12-19")
                        .param("dateTo", "2021-12-20")
                        .session(getLogistSession())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(getResponseAsString(MOCK_STATISTICS_PATH)));
    }

    @Test
    @DataSet(value = "datasets/statistics/dataset.json")
    @DisplayName("getStatistics returns not found status if there are no deliveries for date range specified")
    void testGetStatisticsReturnsNotFoundStatus() throws Exception {
        mockMvc.perform(get("/api/v1/statistics/deliveries")
                        .param("dateFrom", "2021-12-27")
                        .param("dateTo", "2021-12-29")
                        .session(getLogistSession())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(getResponseAsString(MOCK_NO_STATISTICS_PATH)));
    }

    @Test
    @DataSet(value = "datasets/statistics/dataset.json")
    @DisplayName("getCarStatistics returns car statistics for date range specified")
    void testGetCarStatistics() throws Exception {
        mockMvc.perform(get("/api/v1/statistics/cars/151")
                        .param("dateFrom", "2021-12-19")
                        .param("dateTo", "2021-12-20")
                        .session(getLogistSession())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(getResponseAsString(MOCK_CAR_STATISTICS_PATH)));
    }

    @Test
    @DataSet(value = "datasets/statistics/dataset.json")
    @DisplayName("getCarStatistics returns not found status for non-existing car")
    void testGetCarStatisticsReturnsNotFoundStatusForNonExistingCar() throws Exception {
        mockMvc.perform(get("/api/v1/statistics/cars/154")
                        .param("dateFrom", "2021-12-19")
                        .param("dateTo", "2021-12-20")
                        .session(getLogistSession())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DataSet(value = "datasets/statistics/dataset.json")
    @DisplayName("getCarStatistics returns not found status if there are no deliveries for date range specified")
    void testGetCarStatisticsReturnsNotFoundStatusIfThereAreNoDeliveries() throws Exception {
        mockMvc.perform(get("/api/v1/statistics/cars/155")
                        .param("dateFrom", "2021-12-19")
                        .param("dateTo", "2021-12-20")
                        .session(getLogistSession())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(getResponseAsString(MOCK_CAR_NO_STATISTICS_PATH)));
    }

    @Test
    @DataSet(value = "datasets/statistics/dataset.json")
    void testGetDailyCarStatistics() throws Exception {
        mockMvc.perform(get("/api/v1/daily-statistics/cars/151")
                        .param("date", "2021-12-19")
                        .session(getLogistSession())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(getResponseAsString(MOCK_DAILY_CAR_STATISTICS_PATH)));
    }

    @Test
    @DataSet(value = "datasets/statistics/dataset.json")
    @DisplayName("getDailyCarStatistics returns not found status for non-existing car")
    void testGetDailyCarStatisticsReturnsNotFoundStatusForNonExistingCar() throws Exception {
        mockMvc.perform(get("/api/v1/daily-statistics/cars/101")
                        .param("date", "2021-12-19")
                        .session(getLogistSession())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DataSet(value = "datasets/statistics/dataset.json")
    @DisplayName("getDailyCarStatistics returns not found status if there are no deliveries for date specified")
    void testGetDailyCarStatisticsReturnsNotFoundStatusIfThereAreNoDeliveries() throws Exception {
        mockMvc.perform(get("/api/v1/daily-statistics/cars/151")
                        .param("date", "2021-12-17")
                        .session(getLogistSession())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}