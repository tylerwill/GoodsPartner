package com.goodspartner.web.controller;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.spring.api.DBRider;
import com.goodspartner.AbstractWebITest;
import com.goodspartner.config.TestConfigurationToCountAllQueries;
import com.goodspartner.config.TestSecurityEnableConfig;
import com.vladmihalcea.sql.SQLStatementCountValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static com.vladmihalcea.sql.SQLStatementCountValidator.assertSelectCount;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DBRider
@Import({
        TestSecurityEnableConfig.class,
        TestConfigurationToCountAllQueries.class
})
@AutoConfigureMockMvc
class CarLoadControllerITest extends AbstractWebITest {

    private static final String CAR_LOADS_API = "/api/v1/car-loads";

    @Test
    @DataSet(value = "datasets/delivery/delivery-carload-controller-test.yml",
            cleanBefore = true, cleanAfter = true, skipCleaningFor = "flyway_schema_history")
    @DisplayName("when Find By Delivery Id then List Of CarLoadDto Returned")
    public void whenFindByDeliveryId_thenCarLoadDtoListReturned() throws Exception {
        SQLStatementCountValidator.reset();

        mockMvc.perform(MockMvcRequestBuilders
                        .get(CAR_LOADS_API)
                        .param("deliveryId", "00000000-0000-0000-0000-000000000111")
                        .session(getDriverSession())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content()
                        .json(getResponseAsString("response/carload-controller-get_by_delivery_id.json")));

        assertSelectCount(3); // One for user + One for Car + One for CarLoads
    }
}
