package com.goodspartner.web.controller;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.spring.api.DBRider;
import com.goodspartner.AbstractWebITest;
import com.goodspartner.config.TestSecurityDisableConfig;
import com.goodspartner.service.GoogleApiService;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.DistanceMatrix;
import com.google.maps.model.DistanceMatrixRow;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

        mockMvc.perform(get(URL_TEMPLATE)
                .param(URL_PARAM_MANE, "2022-08-07")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content()
                        .json(getResponseAsString("response/route-controller-calculate.json")));
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



