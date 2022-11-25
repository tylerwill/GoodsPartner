package com.goodspartner.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.goodspartner.AbstractBaseITest;
import com.goodspartner.entity.Car;
import com.goodspartner.entity.Route;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

@TestInstance(PER_CLASS)
@Disabled
// TODO rework after moving RoutePoints to separate table
class DefaultStatisticsServiceTest extends AbstractBaseITest {

    private static final String MOCK_ROUTES_PATH = "mock/route/mocked-statistic-routes.json";
    private List<Route> routes;
    private Car car;

    @Autowired
    private DefaultStatisticsService defaultStatisticsService;

    @BeforeAll
    void before() throws JsonProcessingException {
        routes = Arrays.asList(objectMapper.readValue(getResponseAsString(MOCK_ROUTES_PATH), Route[].class));
        car = routes.get(0).getCar();
    }

    @Test
    void testCalculateRoutesStatistic() {
        DefaultStatisticsService.CalculationRoutesResult calculationRoutesResult =
                defaultStatisticsService.calculateRoutesStatistic(routes);

        assertEquals(calculationRoutesResult, new DefaultStatisticsService.CalculationRoutesResult(
                19, 539.5, 30));
    }

    @Test
    void testCalculateRoutesStatisticWithCarDtoAsParameter() {
//        DefaultStatisticsService.CalculationRoutesResult calculationRoutesResult =
//                defaultStatisticsService.calculateRoutesStatistic(routes, car);

//        assertEquals(calculationRoutesResult, new DefaultStatisticsService.CalculationRoutesResult(
//                19, 539.5, 30, 360, 2));
    }

}