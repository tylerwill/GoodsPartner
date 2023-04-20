package com.goodspartner.web.controller.delivery;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import com.github.database.rider.spring.api.DBRider;
import com.goodspartner.AbstractWebITest;
import com.goodspartner.config.TestConfigurationToCountAllQueries;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@DBRider
@Import(TestConfigurationToCountAllQueries.class)
@AutoConfigureMockMvc
@TestPropertySource(properties = "goodspartner.security.enabled=true")
public class DeliveryCalculateControllerIT extends AbstractWebITest {

    private static final String DELIVERY_CALCULATE_ENDPOINT = "/api/v1/deliveries/%s/calculate";

    @Test
    @WithMockUser(roles = "LOGISTICIAN")
    void givenNonExistingDeliveryId_whenCalculateDelivery_NotFoundReturned() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .post(String.format(DELIVERY_CALCULATE_ENDPOINT, "123e4567-e89b-12d3-a456-556642440005"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().json("{\"status\":\"NOT_FOUND\",\"message\":\"Відсутня доставка з id: 123e4567-e89b-12d3-a456-556642440005\"}\n"));
    }

    @Test
    @WithMockUser(roles = "DRIVER")
    void givenDriverSession_whenCalculateDelivery_ForbiddenReturned() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .post(String.format(DELIVERY_CALCULATE_ENDPOINT, "123e4567-e89b-12d3-a456-556642440005"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Disabled // TODO required fix. Flow is not covered!!
    @Test
    @DataSet(value = "datasets/delivery/delivery.yml",
            skipCleaningFor = "flyway_schema_history", cleanAfter = true, cleanBefore = true)
    @ExpectedDataSet(value = "delivery/delivery.yml")
    @DisplayName("when save orders with at least one unknown address provided then not found status returned")
    void whenSaveOrdersWithAtLeastOneUnknownAddressProvided_thenNotFoundReturned() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .post(String.format(DELIVERY_CALCULATE_ENDPOINT, "someOrdeer"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

}
