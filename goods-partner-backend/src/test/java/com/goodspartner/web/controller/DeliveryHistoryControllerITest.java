package com.goodspartner.web.controller;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.spring.api.DBRider;
import com.goodspartner.AbstractWebITest;
import com.goodspartner.config.TestConfigurationToCountAllQueries;
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


@Import({
        TestSecurityDisableConfig.class,
        TestConfigurationToCountAllQueries.class
})
@AutoConfigureMockMvc(addFilters = false)
@DBRider
class DeliveryHistoryControllerITest extends AbstractWebITest {

    private static final String DELIVERY_HISTORY_API = "/api/v1/histories";
    private static final String DELIVERY_ID = "00000000-0000-0000-0000-000000000123";

    @Test
    @DataSet(value = "datasets/history/delivery-history-test.yml", disableConstraints = true,
            cleanAfter = true, cleanBefore = true, skipCleaningFor = "flyway_schema_history")
    @DisplayName("when Find By Delivery Id then List Of CarLoadDto Returned")
    public void whenFindByDeliveryId_thenRelatedHistoryReturned() throws Exception {
        SQLStatementCountValidator.reset();

        mockMvc.perform(MockMvcRequestBuilders
                        .get(DELIVERY_HISTORY_API)
                        .param("deliveryId", DELIVERY_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content()
                        .json(getResponseAsString("response/history/delivery-history-controller-test.json")));

        assertSelectCount(2);
    }

}