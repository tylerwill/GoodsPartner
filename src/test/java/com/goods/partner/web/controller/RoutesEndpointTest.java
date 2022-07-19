package com.goods.partner.web.controller;

import com.goods.partner.dto.AddressOrderDto;
import com.goods.partner.dto.CalculationRoutesDto;
import com.goods.partner.dto.RouteDto;
import com.goods.partner.dto.RoutePointDto;
import com.goods.partner.service.OrderService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
public class RoutesEndpointTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    OrderService orderService;

    @Test
    @DisplayName("given Routes when Calculate Routers then Json Returned")
    public void givenRoutes_whenCalculateRouters_thenJsonReturned() throws Exception {
        AddressOrderDto addressOrderDto = AddressOrderDto.builder()
                .orderId(6)
                .orderNumber(356325)
                .orderTotalWeight(59.32)
                .build();

        RoutePointDto routePointDto = RoutePointDto.builder()
                .clientName("ТОВ Кондитерська")
                .address("м. Київ, вул. Хрещатик, 1")
                .addressTotalWeight(59.32)
                .orders(List.of(addressOrderDto))
                .build();

        RouteDto routeDto = RouteDto.builder()
                .routeId(1)
                .status("new")
                .totalWeight(59.32)
                .totalPoints(1)
                .totalOrders(1)
                .distance(153.8)
                .estimatedTime(LocalTime.of(1, 00))
                .startTime(LocalDateTime.of(2022, 07, 12, 11, 00))
                .finishTime(LocalDateTime.of(2022, 07, 12, 12, 00))
                .spentTime(null)
                .storeName("Склад №1")
                .storeAddress("Фастів, вул. Широка, 15")
                .routePoints(List.of(routePointDto))
                .build();

        CalculationRoutesDto calculationRoutesDto = CalculationRoutesDto.builder()
                .routes(List.of(routeDto))
                .date(LocalDate.of(2022, 7, 12))
                .build();

        Mockito.when(orderService.calculateRoutes(LocalDate.of(2022, 7, 12)))
                .thenReturn(calculationRoutesDto);
        mockMvc.perform(get("/calculate/routes")
                        .param("date", "2022-07-12")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))

                .andExpect(status().isOk())
                .andExpect(content()
                        .json("""
                                {
                                  "date": "2022-07-12",
                                  "routes": [
                                    {
                                      "routeId": 1,
                                      "status": "new",
                                      "totalWeight": 59.32,
                                      "totalPoints": 1,
                                      "totalOrders": 1,
                                      "distance": 153.8,
                                      "estimatedTime": "01:00:00",
                                      "startTime": "11:00:00",
                                      "finishTime": "12:00:00",
                                      "spentTime": null,
                                      "storeName": "Склад №1",
                                      "storeAddress": "Фастів, вул. Широка, 15",
                                      "routePoints": [
                                        {
                                          "clientId": 0,
                                          "clientName": "ТОВ Кондитерська",
                                          "address": "м. Київ, вул. Хрещатик, 1",
                                          "addressTotalWeight": 59.32,
                                          "orders": [
                                            {
                                              "orderId": 6,
                                              "orderNumber": 356325
                                            }
                                          ]
                                        }
                                      ]
                                    }
                                  ]                            
                                }"""));
    }
}