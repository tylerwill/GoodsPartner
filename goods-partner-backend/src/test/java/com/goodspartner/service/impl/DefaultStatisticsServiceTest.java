package com.goodspartner.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.goodspartner.AbstractBaseITest;
import com.goodspartner.dto.CarDto;
import com.goodspartner.entity.Route;
import com.goodspartner.mapper.CarMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

@TestInstance(PER_CLASS)
class DefaultStatisticsServiceTest extends AbstractBaseITest {

    private static final String MOCK_ROUTES_PATH = "mock/route/mocked-statistic-routes.json";
    private List<Route> routes;
    private CarDto carDto;

    @Autowired
    private DefaultStatisticsService defaultStatisticsService;
    @Autowired
    private CarMapper carMapper;

    @BeforeAll
    void before() throws JsonProcessingException {
        routes = Arrays.asList(objectMapper.readValue(getResponseAsString(MOCK_ROUTES_PATH), Route[].class));
        carDto = carMapper.toCarDto(routes.get(0).getCar());
    }

    @Test
    void testCalculateRoutesStatistic() {
        DefaultStatisticsService.CalculationRoutesResult calculationRoutesResult =
                defaultStatisticsService.calculateRoutesStatistic(routes);

        DefaultStatisticsService.CalculationRoutesResult expectedCalculationResult =
                new DefaultStatisticsService.CalculationRoutesResult(4, 39.0, 36);

        assertEquals(expectedCalculationResult, calculationRoutesResult);
    }

    @Test
    void testCalculateRoutesStatisticWithCarDtoAsParameter() {
        DefaultStatisticsService.CalculationRoutesResult calculationRoutesResult =
                defaultStatisticsService.calculateRoutesStatistic(routes, carDto);

        DefaultStatisticsService.CalculationRoutesResult expectedCalculationResult =
                new DefaultStatisticsService.CalculationRoutesResult(
                        4, 39.0, 36, 380, 1);

        assertEquals(expectedCalculationResult, calculationRoutesResult);
    }
}