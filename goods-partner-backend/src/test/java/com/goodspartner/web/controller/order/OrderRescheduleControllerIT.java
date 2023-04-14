package com.goodspartner.web.controller.order;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.spring.api.DBRider;
import com.goodspartner.AbstractWebITest;
import com.goodspartner.config.TestConfigurationToCountAllQueries;
import com.goodspartner.entity.Delivery;
import com.goodspartner.repository.DeliveryRepository;
import com.goodspartner.repository.OrderExternalRepository;
import com.goodspartner.web.controller.request.RemoveOrdersRequest;
import com.goodspartner.web.controller.request.RescheduleOrdersRequest;
import com.vladmihalcea.sql.SQLStatementCountValidator;
import net.ttddyy.dsproxy.QueryCount;
import net.ttddyy.dsproxy.QueryCountHolder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static com.vladmihalcea.sql.SQLStatementCountValidator.assertInsertCount;
import static com.vladmihalcea.sql.SQLStatementCountValidator.assertSelectCount;
import static com.vladmihalcea.sql.SQLStatementCountValidator.assertUpdateCount;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DBRider
@Import({
        TestSecurityEnableConfig.class,
        TestConfigurationToCountAllQueries.class
})
@AutoConfigureMockMvc
public class OrderRescheduleControllerIT extends AbstractWebITest {

    private static final String SKIPPED_ORDERS_ENDPOINT = "/api/v1/orders/skipped";
    private static final String SCHEDULED_ORDERS_ENDPOINT = "/api/v1/orders/scheduled";
    private static final String RESCHEDULE_SKIPPED_ORDER_ENDPOINT = "/api/v1/orders/skipped/reschedule";
    private static final String REMOVE_SKIPPED_ORDER_ENDPOINT = "/api/v1/orders/skipped";

    @Autowired
    private DeliveryRepository deliveryRepository;
    @Autowired
    private OrderExternalRepository orderExternalRepository;

