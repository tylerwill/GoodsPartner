package com.goodspartner.web.controller;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.spring.api.DBRider;
import com.goods.partner.AbstractWebITest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DBRider
public class StoresEndpointITest extends AbstractWebITest {

    @Test
    @DataSet("common/dataset.yml")
    @DisplayName("Check Json returned after calculate stores")
    void givenStores_whenCalculateStores_thenJsonReturned() throws Exception {

        mockMvc.perform(get("/calculate/stores")
                        .param("date", "2022-07-12")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))

                .andExpect(status().isOk())
                .andExpect(content()
                        .json(
                                """
                                        {
                                          "date": "2022-07-12",
                                          "stores": [
                                            {
                                              "storeId": 1,
                                              "storeName": "Склад №1",
                                              "orders": [
                                                {
                                                  "orderId": 6,
                                                  "orderNumber": "356325",
                                                  "totalOrderWeight": 59.32
                                                }
                                              ]
                                            }
                                          ]
                                        }
                                        """));
    }

    @Test
    @DataSet("common/dataset.yml")
    @DisplayName("Check empty Json returned after calculate stores")
    void givenNoStoresForSpecifiedDate_whenCalculateStores_thenJsonWithEmptyStoresFieldReturned() throws Exception {

        mockMvc.perform(get("/calculate/stores")
                        .param("date", "2002-07-12")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))

                .andExpect(status().isOk())
                .andExpect(content()
                        .json("""
                                {
                                  "date": "2002-07-12",
                                  "stores": []
                                }
                                """));
    }
}