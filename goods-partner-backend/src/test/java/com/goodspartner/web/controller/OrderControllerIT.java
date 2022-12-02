package com.goodspartner.web.controller;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.spring.api.DBRider;
import com.goodspartner.AbstractWebITest;
import com.goodspartner.config.TestConfigurationToCountAllQueries;
import com.goodspartner.config.TestSecurityEnableConfig;
import com.goodspartner.dto.MapPoint;
import com.goodspartner.dto.OrderDto;
import com.goodspartner.entity.AddressStatus;
import com.goodspartner.web.controller.request.RescheduleOrdersRequest;
import com.vladmihalcea.sql.SQLStatementCountValidator;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.goodspartner.entity.DeliveryType.POSTAL;
import static com.vladmihalcea.sql.SQLStatementCountValidator.assertInsertCount;
import static com.vladmihalcea.sql.SQLStatementCountValidator.assertSelectCount;
import static com.vladmihalcea.sql.SQLStatementCountValidator.assertUpdateCount;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


/**
 * TODO
 * 1. [Done] Migrate DefaultOrderExternalServiceIT to OrderControllerIT in order to invoke from controller and verify response
 * 2. Add test for reschedule when delivery is present
 * 3. order-filter-dataset.json -> remap to YAML and rename accordingly to a case
 * 4. separate test with order with no AddressExternal
 * 5. separate test when Delivery got READY_FOR_CALCULATION due to last Order fixed Address
 */
@DBRider
@Import({
        TestSecurityEnableConfig.class,
        TestConfigurationToCountAllQueries.class
})
@AutoConfigureMockMvc
class OrderControllerIT extends AbstractWebITest {

    private static final String ROOT_ORDER_ENDPOINT = "/api/v1/orders/";
    private static final String SKIPPED_ORDERS_ENDPOINT = "/api/v1/orders/skipped";
    private static final String COMPLETED_ORDERS_ENDPOINT = "/api/v1/orders/completed";
    private static final String RESCHEDULE_ORDER_ENDPOINT = "/api/v1/orders/skipped/reschedule";
    private static final String UPDATE_ORDER_ENDPOINT = "/api/v1/orders/%d";

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
    void getSkippedOrders() throws Exception {
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
    void whenRescheduleOrder_newOrderGotCreated() throws Exception {
        SQLStatementCountValidator.reset();

        RescheduleOrdersRequest rescheduleOrdersRequest = new RescheduleOrdersRequest();
        rescheduleOrdersRequest.setRescheduleDate(LocalDate.of(2022, 2, 20));
        rescheduleOrdersRequest.setOrderIds(List.of(251));

        mockMvc.perform(MockMvcRequestBuilders.post(RESCHEDULE_ORDER_ENDPOINT)
                        .session(getLogistSession())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rescheduleOrdersRequest)))
                .andExpect(status().isOk())
                .andExpect(content().json(getResponseAsString("response/orders/reschedule-order-response.json")));

        assertSelectCount(3); // Orders + Delivery + SequenceNextVal. N+1 Verification Passed
        assertUpdateCount(2);
        assertInsertCount(1);
    }

    @Test
    @DataSet(value = "datasets/orders/default-order-dataset.json",
            cleanAfter = true, cleanBefore = true, skipCleaningFor = "flyway_schema_history")
    void whenOrderUpdate_orderFieldsGotUpdated() throws Exception {
        SQLStatementCountValidator.reset();

        mockMvc.perform(MockMvcRequestBuilders.put(String.format(UPDATE_ORDER_ENDPOINT, 251))
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(getLogistSession())
                        .content(objectMapper.writeValueAsString(buildRequestUpdateOrderDto())))
                .andExpect(status().isOk())
                .andExpect(content().json(getResponseAsString("response/orders/update-order-response.json")));

        assertSelectCount(2); // OrderById + isAllOrdersValid verification
        assertUpdateCount(3); // Update Orders + Addresses + Deliveries
    }

    private OrderDto buildRequestUpdateOrderDto() {
        return OrderDto.builder()
                // Unmodifiable
                .id(251)
                .orderNumber("00000003639 - This value not modifiable")
                .managerFullName("Крамаренко Леся - This value not modifiable")
                .shippingDate(LocalDate.parse("2022-02-20")) // This value not modifiable
                .clientName("Новус Україна ТОВ - This value not modifiable")
                .address("м.Київ, пр. Академіка Палладіна, 7-А - This value not modifiable")
                .orderWeight(1500.00)
                .products(new ArrayList<>()) // TODO check
                .refKey("18ee46a5-8e41-11ec-b3ce-00155dd72305 - This value not modifiable")
                .comment("some-test-comment - This value not modifiable")
                .deliveryId(UUID.fromString("70574dfd-48a3-40c7-8b0c-3e5defe7d081")) // - This value not modifiable
                // Modifiable
                .deliveryType(POSTAL)
                .excluded(true)
                .dropped(true)
                .isFrozen(true)
                .deliveryStart(LocalTime.of(10, 0))
                .deliveryFinish(LocalTime.of(11, 0))
                .mapPoint(MapPoint.builder()
                        .status(AddressStatus.KNOWN)
                        .address("проспект Академіка Палладіна, 9А, Київ, Україна, 03179")
                        .longitude(32.3553835000000)
                        .latitude(52.4618259000000)
                        .build())
                .build();
    }
}