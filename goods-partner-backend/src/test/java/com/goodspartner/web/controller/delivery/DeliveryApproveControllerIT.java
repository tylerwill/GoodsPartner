package com.goodspartner.web.controller.delivery;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.spring.api.DBRider;
import com.goodspartner.AbstractWebITest;
import com.goodspartner.config.TestConfigurationToCountAllQueries;
import com.goodspartner.config.TestSecurityEnableConfig;
import com.goodspartner.service.EventService;
import com.vladmihalcea.sql.SQLStatementCountValidator;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.UUID;

import static com.goodspartner.entity.DeliveryHistoryTemplate.DELIVERY_APPROVED;
import static com.vladmihalcea.sql.SQLStatementCountValidator.assertSelectCount;
import static com.vladmihalcea.sql.SQLStatementCountValidator.assertUpdateCount;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// TODO cover event propagation matching and delivery history if required
@Slf4j
@DBRider
@Import({
        TestSecurityEnableConfig.class,
        TestConfigurationToCountAllQueries.class
})
@AutoConfigureMockMvc
public class DeliveryApproveControllerIT extends AbstractWebITest {

    @MockBean
    private EventService eventService;

    @Test
    @DataSet(value = "datasets/delivery/default_deliveries_dataset.yml", skipCleaningFor = "flyway_schema_history",
            cleanAfter = true, cleanBefore = true)
    @DisplayName("when Approve Delivery then Correct DeliveryAndRoutesStatusDto Returned")
    void whenApproveDelivery_thenCorrectDeliveryAndRoutesStatusDtoReturned() throws Exception {
        SQLStatementCountValidator.reset();
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/deliveries/00000000-0000-0000-0000-000000000111/approve")
                        .session(getLogistSession()))
                .andExpect(status().isOk())
                .andExpect(content().json(getResponseAsString("response/delivery/approve-delivery-response.json")));
        Mockito.verify(eventService, times(1)).publishDeliveryEvent(DELIVERY_APPROVED, UUID.fromString("00000000-0000-0000-0000-000000000111"));
        Mockito.verify(eventService, times(1)).publishRouteStatusChangeAuto(any());
        assertSelectCount(2); // Select Delivery
        assertUpdateCount(2); // Update Delivery status + Update Route Status
    }

    @Test
    @DataSet(value = "datasets/delivery/default_deliveries_dataset.yml", skipCleaningFor = "flyway_schema_history",
            cleanAfter = true, cleanBefore = true)
    @DisplayName("when Approve Delivery By Non-Existing Id then Not Found Returned")
    void whenApproveDeliveryByNonExistingId_thenNotFoundReturned() throws Exception {
        SQLStatementCountValidator.reset();
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/deliveries/00000000-0000-0000-0000-000000000999/approve")
                        .session(getLogistSession()))
                .andExpect(status().isNotFound())
                .andExpect(content().json("{" +
                        "\"status\":\"NOT_FOUND\"," +
                        "\"message\":\"There is no delivery with id: 00000000-0000-0000-0000-000000000999\"}"));
        Mockito.verify(eventService, times(0)).publishDeliveryEvent(any(), any());
        Mockito.verify(eventService, times(0)).publishRouteStatusChangeAuto(any());
        assertSelectCount(1);
    }

    @Test
    @DataSet(value = "datasets/delivery/default_deliveries_dataset.yml")
    @DisplayName("when Approve Delivery of Non-Draft Status then Exception Thrown")
    void whenApproveDeliveryOfNonDraftStatus_thenExceptionThrown() throws Exception {
        SQLStatementCountValidator.reset();
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/deliveries/00000000-0000-0000-0000-000000000222/approve")
                        .session(getLogistSession()))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{" +
                        "\"status\":\"BAD_REQUEST\"," +
                        "\"message\":\"Unable to approve delivery: 00000000-0000-0000-0000-000000000222 with status: APPROVED\"}\n"));
        Mockito.verify(eventService, times(0)).publishDeliveryEvent(any(), any());
        Mockito.verify(eventService, times(0)).publishRouteStatusChangeAuto(any());
        assertSelectCount(1);
    }

    @Test
    @DataSet(value = "datasets/delivery/default_deliveries_dataset.yml")
    @DisplayName("when Approve Delivery without Routes then Exception Thrown")
    void whenApproveDeliveryWithoutRoutes_thenExceptionThrown() throws Exception {
        SQLStatementCountValidator.reset();
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/deliveries/00000000-0000-0000-0000-000000000000/approve")
                        .session(getLogistSession()))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{" +
                        "\"status\":\"BAD_REQUEST\"," +
                        "\"message\":\"No routes found for delivery: 00000000-0000-0000-0000-000000000000\"}"));
        assertSelectCount(2); // Select Delivery + Routes
    }

    @Test
    @DisplayName("when Approve Delivery without Routes then Exception Thrown")
    void whenApproveDeliveryByNotLogist_thenExceptionThrown() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/deliveries/123e4567-e89b-12d3-a456-556642440001/approve")
                        .session(getDriverSession()))
                .andExpect(status().isForbidden());
    }

}
