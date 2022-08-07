package com.goodspartner.web.controller;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.spring.api.DBRider;
import com.goodspartner.AbstractWebITest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DBRider
public class OrdersControllerITest extends AbstractWebITest {

    @Test
    @DataSet("common/dataset.yml")
    void givenOrders_whenCalculateOrders_thenJsonReturned() throws Exception {
        mockMvc.perform(get("/api/v1/orders")
                        .param("date", "2022-07-10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))

                .andExpect(status().isOk())
                .andExpect(content()
                        .json(
                                """
                                        {
                                          "date": "2022-07-10",
                                          "orders": [
                                            {
                                              "id": 2,
                                              "orderNumber": "43532",
                                              "createdDate": "2022-07-09",
                                              "clientName": "ТОВ Пекарня",
                                              "address": "м. Київ, вул. Металістів, 8, оф. 4-24",
                                              "managerFullName": "Іван Шугай",
                                              "products": [
                                                {
                                                  "productName": "6798 Фарба харчова червона",
                                                  "amount": 3,
                                                  "storeName": "Склад №1"
                                                },
                                                {
                                                  "productName": "576853 Масло екстра",
                                                  "amount": 4,
                                                  "storeName": "Склад №1"
                                                }
                                              ]
                                            },
                                            {
                                              "id": 3,
                                              "orderNumber": "45463",
                                              "createdDate": "2022-07-09",
                                              "clientName": "ТОВ Кондитерська",
                                              "address": "м. Київ, вул. Хрещатик, 1",
                                              "managerFullName": "Андрій Бублик",
                                              "products": [
                                                {
                                                  "productName": "66784 Арахісова паста",
                                                  "amount": 5,
                                                  "storeName": "Склад №1"
                                                },
                                                {
                                                  "productName": "8795 Мука екстра",
                                                  "amount": 5,
                                                  "storeName": "Склад №1"
                                                }
                                              ]
                                            }
                                          ]
                                        }
                                                                                """));
    }

    @Test
    @DataSet("common/dataset.yml")
    void givenNoOrdersForSpecifiedDate_whenCalculateOrders_thenJsonWithEmptyOrdersFieldReturned() throws Exception {

        mockMvc.perform(get("/api/v1/orders")
                        .param("date", "2000-01-01")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))

                .andExpect(status().isOk())
                .andExpect(content()
                        .json("""
                                {
                                  "date": "2000-01-01",
                                  "orders": []
                                }
                                """));
    }
}