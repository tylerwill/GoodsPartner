package com.goodspartner.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.goodspartner.AbstractBaseITest;
import com.goodspartner.dto.CarDto;
import com.goodspartner.dto.RouteDto;
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

    private static final String MOCK_ROUTES_PATH = "datasets/common/statistics/routes.json";
    private List<RouteDto> routeDtos;
    private CarDto carDto;

    @Autowired
    private DefaultStatisticsService defaultStatisticsService;

    @BeforeAll
    void before() throws JsonProcessingException {
        routeDtos = Arrays.asList(objectMapper.readValue(getResponseAsString(MOCK_ROUTES_PATH), RouteDto[].class));
        carDto = routeDtos.get(0).getCar();
    }

    @Test
    void testCalculateRoutesStatistic() {
        DefaultStatisticsService.CalculationRoutesResult calculationRoutesResult =
                defaultStatisticsService.calculateRoutesStatistic(routeDtos);

        assertEquals(calculationRoutesResult, new DefaultStatisticsService.CalculationRoutesResult(
                19, 539.5, 30));
    }

    @Test
    void testCalculateRoutesStatisticWithCarDtoAsParameter() {
        DefaultStatisticsService.CalculationRoutesResult calculationRoutesResult =
                defaultStatisticsService.calculateRoutesStatistic(routeDtos, carDto);

        assertEquals(calculationRoutesResult, new DefaultStatisticsService.CalculationRoutesResult(
                19, 539.5, 30, 360, 2));
    }

}