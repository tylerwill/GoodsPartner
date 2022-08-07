package com.goodspartner.web.controller;

import com.goodspartner.AbstractWebITest;
import com.goodspartner.dto.CarDto;
import com.goodspartner.dto.OrderDto;
import com.goodspartner.dto.ProductDto;
import com.goodspartner.dto.RoutePointDto;
import com.goodspartner.entity.RouteStatus;
import com.goodspartner.web.controller.response.RoutesCalculation;
import com.goodspartner.service.OrderService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class RouteControllerITest extends AbstractWebITest {

    // TODO: Maybe we shouldn't mock all order service in this Integration test
    @MockBean
    private OrderService orderService;

    @Test
    @DisplayName("given Routes when Calculate Routers then Json Returned")
    public void givenRoutes_whenCalculateRouters_thenJsonReturned() throws Exception {
        RoutePointDto.AddressOrderDto addressOrderDto = RoutePointDto.AddressOrderDto.builder()
                .id(6)
                .orderNumber("356325")
                .orderTotalWeight(59.32)
                .build();

        RoutePointDto routePointDto = RoutePointDto.builder()
                .clientName("ТОВ Кондитерська")
                .address("м. Київ, вул. Хрещатик, 1")
                .addressTotalWeight(59.32)
                .routePointDistantTime(Duration.ofSeconds(3600))
                .orders(List.of(addressOrderDto))
                .build();

        CarDto carDto = CarDto.builder()
                .id(1)
                .name("Mercedes Vito")
                .driver("Ivan Piddubny")
                .licencePlate("AA 2222 CT")
                .travelCost(10)
                .available(true)
                .cooler(false)
                .weightCapacity(1000)
                .loadSize(59.32)
                .build();

        ProductDto productInfoDto = ProductDto.builder()
                .productName("3434 Паста шоколадна")
                .amount(1)
                .unitWeight(1.52)
                .build();

        ProductDto productInfoDto2 = ProductDto.builder()
                .productName("46643 Фарба харчова синя")
                .amount(10)
                .unitWeight(57.8)
                .build();

        OrderDto orderInfoDto = OrderDto.builder()
                .products(List.of(productInfoDto, productInfoDto2))
                .orderNumber(String.valueOf(356325))
                .id(6)
                .build();

        RoutesCalculation.CarLoadDto carLoadDto = RoutesCalculation.CarLoadDto.builder()
                .car(carDto)
                .orders(List.of(orderInfoDto))
                .build();

        RoutesCalculation.RouteDto routeDto = RoutesCalculation.RouteDto.builder()
                .id(1)
                .status(RouteStatus.DRAFT)
                .totalWeight(59.32)
                .totalPoints(1)
                .totalOrders(1)
                .distance(153.8)
                .estimatedTime(Duration.ofSeconds(3600))
                .startTime(LocalDateTime.of(2022, 07, 12, 11, 00))
                .finishTime(LocalDateTime.of(2022, 07, 12, 12, 00))
                .spentTime(Duration.ofSeconds(3600))
                .storeName("Склад №1")
                .storeAddress("Фастів, вул. Широка, 15")
                .routePoints(List.of(routePointDto))
                .car(carDto)
                .build();

        RoutesCalculation routesCalculation = RoutesCalculation.builder()
                .routes(List.of(routeDto))
                .carLoadDetails(List.of(carLoadDto))
                .date(LocalDate.of(2022, 7, 12))
                .build();

        Mockito.when(orderService.calculateRoutes(LocalDate.of(2022, 7, 12)))
                .thenReturn(routesCalculation);
        mockMvc.perform(get("/api/v1/routes")
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
                                      "id": 1,
                                      "status": "DRAFT",
                                      "totalWeight": 59.32,
                                      "totalPoints": 1,
                                      "totalOrders": 1,
                                      "distance": 153.8,
                                      "estimatedTime": "PT1H",
                                      "startTime": "11:00:00",
                                      "finishTime": "12:00:00",
                                      "spentTime": "PT1H",
                                      "storeName": "Склад №1",
                                      "storeAddress": "Фастів, вул. Широка, 15",
                                      "routePoints": [
                                        {
                                          "clientId": 0,
                                          "clientName": "ТОВ Кондитерська",
                                          "address": "м. Київ, вул. Хрещатик, 1",
                                          "addressTotalWeight": 59.32,
                                          "routePointDistantTime": "PT1H",
                                          "orders": [
                                            {
                                              "id": 6,
                                              "orderNumber": "356325"
                                            }
                                          ]
                                        }
                                      ],
                                      "car": {
                                        "id": 1,
                                        "name": "Mercedes Vito",
                                        "licencePlate": "AA 2222 CT",
                                        "driver": "Ivan Piddubny",
                                        "weightCapacity": 1000,
                                        "cooler": false,
                                        "available": true,
                                        "loadSize": 59.32,
                                        "travelCost": 10
                                      }
                                    }
                                  ],
                                  "carLoadDetails": [
                                    {
                                      "car": {
                                        "id": 1,
                                        "name": "Mercedes Vito",
                                        "licencePlate": "AA 2222 CT",
                                        "driver": "Ivan Piddubny",
                                        "weightCapacity": 1000,
                                        "cooler": false,
                                        "available": true,
                                        "loadSize": 59.32,
                                        "travelCost": 10
                                      },
                                      "orders": [
                                        {
                                          "id": 6,
                                          "orderNumber": "356325",
                                          "products": [
                                            {
                                              "productName": "3434 Паста шоколадна",
                                              "amount": 1,
                                              "unitWeight": 1.52
                                            },
                                            {
                                              "productName": "46643 Фарба харчова синя",
                                              "amount": 10,
                                              "unitWeight": 57.8
                                            }
                                          ]
                                        }
                                      ]
                                    }
                                  ]
                                }"""
                        ));
    }
}