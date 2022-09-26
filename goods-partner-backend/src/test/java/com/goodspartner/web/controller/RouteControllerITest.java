package com.goodspartner.web.controller;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.spring.api.DBRider;
import com.goodspartner.AbstractWebITest;
import com.goodspartner.config.TestSecurityDisableConfig;
import com.goodspartner.dto.DeliveryDto;
import com.goodspartner.entity.DeliveryStatus;
import com.goodspartner.service.client.GoogleClient;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.DistanceMatrix;
import com.google.maps.model.DistanceMatrixRow;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;

import java.time.LocalDate;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Disabled
@Import({TestSecurityDisableConfig.class})
@DBRider
@AutoConfigureMockMvc(addFilters = false)
public class RouteControllerITest extends AbstractWebITest {

    public static final String MOCKED_ROUTE = "datasets/route/route.json";
    public static final String DISTANCE_MATRIX = "datasets/route/distanceMatrix.json";
    public static final String ORIGIN_ADDR = "originAddresses";
    public static final String DEST_ADDR = "destinationAddresses";
    public static final String DISTANCE_MATRIX_ROWS = "rows";
    public static final String URL_TEMPLATE = "/api/v1/routes/calculate";
    public static final String URL_PARAM_MANE = "date";
    private static final String EMPTY_DISTANCE_MATRIX = "datasets/route/emptyDistanceMatrix.json";

    @MockBean
    private GoogleClient googleClient;

    @Test
    @DataSet(value = "route/sql_dump.json", disableConstraints = true)
    @DisplayName("given date with orders when Calculate Routers then Json Returned")
    void givenDateWithOrdersWhenCalculateRoutersThenJsonWithRoutesAndCarsReturned() throws Exception {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        DirectionsRoute directionsRoute =
                objectMapper.readValue(getClass().getClassLoader().getResource(MOCKED_ROUTE), DirectionsRoute.class);

        JsonNode jsonNode = objectMapper.readTree(getClass().getClassLoader().getResource(DISTANCE_MATRIX));
        JsonNode originAddresses = jsonNode.get(ORIGIN_ADDR);
        JsonNode destinationAddresses = jsonNode.get(DEST_ADDR);
        JsonNode rows = jsonNode.get(DISTANCE_MATRIX_ROWS);

        String[] origin = objectMapper.readValue(originAddresses.traverse(), String[].class);
        String[] dest = objectMapper.readValue(destinationAddresses.traverse(), String[].class);
        DistanceMatrixRow[] distanceMatrixRows = objectMapper.readValue(rows.traverse(), DistanceMatrixRow[].class);
        DistanceMatrix distanceMatrix = new DistanceMatrix(origin, dest, distanceMatrixRows);

        mockMvc.perform(get(URL_TEMPLATE)
                .param(URL_PARAM_MANE, "2022-02-04")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content()
                        .json(getResponseAsString("response/route-controller-calculate.json")));
    }

    @Test
    @DataSet(value = "route/sql_dump.json", disableConstraints = true)
    @DisplayName("given date without orders when Calculate Routers then empty Json Returned")
    void givenDateWithoutOrdersWhenCalculateRoutersThenEmptyJsonReturned() throws Exception {
        JsonNode jsonNode = objectMapper.readTree(getClass().getClassLoader().getResource(EMPTY_DISTANCE_MATRIX));
        JsonNode originAddresses = jsonNode.get(ORIGIN_ADDR);
        JsonNode destinationAddresses = jsonNode.get(DEST_ADDR);
        JsonNode rows = jsonNode.get(DISTANCE_MATRIX_ROWS);

        String[] origin = objectMapper.readValue(originAddresses.traverse(), String[].class);
        String[] dest = objectMapper.readValue(destinationAddresses.traverse(), String[].class);
        DistanceMatrixRow[] distanceMatrixRows = objectMapper.readValue(rows.traverse(), DistanceMatrixRow[].class);
        DistanceMatrix distanceMatrix = new DistanceMatrix(origin, dest, distanceMatrixRows);

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


    @Test
    @DataSet(value = "route/grandedolce_orders.json", disableConstraints = true)
    @DisplayName("delivery")
    void delivery() throws Exception {
        DeliveryDto deliveryDto = DeliveryDto.builder()
                .id(UUID.fromString("70574dfd-48a3-40c7-8b0c-3e5defe7d080"))
                .deliveryDate(LocalDate.of(2022, 2, 17))
                .status(DeliveryStatus.DRAFT)
                .build();

        System.out.println(objectMapper.writeValueAsString(deliveryDto));

        mockMvc.perform(post("/api/v1/routes/calculate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(deliveryDto)))
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



