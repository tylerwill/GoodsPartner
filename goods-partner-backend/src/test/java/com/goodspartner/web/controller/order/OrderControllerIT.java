package com.goodspartner.web.controller.order;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.spring.api.DBRider;
import com.goodspartner.AbstractWebITest;
import com.goodspartner.config.TestConfigurationToCountAllQueries;
import com.goodspartner.config.TestSecurityEnableConfig;
import com.goodspartner.web.controller.request.ExcludeOrderRequest;
import com.vladmihalcea.sql.SQLStatementCountValidator;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static com.vladmihalcea.sql.SQLStatementCountValidator.assertSelectCount;
import static com.vladmihalcea.sql.SQLStatementCountValidator.assertUpdateCount;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DBRider
@Import({
        TestSecurityEnableConfig.class,
        TestConfigurationToCountAllQueries.class
})
@AutoConfigureMockMvc
class OrderControllerIT extends AbstractWebITest {

    private static final String ORDERS_BY_DELIVERY_ENDPOINT = "/api/v1/orders";
    private static final String SKIPPED_ORDERS_ENDPOINT = "/api/v1/orders/skipped";
    private static final String COMPLETED_ORDERS_ENDPOINT = "/api/v1/orders/completed";
    private static final String EXCLUDE_ORDER_ENDPOINT = "/api/v1/orders/%d/exclude";

    @Test
    @DataSet(value = "datasets/orders/order-filter-dataset.json",
            cleanAfter = true, cleanBefore = true, skipCleaningFor = "flyway_schema_history")
    void givenDeliveryWithoutOrders_whenGetOrders_thenEmptyOrdersListReturned() throws Exception {
        mockMvc.perform(get(ORDERS_BY_DELIVERY_ENDPOINT)
                        .param("deliveryId", "11111111-48a3-40c7-8b0c-3e5defe7d080")
                        .session(getLogistSession())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    @DataSet(value = "datasets/orders/order-filter-dataset.json",
            cleanAfter = true, cleanBefore = true, skipCleaningFor = "flyway_schema_history")
    void givenDeliveryWithOrders_whenLogistGetOrders_thenAllDeliveryOrdersListReturned() throws Exception {
        SQLStatementCountValidator.reset();
        mockMvc.perform(get(ORDERS_BY_DELIVERY_ENDPOINT)
                        .param("deliveryId", "70574dfd-48a3-40c7-8b0c-3e5defe7d081")
                        .session(getLogistSession())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(getResponseAsString("response/orders/orders-by-deliveryId-response.json")));
        assertSelectCount(2); // find user + respective grandedolce_orders
    }

    @Test
    @DataSet(value = "datasets/orders/driver-order-dataset.json",
            cleanAfter = true, cleanBefore = true, skipCleaningFor = "flyway_schema_history")
    void givenDeliveryWithOrders_whenDriverGetOrders_thenDriverRelatedDeliveryOrdersListReturned() throws Exception {
        SQLStatementCountValidator.reset();
        mockMvc.perform(get(ORDERS_BY_DELIVERY_ENDPOINT)
                        .param("deliveryId", "70574dfd-48a3-40c7-8b0c-3e5defe7d081")
                        .session(getDriverSession())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(getResponseAsString("response/orders/driver-orders-by-deliveryId-response.json")));
        assertSelectCount(3); // find user + respective grandedolce_orders
    }

    @Test
    @DataSet(value = "datasets/orders/order-filter-dataset.json",
            cleanAfter = true, cleanBefore = true, skipCleaningFor = "flyway_schema_history")
    void getCompletedOrders() throws Exception {
        SQLStatementCountValidator.reset();
        mockMvc.perform(MockMvcRequestBuilders.get(COMPLETED_ORDERS_ENDPOINT)
                        .session(getLogistSession())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(getResponseAsString("response/orders/completed-orders-response.json")));
        assertSelectCount(1);
    }

    @Test
    @DataSet(value = "datasets/orders/order-filter-dataset.json",
            cleanAfter = true, cleanBefore = true, skipCleaningFor = "flyway_schema_history")
    void givenSkippedOrders_whenGetSkippedOrders_SkippedOrdersReturned() throws Exception {
        // orderId=1 - RoutePoint Skipped . orderId=51 - excluded . orderId=151 - dropped
        SQLStatementCountValidator.reset();
        mockMvc.perform(MockMvcRequestBuilders.get(SKIPPED_ORDERS_ENDPOINT)
                        .session(getLogistSession())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(getResponseAsString("response/orders/skipped-orders-response.json")));
        assertSelectCount(1);
    }

    @Test
    @DataSet(value = "datasets/orders/default-order-dataset.json",
            cleanAfter = true, cleanBefore = true, skipCleaningFor = "flyway_schema_history")
    void whenOrderRemoveWithExcludeReason() throws Exception {
        SQLStatementCountValidator.reset();

        var excludeOrderRequest = new ExcludeOrderRequest();
        excludeOrderRequest.setExcludeReason("Тест");

        mockMvc.perform(MockMvcRequestBuilders.post(String.format(EXCLUDE_ORDER_ENDPOINT, 251))
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(getLogistSession())
                        .content(objectMapper.writeValueAsString(excludeOrderRequest)))
                .andExpect(status().isOk())
                .andExpect(content().json(getResponseAsString("response/orders/delete-order-response.json")));

        assertSelectCount(4); // OrderById + isAllOrdersValid verification + Exclude check
        assertUpdateCount(2); // Update Orders + Delivery
    }
}