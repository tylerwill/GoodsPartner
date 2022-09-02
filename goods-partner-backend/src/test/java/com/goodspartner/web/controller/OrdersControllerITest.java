package com.goodspartner.web.controller;

import com.goodspartner.AbstractWebITest;
import com.goodspartner.dto.OrderDto;
import com.goodspartner.dto.ProductDto;
import com.goodspartner.service.OrderService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import java.time.LocalDate;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class OrdersControllerITest extends AbstractWebITest {

    @MockBean
    private OrderService orderService;

    @Test
    @DisplayName("given OrderDto when Calculate Orders then Json Returned")
    void givenOrderDto_whenCalculateOrders_thenJsonReturned() throws Exception {
        ProductDto firstProductDto = ProductDto.builder()
                .amount(1)
                .storeName("Склад №1")
                .unitWeight(12.0)
                .productName("Наповнювач фруктово-ягідний (декоргель) (12 кг)")
                .totalProductWeight(12.0)
                .build();

        OrderDto firstOrderDto = OrderDto.builder()
                .orderNumber("45678")
                .refKey("01grande-0000-0000-0000-000000000000")
                .createdDate(LocalDate.parse("2022-02-17"))
                .clientName("Домашня випічка")
                .address("Бровари, Марії Лагунової, 11")
                .comment("бн")
                .managerFullName("Балашова Лариса")
                .products(List.of(firstProductDto))
                .orderWeight(12.00)
                .validAddress(false)
                .build();

        ProductDto secondProductDto = ProductDto.builder()
                .amount(1)
                .storeName("Склад №2")
                .unitWeight(20.0)
                .productName("66784 Арахісова паста")
                .totalProductWeight(20.0)
                .build();

        OrderDto secondOrderDto = OrderDto.builder()
                .orderNumber("43532")
                .refKey("02grande-0000-0000-0000-000000000000")
                .createdDate(LocalDate.parse("2022-02-17"))
                .clientName("ТОВ Пекарня")
                .address("м.Киїїїв, вул. Некрасова 8667")
                .comment("бн")
                .managerFullName("Шульженко Олег")
                .products(List.of(secondProductDto))
                .orderWeight(20.00)
                .validAddress(false)
                .build();

        Mockito.when(orderService.findAllByShippingDate(LocalDate.parse("2022-07-10")))
                .thenReturn(List.of(firstOrderDto, secondOrderDto));

        Mockito.when(orderService.calculateTotalOrdersWeight(List.of(firstOrderDto, secondOrderDto)))
                .thenReturn(32.00);

        mockMvc.perform(get("/api/v1/orders")
                        .param("date", "2022-07-10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content()
                        .json("""
                                {
                                   "date":"2022-07-10",
                                   "validOrders":[
                                      {
                                         "orderNumber":"45678",
                                         "refKey": "01grande-0000-0000-0000-000000000000",
                                         "createdDate":"2022-02-17",
                                         "clientName":"Домашня випічка",
                                         "address":"Бровари, Марії Лагунової, 11",
                                         "managerFullName":"Балашова Лариса",
                                         "comment": "бн",
                                         "validAddress":true,
                                         "products":[
                                            {
                                               "productName":"Наповнювач фруктово-ягідний (декоргель) (12 кг)",
                                               "amount":1,
                                               "storeName":"Склад №1"
                                            }
                                         ]
                                      }
                                   ],
                                  "invalidOrders":[
                                      {
                                         "orderNumber":"43532",
                                         "refKey": "02grande-0000-0000-0000-000000000000",
                                         "createdDate":"2022-02-17",
                                         "clientName":"ТОВ Пекарня",
                                         "address":"м.Киїїїв, вул. Некрасова 8667",
                                         "comment": "бн",
                                         "managerFullName":"Шульженко Олег",
                                         "validAddress":false,
                                         "products":[
                                            {
                                               "productName":"66784 Арахісова паста",
                                               "amount":1,
                                               "storeName":"Склад №2"
                                            }
                                         ]
                                      }
                                   ],
                                   "totalOrdersWeight": 32.00
                                }
                                                                          """));
    }

    @Test
    void givenNoOrdersForSpecifiedDate_whenCalculateOrders_thenJsonWithEmptyOrdersFieldReturned() throws Exception {

        mockMvc.perform(get("/api/v1/orders")
                        .param("date", "2000-01-01")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))

                .andExpect(status().isOk())
                .andExpect(content()
                        .json("""
                                {
                                   "date":"2000-01-01",
                                   "validOrders":[],
                                   "invalidOrders":[],
                                   "totalOrdersWeight":0.00
                                }
                                   """));
    }
}