    @Test
    @DataSet(value = "datasets/orders/default-order-dataset.json",
            cleanAfter = true, cleanBefore = true, skipCleaningFor = "flyway_schema_history")
    void givenNewShippingDate_whenRescheduleDroppedOrder_newOrderGotCreated() throws Exception {
        // Given
        RescheduleOrdersRequest rescheduleOrdersRequest = new RescheduleOrdersRequest();
        rescheduleOrdersRequest.setRescheduleDate(LocalDate.of(2022, 2, 20));
        rescheduleOrdersRequest.setOrderIds(List.of(251L));
        // When
        SQLStatementCountValidator.reset();
        mockMvc.perform(post(RESCHEDULE_SKIPPED_ORDER_ENDPOINT)
//                        .session(getLogistSession())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rescheduleOrdersRequest)))
                .andExpect(status().isOk())
                .andExpect(content().json(getResponseAsString("response/orders/reschedule-order-response.json"))); // id could be different, not matching it
        // TODO due to sequence fetch in various test phases Select count could be deifferent
        QueryCount queryCount = QueryCountHolder.getGrandTotal();
        long recordedSelectCount = queryCount.getSelect();
        assertTrue(recordedSelectCount <= 3); // Orders + Delivery + SequenceNextVal. N+1 Verification Passed
        assertUpdateCount(1);
        assertInsertCount(1);
        // Then
        SQLStatementCountValidator.reset();
        mockMvc.perform(get(SCHEDULED_ORDERS_ENDPOINT)
//                        .session(getLogistSession())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(getResponseAsString("response/orders/reschedule-order-response.json"))) // id could be different, not matching it
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        assertSelectCount(1);
    }

    @Test
    @DataSet(value = "datasets/orders/excluded-order-dataset.json",
            cleanAfter = true, cleanBefore = true, skipCleaningFor = "flyway_schema_history")
    void givenNewShippingDate_whenRescheduleExcludedOrder_newOrderGotCreated() throws Exception {
        // Given
        RescheduleOrdersRequest rescheduleOrdersRequest = new RescheduleOrdersRequest();
        rescheduleOrdersRequest.setRescheduleDate(LocalDate.of(2022, 2, 20));
        rescheduleOrdersRequest.setOrderIds(List.of(251L));
        // When
        SQLStatementCountValidator.reset();
        mockMvc.perform(post(RESCHEDULE_SKIPPED_ORDER_ENDPOINT)
//                        .session(getLogistSession())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rescheduleOrdersRequest)))
                .andExpect(status().isOk())
                .andExpect(content().json(getResponseAsString("response/orders/reschedule-order-response.json"))); // id could be different, not matching it
        // TODO due to sequence fetch in various test phases Select count could be different
        QueryCount queryCount = QueryCountHolder.getGrandTotal();
        long recordedSelectCount = queryCount.getSelect();
        assertTrue(recordedSelectCount <= 3); // Orders + Delivery + SequenceNextVal. N+1 Verification Passed
        assertUpdateCount(1);
        assertInsertCount(1);
        // Then
        SQLStatementCountValidator.reset();
        mockMvc.perform(get(SCHEDULED_ORDERS_ENDPOINT)
//                        .session(getLogistSession())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(getResponseAsString("response/orders/reschedule-order-response.json"))) // id could be different, not matching it
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        assertSelectCount(1);
    }

    @Test
    @DataSet(value = "datasets/orders/order-filter-dataset.json",
            cleanAfter = true, cleanBefore = true, skipCleaningFor = "flyway_schema_history")
    void givenRescheduledOrders_whenRemove_thenOrderInaccessible() throws Exception {
        // Given
        mockMvc.perform(MockMvcRequestBuilders.get(SKIPPED_ORDERS_ENDPOINT) // Skipped 1, 51, 151
//                        .session(getLogistSession())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(getResponseAsString("response/orders/skipped-orders-response.json")));
        RemoveOrdersRequest removeOrdersRequest = new RemoveOrdersRequest();
        removeOrdersRequest.setOrderIds(Arrays.asList(1L, 51L)); // Remove only 2 from 3
        // When - Then
        SQLStatementCountValidator.reset();
        mockMvc.perform(delete(REMOVE_SKIPPED_ORDER_ENDPOINT)
//                        .session(getLogistSession())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(removeOrdersRequest)))
                .andExpect(status().isOk())
                .andExpect(content().json(getResponseAsString("response/orders/remove-skipped-order-response.json")));
        assertSelectCount(1);
        assertUpdateCount(2);
        // Then
        mockMvc.perform(MockMvcRequestBuilders.get(SKIPPED_ORDERS_ENDPOINT) // Returned only 151 skipped. Other 2 removed
//                        .session(getLogistSession())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(getResponseAsString("response/orders/skipped-orders-after-remove-response.json")));
    }

    @Test
    @DataSet(value = "datasets/orders/default-order-dataset.json",
            cleanAfter = true, cleanBefore = true, skipCleaningFor = "flyway_schema_history")
    void givenNewShippingDateMatchExistentDelivery_whenRescheduleOrder_newOrderGotCreated() throws Exception {
        // Given
        UUID deliveryUUID = UUID.fromString("70574dfd-48a3-40c7-8b0c-3e5defe7d080");

        RescheduleOrdersRequest rescheduleOrdersRequest = new RescheduleOrdersRequest();
        rescheduleOrdersRequest.setOrderIds(List.of(151L));
        rescheduleOrdersRequest.setRescheduleDate(LocalDate.of(2022, 2, 17)); // Matching existent deliveryId

        verifyDatabaseBefore(deliveryUUID);

        // When
        SQLStatementCountValidator.reset();
        mockMvc.perform(post(RESCHEDULE_SKIPPED_ORDER_ENDPOINT)
//                        .session(getLogistSession())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rescheduleOrdersRequest)))
                .andExpect(status().isOk())
                .andExpect(content().json(getResponseAsString("response/orders/reschedule-order-response-assigned-delivery.json"))); // id could be different, not matching it
        // Then
//        assertSelectCount(2); // Orders + Delivery + SequenceNextVal. N+1 Verification Passed
        // TODO due to sequence fetch in various test phases Select count could be deifferent
        QueryCount queryCount = QueryCountHolder.getGrandTotal();
        long recordedSelectCount = queryCount.getSelect();
        assertTrue(recordedSelectCount <= 3); // Orders + Delivery + SequenceNextVal. N+1 Verification Passed
        assertUpdateCount(1);
        assertInsertCount(1);

        verifyDatabaseAfter(deliveryUUID);
    }

    private void verifyDatabaseBefore(UUID deliveryUUID) {
        Delivery deliveryBefore = deliveryRepository.findByIdWithOrders(deliveryUUID).get();
        assertEquals(1, deliveryBefore.getOrders().size());
        assertEquals(2, orderExternalRepository.findAll().size());
    }

    private void verifyDatabaseAfter(UUID deliveryUUID) {
        Delivery deliveryAfter = deliveryRepository.findByIdWithOrders(deliveryUUID).get();
        assertEquals(2, deliveryAfter.getOrders().size());
        assertEquals(3, orderExternalRepository.findAll().size());
    }

}
