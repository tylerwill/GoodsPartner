package com.goodspartner.web.controller;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.spring.api.DBRider;
import com.goodspartner.AbstractWebITest;
import com.goodspartner.config.TestSecurityDisableConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DBRider
@Import({TestSecurityDisableConfig.class})
@AutoConfigureMockMvc(addFilters = false)
class StatisticsControllerTest extends AbstractWebITest {

    public static final String MOCK_STATISTICS_PATH = "datasets/common/statistics/statistics.json";
    public static final String MOCK_CAR_STATISTICS_PATH = "datasets/common/statistics/carStatistics.json";
    public static final String MOCK_DAILY_CAR_STATISTICS_PATH = "datasets/common/statistics/dailyCarStatistics.json";

    @Test
    @DataSet(value = "common/statistics/dataset.json")
    @DisplayName("getStatistics returns statistics for date range specified")
    void testGetStatistics() throws Exception {
        mockMvc.perform(get("/api/v1/statistics/deliveries")
                        .param("dateFrom", "2022-09-21")
                        .param("dateTo", "2022-09-23")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(getResponseAsString(MOCK_STATISTICS_PATH)));
    }

    @Test
    @DataSet(value = "common/statistics/dataset.json")
    @DisplayName("getStatistics returns not found status if there are no deliveries for date range specified")
    void testGetStatisticsReturnsNotFoundStatus() throws Exception {
        mockMvc.perform(get("/api/v1/statistics/deliveries")
                        .param("dateFrom", "2022-01-01")
                        .param("dateTo", "2022-01-10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DataSet(value = "common/statistics/dataset.json")
    @DisplayName("getCarStatistics returns car statistics for date range specified")
    void testGetCarStatistics() throws Exception {
        mockMvc.perform(get("/api/v1/statistics/cars/101")
                        .param("dateFrom", "2022-09-21")
                        .param("dateTo", "2022-09-23")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(getResponseAsString(MOCK_CAR_STATISTICS_PATH)));
    }

    @Test
    @DataSet(value = "common/statistics/dataset.json")
    @DisplayName("getCarStatistics returns not found status for non-existing car")
    void testGetCarStatisticsReturnsNotFoundStatusForNonExistingCar() throws Exception {
        mockMvc.perform(get("/api/v1/statistics/cars/666")
                        .param("dateFrom", "2022-09-21")
                        .param("dateTo", "2022-09-23")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DataSet(value = "common/statistics/dataset.json")
    @DisplayName("getCarStatistics returns not found status if there are no deliveries for date range specified")
    void testGetCarStatisticsReturnsNotFoundStatusIfThereAreNoDeliveries() throws Exception {
        mockMvc.perform(get("/api/v1/statistics/cars/51")
                        .param("dateFrom", "2022-01-01")
                        .param("dateTo", "2022-01-10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DataSet(value = "common/statistics/dataset.json")
    void testGetDailyCarStatistics() throws Exception {
        mockMvc.perform(get("/api/v1/daily-statistics/cars/101")
                        .param("date", "2022-09-23")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(getResponseAsString(MOCK_DAILY_CAR_STATISTICS_PATH)));
    }

    @Test
    @DataSet(value = "common/statistics/dataset.json")
    @DisplayName("getDailyCarStatistics returns not found status for non-existing car")
    void testGetDailyCarStatisticsReturnsNotFoundStatusForNonExistingCar() throws Exception {
        mockMvc.perform(get("/api/v1/daily-statistics/cars/2000")
                        .param("date", "2022-09-23")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DataSet(value = "common/statistics/dataset.json")
    @DisplayName("getDailyCarStatistics returns not found status if there are no deliveries for date specified")
    void testGetDailyCarStatisticsReturnsNotFoundStatusIfThereAreNoDeliveries() throws Exception {
        mockMvc.perform(get("/api/v1/daily-statistics/cars/2000")
                        .param("date", "2022-09-23")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}