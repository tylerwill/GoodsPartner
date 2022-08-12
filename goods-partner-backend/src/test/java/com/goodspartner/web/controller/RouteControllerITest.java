package com.goodspartner.web.controller;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.spring.api.DBRider;
import com.goodspartner.AbstractWebITest;
import com.goodspartner.service.GoogleApiService;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.DistanceMatrix;
import com.google.maps.model.DistanceMatrixRow;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DBRider
public class RouteControllerITest extends AbstractWebITest {
    public static final String MOCKED_ROUTE = "datasets/route/route.json";
    public static final String DISTANCE_MATRIX = "datasets/route/distanceMatrix.json";
    public static final String ORIGIN_ADDR = "originAddresses";
    public static final String DEST_ADDR = "destinationAddresses";
    public static final String DISTANCE_MATRIX_ROWS = "rows";
    public static final String URL_TEMPLATE = "/api/v1/routes/calculate";
    public static final String URL_PARAM_MANE = "date";
    private static final String EMPTY_DISTANCE_MATRIX = "datasets/route/emptyDistanceMatrix.json";

    private final ObjectMapper mapper = new ObjectMapper();

    @MockBean
    private GoogleApiService googleApiService;

    @Test
    @DataSet(value = "route/sql_dump.json", disableConstraints = true)
    @DisplayName("given date with orders when Calculate Routers then Json Returned")
    public void givenDateWithOrdersWhenCalculateRoutersThenJsonWithRoutesAndCarsReturned() throws Exception {
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        DirectionsRoute directionsRoute =
                mapper.readValue(getClass().getClassLoader().getResource(MOCKED_ROUTE), DirectionsRoute.class);

        JsonNode jsonNode = mapper.readTree(getClass().getClassLoader().getResource(DISTANCE_MATRIX));
        JsonNode originAddresses = jsonNode.get(ORIGIN_ADDR);
        JsonNode destinationAddresses = jsonNode.get(DEST_ADDR);
        JsonNode rows = jsonNode.get(DISTANCE_MATRIX_ROWS);

        String[] origin = mapper.readValue(originAddresses.traverse(), String[].class);
        String[] dest = mapper.readValue(destinationAddresses.traverse(), String[].class);
        DistanceMatrixRow[] distanceMatrixRows = mapper.readValue(rows.traverse(), DistanceMatrixRow[].class);
        DistanceMatrix distanceMatrix = new DistanceMatrix(origin, dest, distanceMatrixRows);

        when(googleApiService.getDirectionRoute(anyString(), anyList())).thenReturn(directionsRoute);
        when(googleApiService.getDistanceMatrix(anyList())).thenReturn(distanceMatrix);


        String contentAsString = mockMvc.perform(get(URL_TEMPLATE)
                .param(URL_PARAM_MANE, "2022-08-07")
                .contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        mockMvc.perform(get(URL_TEMPLATE)
                .param(URL_PARAM_MANE, "2022-08-07")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content()
                        .json("""  
                                  {
                                  "date": "2022-08-07",
                                  "routes": [
                                    {
                                      "id": 51,
                                      "status": "DRAFT",
                                      "totalWeight": 1148.76,
                                      "totalPoints": 3,
                                      "totalOrders": 4,
                                      "distance": 162.44,
                                      "estimatedTime": 176,
                                      "startTime": null,
                                      "finishTime": null,
                                      "spentTime": 0,
                                      "storeName": "Склад №1",
                                      "storeAddress": "Фастів, вул. Широка, 15",
                                      "optimization": false,
                                      "routePoints": [
                                        {
                                          "status": "PENDING",
                                          "completedAt": null,
                                          "clientId": 0,
                                          "clientName": "ТОВ \\"Пекарня\\"",
                                          "address": "м. Київ, вул. Молодогвардійська, 22, оф. 35",
                                          "addressTotalWeight": 804.98,
                                          "routePointDistantTime": 76,
                                          "orders": [
                                            {
                                              "id": 9,
                                              "orderNumber": "986453"
                                            },
                                            {
                                              "id": 10,
                                              "orderNumber": "426457"
                                            }
                                          ]
                                        },
                                        {
                                          "status": "PENDING",
                                          "completedAt": null,
                                          "clientId": 0,
                                          "clientName": "ТОВ \\"Пекарня\\"",
                                          "address": "м. Київ, вул. Металістів, 8, оф. 4-24",
                                          "addressTotalWeight": 5.5,
                                          "routePointDistantTime": 10,
                                          "orders": [
                                            {
                                              "id": 3,
                                              "orderNumber": "45463"
                                            }
                                          ]
                                        },
                                        {
                                          "status": "PENDING",
                                          "completedAt": null,
                                          "clientId": 0,
                                          "clientName": "ТОВ \\"Пекарня\\"",
                                          "address": "м. Київ, вул. Хрещатик, 1",
                                          "addressTotalWeight": 338.28,
                                          "routePointDistantTime": 11,
                                          "orders": [
                                            {
                                              "id": 5,
                                              "orderNumber": "432565"
                                            }
                                          ]
                                        }
                                      ],
                                      "car": {
                                        "id": 51,
                                        "name": "Mercedes Sprinter",
                                        "licencePlate": "AA 1111 CT",
                                        "driver": "Oleg Dudka",
                                        "weightCapacity": 2000,
                                        "cooler": false,
                                        "available": true,
                                        "loadSize": 1148.76,
                                        "travelCost": 12
                                      }
                                    }
                                  ],
                                  "carLoadDetails": [
                                    {
                                      "car": {
                                        "id": 51,
                                        "name": "Mercedes Sprinter",
                                        "licencePlate": "AA 1111 CT",
                                        "driver": "Oleg Dudka",
                                        "weightCapacity": 2000,
                                        "cooler": false,
                                        "available": true,
                                        "loadSize": 1148.76,
                                        "travelCost": 12
                                      },
                                      "orders": [
                                        {
                                          "id": 5,
                                          "orderNumber": "432565",
                                          "products": [
                                            {
                                              "productName": "4695 Фарба харчова зелена",
                                              "amount": 8,
                                              "storeName": "Склад №1",
                                              "unitWeight": 3.54,
                                              "totalProductWeight": 28.32
                                            },
                                            {
                                              "productName": "8452 Масло 1й гатунок",
                                              "amount": 9,
                                              "storeName": "Склад №1",
                                              "unitWeight": 34.44,
                                              "totalProductWeight": 309.96
                                            }
                                          ],
                                          "createdDate": "2022-08-06",
                                          "clientName": "ТОВ \\"Пекарня\\"",
                                          "address": "м. Київ, вул. Хрещатик, 1",
                                          "managerFullName": "Андрій Бублик",
                                          "orderWeight": 338.28
                                        },
                                        {
                                          "id": 3,
                                          "orderNumber": "45463",
                                          "products": [
                                            {
                                              "productName": "66784 Арахісова паста",
                                              "amount": 5,
                                              "storeName": "Склад №1",
                                              "unitWeight": 0.55,
                                              "totalProductWeight": 2.75
                                            },
                                            {
                                              "productName": "66784 Арахісова паста",
                                              "amount": 5,
                                              "storeName": "Склад №1",
                                              "unitWeight": 0.55,
                                              "totalProductWeight": 2.75
                                            }
                                          ],
                                          "createdDate": "2022-08-05",
                                          "clientName": "ТОВ \\"Пекарня\\"",
                                          "address": "м. Київ, вул. Металістів, 8, оф. 4-24",
                                          "managerFullName": "Андрій Бублик",
                                          "orderWeight": 5.5
                                        },
                                        {
                                          "id": 10,
                                          "orderNumber": "426457",
                                          "products": [
                                            {
                                              "productName": "56743 Форми пасхальні",
                                              "amount": 7,
                                              "storeName": "Склад №1",
                                              "unitWeight": 4.65,
                                              "totalProductWeight": 32.550000000000004
                                            },
                                            {
                                              "productName": "4695 Фарба харчова зелена",
                                              "amount": 8,
                                              "storeName": "Склад №1",
                                              "unitWeight": 3.54,
                                              "totalProductWeight": 28.32
                                            }
                                          ],
                                          "createdDate": "2022-08-07",
                                          "clientName": "ТОВ \\"Пекарня\\"",
                                          "address": "м. Київ, вул. Молодогвардійська, 22, оф. 35",
                                          "managerFullName": "Іван Шугай",
                                          "orderWeight": 60.870000000000005
                                        },
                                        {
                                          "id": 9,
                                          "orderNumber": "986453",
                                          "products": [
                                            {
                                              "productName": "66784 Арахісова паста",
                                              "amount": 5,
                                              "storeName": "Склад №1",
                                              "unitWeight": 0.55,
                                              "totalProductWeight": 2.75
                                            },
                                            {
                                              "productName": "8795 Мука екстра",
                                              "amount": 6,
                                              "storeName": "Склад №1",
                                              "unitWeight": 123.56,
                                              "totalProductWeight": 741.36
                                            }
                                          ],
                                          "createdDate": "2022-08-07",
                                          "clientName": "ТОВ \\"Пекарня\\"",
                                          "address": "м. Київ, вул. Молодогвардійська, 22, оф. 35",
                                          "managerFullName": "Петро Коваленко",
                                          "orderWeight": 744.11
                                        }
                                      ]
                                    }
                                  ]
                                }"""));
    }

    @Test
    @DataSet(value = "route/sql_dump.json", disableConstraints = true)
    @DisplayName("given date without orders when Calculate Routers then empty Json Returned")
    public void givenDateWithoutOrdersWhenCalculateRoutersThenEmptyJsonReturned() throws Exception {
        JsonNode jsonNode = mapper.readTree(getClass().getClassLoader().getResource(EMPTY_DISTANCE_MATRIX));
        JsonNode originAddresses = jsonNode.get(ORIGIN_ADDR);
        JsonNode destinationAddresses = jsonNode.get(DEST_ADDR);
        JsonNode rows = jsonNode.get(DISTANCE_MATRIX_ROWS);

        String[] origin = mapper.readValue(originAddresses.traverse(), String[].class);
        String[] dest = mapper.readValue(destinationAddresses.traverse(), String[].class);
        DistanceMatrixRow[] distanceMatrixRows = mapper.readValue(rows.traverse(), DistanceMatrixRow[].class);
        DistanceMatrix distanceMatrix = new DistanceMatrix(origin, dest, distanceMatrixRows);

        when(googleApiService.getDirectionRoute(anyString(), anyList())).thenReturn(null);
        when(googleApiService.getDistanceMatrix(anyList())).thenReturn(distanceMatrix);

        mockMvc.perform(get(URL_TEMPLATE)
                .param(URL_PARAM_MANE, "7777-07-07")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content()
                        .json("""  
                                {
                                  "date": "7777-07-07",
                                  "routes": [],
                                  "carLoadDetails": []
                                }"""));
    }
}



