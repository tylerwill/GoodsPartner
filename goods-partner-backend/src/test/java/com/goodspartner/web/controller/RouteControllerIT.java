package com.goodspartner.web.controller;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.spring.api.DBRider;
import com.goodspartner.AbstractWebITest;
import com.goodspartner.config.TestConfigurationToCountAllQueries;
import com.goodspartner.config.TestSecurityEnableConfig;
import com.goodspartner.web.action.RouteAction;
import com.vladmihalcea.sql.SQLStatementCountValidator;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;

import static com.vladmihalcea.sql.SQLStatementCountValidator.assertInsertCount;
import static com.vladmihalcea.sql.SQLStatementCountValidator.assertSelectCount;
import static com.vladmihalcea.sql.SQLStatementCountValidator.assertUpdateCount;
import static org.hamcrest.Matchers.any;
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
@AutoConfigureMockMvc
public class RouteControllerIT extends AbstractWebITest {

    private static final String ROUTES_BY_DELIVERY_ENDPOINT = "/api/v1/routes";
    private static final String UPDATE_ROUTE_ENDPOINT = "/api/v1/routes/%d/%s";
    private static final long ROUTE_ID = 1002L;

    @Test
    @DataSet(value = {"datasets/routes/common-dataset.json", "datasets/routes/route-filter-dataset.json"},
            cleanAfter = true, cleanBefore = true, skipCleaningFor = "flyway_schema_history")
    void givenDeliveryWithRoutes_whenGetRoutes_thenJsonWithEmptyRouteFieldReturned() throws Exception {
        SQLStatementCountValidator.reset();
        mockMvc.perform(get(ROUTES_BY_DELIVERY_ENDPOINT)
                        .param("deliveryId", "6a311f72-7613-11ed-a1eb-0242ac120002")
                        .session(getLogistSession())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
        assertSelectCount(2);
    }

    @Test
    @DataSet(value = {"datasets/routes/common-dataset.json", "datasets/routes/route-filter-dataset.json"},
            cleanAfter = true, cleanBefore = true, skipCleaningFor = "flyway_schema_history")
    void givenDeliveryWithRoutes_whenLogisticianGetRoutes_thenJsonWithRoutesReturned() throws Exception {
        SQLStatementCountValidator.reset();
        mockMvc.perform(get(ROUTES_BY_DELIVERY_ENDPOINT)
                        .param("deliveryId", "49228d27-2ce7-4246-b7c3-e53c143e5550")
                        .session(getLogistSession())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(getResponseAsString("response/routes/routes-for-logistician-by-deliveryId-response.json")));
        assertSelectCount(2);
    }

    @Test
    @DataSet(value = {"datasets/routes/common-dataset.json", "datasets/routes/route-filter-dataset.json"},
            cleanAfter = true, cleanBefore = true, skipCleaningFor = "flyway_schema_history")
    void givenDeliveryWithRoutes_whenDriverGetRoutes_thenJsonWithRouteReturned() throws Exception {
        SQLStatementCountValidator.reset();
        mockMvc.perform(get(ROUTES_BY_DELIVERY_ENDPOINT)
                        .param("deliveryId", "49228d27-2ce7-4246-b7c3-e53c143e5550")
                        .session(getDriverSession())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(getResponseAsString("response/routes/routes-for-driver-by-deliveryId-response.json")));
        assertSelectCount(3);
    }

    @Test
    @DataSet(value = {"datasets/routes/common-dataset.json", "datasets/routes/route-filter-dataset.json"},
            cleanAfter = true, cleanBefore = true, skipCleaningFor = "flyway_schema_history")
    void givenDeliveryWithRoutes_whenUpdateRouteStatus_thenStartRouteAndJsonWithRouteActionResponseReturned() throws Exception {
        RouteAction start = RouteAction.START;

        SQLStatementCountValidator.reset();

        mockMvc.perform(post(String.format(UPDATE_ROUTE_ENDPOINT, ROUTE_ID, start))
                        .session(getLogistSession())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(getResponseAsString("response/routes/route-start-response.json")));
        assertSelectCount(4);
        assertInsertCount(1);
    }

    @Test
    @DataSet(value = {"datasets/routes/common-dataset.json", "datasets/routes/route-complete-dataset.json"},
            cleanAfter = true, cleanBefore = true, skipCleaningFor = "flyway_schema_history")
    void givenDeliveryWithRoutes_whenUpdateRouteStatus_thenCompleteRouteAndJsonWithRouteActionResponseReturned() throws Exception {
        RouteAction complete = RouteAction.COMPLETE;

        SQLStatementCountValidator.reset();
        mockMvc.perform(post(String.format(UPDATE_ROUTE_ENDPOINT, ROUTE_ID, complete))
                        .session(getLogistSession())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.deliveryId").value("49228d27-2ce7-4246-b7c3-e53c143e5550"))
                .andExpect(jsonPath("$.deliveryStatus").value("APPROVED"))
                .andExpect(jsonPath("$.routeId").value("1002"))
                .andExpect(jsonPath("$.routeStatus").value("COMPLETED"))
                .andExpect(jsonPath("$.routeFinishTime").value(any(String.class)));
        assertSelectCount(5);
        assertInsertCount(1);
        assertUpdateCount(4);
    }

    @Test
    @DataSet(value = {"datasets/routes/common-dataset.json",
            "datasets/routes/route-and-automatically-delivery-complete-with-dataset.json"},
            cleanAfter = true, cleanBefore = true, skipCleaningFor = "flyway_schema_history")
    void givenDeliveryWithRoutes_whenUpdateRouteStatus_thenCompleteRouteAndDeliveryAutomaticallyAndJsonWithRouteActionResponseReturned() throws Exception {
        RouteAction complete = RouteAction.COMPLETE;

        SQLStatementCountValidator.reset();
        mockMvc.perform(post(String.format(UPDATE_ROUTE_ENDPOINT, ROUTE_ID, complete))
                        .session(getLogistSession())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.deliveryId").value("49228d27-2ce7-4246-b7c3-e53c143e5550"))
                .andExpect(jsonPath("$.deliveryStatus").value("COMPLETED"))
                .andExpect(jsonPath("$.routeId").value("1002"))
                .andExpect(jsonPath("$.routeStatus").value("COMPLETED"))
                .andExpect(jsonPath("$.routeFinishTime").value(any(String.class)));
        assertSelectCount(5);
        assertInsertCount(2);
        assertUpdateCount(5);
    }

    //TODO: Test route reordering
}