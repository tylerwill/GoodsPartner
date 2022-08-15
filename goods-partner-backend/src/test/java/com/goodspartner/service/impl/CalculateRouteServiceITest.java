package com.goodspartner.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.spring.api.DBRider;
import com.goodspartner.AbstractBaseITest;
import com.goodspartner.dto.OrderDto;
import com.goodspartner.dto.RoutePointDto;
import com.goodspartner.dto.StoreDto;
import com.goodspartner.service.CalculateRouteService;
import com.goodspartner.service.OrderService;
import com.goodspartner.web.controller.response.RoutesCalculation;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static org.mockito.Mockito.when;

@Slf4j
@DBRider
public class CalculateRouteServiceITest extends AbstractBaseITest {

    private static LocalDate DATE = LocalDate.of(2022, 2, 1);

    @Autowired
    private CalculateRouteService calculateRouteService;

    @Autowired
    private MockedStoreService mockedStoreService;

    @Autowired
    private ObjectMapper objectMapper;

//    @Autowired
//    private ExternalOrderService orderService;

    @MockBean
    private OrderService orderService;

    // Get route report by period
    @Disabled
    @Test
    @DataSet("common/car/dataset_add_car.yml")
    void testCalculateRoutes() throws JsonProcessingException {

        while (DATE.isBefore(LocalDate.of(2022, 2, 28))) {

            List<OrderDto> orders = orderService.findAllByShippingDate(DATE);

            StoreDto store = mockedStoreService.getMainStore();

            System.out.println("By Date " + DATE + " fetched Orders size: " +  orders.size());

            if (!orders.isEmpty()) {

                List<RoutesCalculation.RouteDto> routeDtos = calculateRouteService.calculateRoutes(orders, store);

                Assertions.assertEquals(1, routeDtos.size());

                RoutesCalculation.RouteDto routeDto = routeDtos.get(0);
                List<RoutePointDto> routePoints = routeDto.getRoutePoints();

                System.out.println("Size " + routePoints.size() + "  Parsed adresses:");

                routePoints.stream()
                        .map(RoutePointDto::getAddress)
                        .forEach(address -> System.out.println("   " + address));
            } else  {
                System.out.println("No Orders found for date: " + DATE);
            }

            log.info("\n");
            DATE = DATE.plusDays(1);
        }
    }

    // Get route report by specific date
    @Disabled
    @Test
    @DataSet("common/car/dataset_add_car.yml")
    void testCalculateRoutesByDate() {

        LocalDate date = LocalDate.of(2022, 2, 17);

        List<OrderDto> orders = orderService.findAllByShippingDate(date);

        Assertions.assertTrue(1 < orders.size());

        StoreDto store = mockedStoreService.getMainStore();

        log.info("By Date {} fetched Orders size: {}", date,  orders.size());
        log.info("Addresses: {}", orders.stream().map(OrderDto::getAddress).collect(Collectors.toList()));

        List<RoutesCalculation.RouteDto> routeDtos = calculateRouteService.calculateRoutes(orders, store);

        Assertions.assertEquals(1, routeDtos.size());

        RoutesCalculation.RouteDto routeDto = routeDtos.get(0);
        List<RoutePointDto> routePoints = routeDto.getRoutePoints();

        Assertions.assertEquals(9, routePoints.size());

        log.info("RoutePoints: {}", routePoints.stream().map(RoutePointDto::getAddress).collect(Collectors.toList()));
    }

    /*
    Uses the mock 1C data but still requite real GoogleAPI interaction via valid google_api_key
     */
    @Disabled
    @Test
    @DataSet("common/car/dataset_add_car.yml")
    void testCalculateRoutesByMockerOrders() throws JsonProcessingException {

        String responseAsString = getResponseAsString("response/external-order-service-2022-2-4.json");

        List<OrderDto> orders = objectMapper.readValue(responseAsString, new TypeReference<>() {
        });

        when(orderService.findAllByShippingDate(DATE)).thenReturn(orders);

        StoreDto store = mockedStoreService.getMainStore();

        List<RoutesCalculation.RouteDto> routeDtos = calculateRouteService.calculateRoutes(orders, store);

        Assertions.assertEquals(1, routeDtos.size());

        RoutesCalculation.RouteDto routeDto = routeDtos.get(0);
        List<RoutePointDto> routePoints = routeDto.getRoutePoints();

        Assertions.assertEquals(9, routePoints.size());

    }
}
