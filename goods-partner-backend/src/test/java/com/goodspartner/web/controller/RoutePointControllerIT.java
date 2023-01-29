package com.goodspartner.web.controller;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.spring.api.DBRider;
import com.goodspartner.AbstractWebITest;
import com.goodspartner.config.TestConfigurationToCountAllQueries;
import com.goodspartner.config.TestSecurityEnableConfig;
import com.goodspartner.web.action.RoutePointAction;
import com.graphhopper.GHResponse;
import com.graphhopper.ResponsePath;
import com.vladmihalcea.sql.SQLStatementCountValidator;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;

import static com.vladmihalcea.sql.SQLStatementCountValidator.assertInsertCount;
import static com.vladmihalcea.sql.SQLStatementCountValidator.assertSelectCount;
import static com.vladmihalcea.sql.SQLStatementCountValidator.assertUpdateCount;
import static org.hamcrest.Matchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DBRider
@Import({
        TestSecurityEnableConfig.class,
        TestConfigurationToCountAllQueries.class
})
public class RoutePointControllerIT extends AbstractWebITest {

    private static final String UPDATE_ROUTE_POINT_ENDPOINT = "/api/v1/route-points/%d/%s";
    private static final String ORDERS_BY_ROUTE_POINT_ENDPOINT = "/api/v1/route-points/%d/orders";
    private static final long ROUTE_POINT_ID = 1052L;

    @Mock
    private GHResponse ghResponse;

    @Mock
    private ResponsePath responsePath;

    @Test
    @DataSet(value = {"datasets/route-points/common-dataset.json",
            "datasets/route-points/route-point-complete-dataset.json"},
            cleanAfter = true, cleanBefore = true, skipCleaningFor = "flyway_schema_history")
    void givenDeliveryWithRoute_whenUpdateRoutePointStatus_thenJsonWithRoutePointActionResponseReturned() throws Exception {
        RoutePointAction complete = RoutePointAction.COMPLETE;

        when(hopper.route(Mockito.any())).thenReturn(ghResponse);
        when(ghResponse.hasErrors()).thenReturn(false);
        when(ghResponse.getBest()).thenReturn(responsePath);
        when(responsePath.getTime()).thenReturn(5 * 60 * 1000L); // 5 min in mills

        SQLStatementCountValidator.reset();
        mockMvc.perform(post(String.format(UPDATE_ROUTE_POINT_ENDPOINT, ROUTE_POINT_ID, complete))
                        .session(getLogistSession())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.deliveryId").value("49228d27-2ce7-4246-b7c3-e53c143e5550"))
                .andExpect(jsonPath("$.deliveryStatus").value("APPROVED"))
                .andExpect(jsonPath("$.routeId").value("1002"))
                .andExpect(jsonPath("$.routeStatus").value("INPROGRESS"))
                .andExpect(jsonPath("$.routePointId").value("1052"))
                .andExpect(jsonPath("$.routePointStatus").value("DONE"))
                .andExpect(jsonPath("$.pointCompletedAt").value(any(String.class)));
        assertSelectCount(8);
        assertInsertCount(1);
        assertUpdateCount(3);
    }

    @Test
    @DataSet(value = {"datasets/route-points/common-dataset.json",
            "datasets/route-points/route-point-and-route-and-delivery-complete-dataset.json"},
            cleanAfter = true, cleanBefore = true, skipCleaningFor = "flyway_schema_history")
    void givenDeliveryWithRoute_whenUpdateRoutePointStatus_thenCompleteRouteAndDeliveryAndJsonWithRoutePointActionResponseReturned() throws Exception {
        RoutePointAction complete = RoutePointAction.COMPLETE;

        SQLStatementCountValidator.reset();
        mockMvc.perform(post(String.format(UPDATE_ROUTE_POINT_ENDPOINT, ROUTE_POINT_ID, complete))
                        .session(getLogistSession())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.deliveryId").value("49228d27-2ce7-4246-b7c3-e53c143e5550"))
                .andExpect(jsonPath("$.deliveryStatus").value("COMPLETED"))
                .andExpect(jsonPath("$.routeId").value("1002"))
                .andExpect(jsonPath("$.routeStatus").value("COMPLETED"))
                .andExpect(jsonPath("$.routeFinishTime").value(any(String.class)))
                .andExpect(jsonPath("$.routePointId").value("1052"))
                .andExpect(jsonPath("$.routePointStatus").value("DONE"))
                .andExpect(jsonPath("$.pointCompletedAt").value(any(String.class)));
        assertSelectCount(13);
        assertInsertCount(3);
        assertUpdateCount(3);
    }

    @Test
    @DataSet(value = {"datasets/route-points/common-dataset.json",
            "datasets/route-points/route-point-complete-dataset.json"},
            cleanAfter = true, cleanBefore = true, skipCleaningFor = "flyway_schema_history")
    void givenDeliveryWithRoute_whenGetOrdersByRoutePointId_thenJsonWithOrdersReturned() throws Exception {
        SQLStatementCountValidator.reset();

        mockMvc.perform(get(String.format(ORDERS_BY_ROUTE_POINT_ENDPOINT, ROUTE_POINT_ID))
                        .session(getLogistSession())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(getResponseAsString("response/route-points/orders-by-route-point-response.json")));

        assertSelectCount(1);
    }
}
