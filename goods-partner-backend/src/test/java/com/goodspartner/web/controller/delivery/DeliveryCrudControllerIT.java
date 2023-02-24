package com.goodspartner.web.controller.delivery;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import com.github.database.rider.spring.api.DBRider;
import com.goodspartner.AbstractWebITest;
import com.goodspartner.config.TestConfigurationToCountAllQueries;
import com.goodspartner.config.TestSecurityEnableConfig;
import com.vladmihalcea.sql.SQLStatementCountValidator;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static com.vladmihalcea.sql.SQLStatementCountValidator.assertDeleteCount;
import static com.vladmihalcea.sql.SQLStatementCountValidator.assertSelectCount;
import static com.vladmihalcea.sql.SQLStatementCountValidator.assertUpdateCount;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/* TODO
    1. TEST calculate delivery order count and route count when delivery colculated
    2. Expected dataset verification on DB layer
    3. Different datasets packages - initial / expected in order to not mix them up
*/
@Slf4j
@DBRider
@Import({
        TestSecurityEnableConfig.class,
        TestConfigurationToCountAllQueries.class
})
@AutoConfigureMockMvc
public class DeliveryCrudControllerIT extends AbstractWebITest {

    private static final String DELIVERIES_ENDPOINT = "/api/v1/deliveries";

    @Test
    @DataSet(value = "datasets/delivery/default_deliveries_dataset.yml",
            skipCleaningFor = "flyway_schema_history", cleanAfter = true, cleanBefore = true)
    @DisplayName("when driver gets deliveries then driver retrieve respective Deliveries assigned except Delivery in Draft status")
    public void whenGetDeliveries_thenDriverCantSeeDeliveryInStatusDRAFT() throws Exception {
        SQLStatementCountValidator.reset();
        mockMvc.perform(MockMvcRequestBuilders
                        .get(DELIVERIES_ENDPOINT)
                        .session(getDriverSession())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(getResponseAsString("response/delivery/get-deliveries-as-driver-response.json")));
        assertSelectCount(3); // User + Car + Deliveries
    }

    @Test
    @DataSet(value = "datasets/delivery/default_deliveries_dataset.yml",
            skipCleaningFor = "flyway_schema_history", cleanAfter = true, cleanBefore = true)
    @DisplayName("when logistician gets deliveries then logistician retrieve all deliveries")
    public void whenGetDeliveries_thenLogisticianSeeAllDeliveries() throws Exception {
        SQLStatementCountValidator.reset();
        mockMvc.perform(MockMvcRequestBuilders
                        .get(DELIVERIES_ENDPOINT)
                        .session(getLogistSession())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(getResponseAsString("response/delivery/get-deliveries-as-logistician-response.json")));
        assertSelectCount(2); // User + Deliveries
    }

    @Test
    @DataSet(value = "datasets/delivery/default_deliveries_dataset.yml", skipCleaningFor = "flyway_schema_history",
            cleanAfter = true, cleanBefore = true)
    @DisplayName("when Get Delivery by id then required Delivery returned")
    void whenGetDeliveryById_thenOkStatusReturned() throws Exception {
        SQLStatementCountValidator.reset();
        mockMvc.perform(MockMvcRequestBuilders
                        .get(DELIVERIES_ENDPOINT + "/00000000-0000-0000-0000-000000000111")
                        .session(getLogistSession())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(getResponseAsString("response/delivery/get-delivery-response.json")));
        assertSelectCount(1);
    }

    @Test
    @DataSet(value = "datasets/delivery/default_deliveries_dataset.yml", skipCleaningFor = "flyway_schema_history",
            cleanAfter = true, cleanBefore = true)
    @DisplayName("when delete delivery by id then required delivery returned as soft delete")
    void whenDeleteDeliveryById_thenOkStatusReturned() throws Exception {
        SQLStatementCountValidator.reset();
        mockMvc.perform(MockMvcRequestBuilders
                        .delete(DELIVERIES_ENDPOINT + "/00000000-0000-0000-0000-000000000111")
                        .session(getLogistSession())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(getResponseAsString("response/delivery/get-delivery-response.json")));
        assertSelectCount(7); // we remove orphans
        assertUpdateCount(0);
        assertDeleteCount(4);
    }

    @Test
    @DataSet(value = "datasets/delivery/default_deliveries_dataset.yml", skipCleaningFor = "flyway_schema_history",
            cleanAfter = true, cleanBefore = true)
    @DisplayName("when delete approved delivery by id then bad request status returned")
    void whenDeleteApprovedDeliveryById_thenBadRequestStatusReturned() throws Exception {
        SQLStatementCountValidator.reset();
        mockMvc.perform(MockMvcRequestBuilders
                        .delete(DELIVERIES_ENDPOINT + "/00000000-0000-0000-0000-000000000222")
                        .session(getLogistSession())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{" +
                        "\"status\":\"BAD_REQUEST\"," +
                        "\"message\":\"Видалення доставки можливе лише в статусі - Створена\"}\n"));
        assertSelectCount(1);
    }

    @Test
    @DataSet(value = "datasets/delivery/delete_delivery_dataset.yml", skipCleaningFor = "flyway_schema_history",
            cleanAfter = true, cleanBefore = true)
    @ExpectedDataSet(value = "datasets/delivery/delete_delivery.yml")
    @DisplayName("when Delete Delivery then Ok Status Returned")
    void whenDeleteDelivery_thenOkStatusReturned() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/api/v1/deliveries/f8c3de3d-1fea-4d7c-a8b0-29f63c4c3454")
                        .session(getLogistSession())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
    // TODO delete wrong status
}
