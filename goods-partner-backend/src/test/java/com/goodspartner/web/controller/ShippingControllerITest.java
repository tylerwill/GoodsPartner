package com.goodspartner.web.controller;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.spring.api.DBRider;
import com.goodspartner.AbstractWebITest;
import com.goodspartner.config.TestConfigurationToCountAllQueries;
import com.goodspartner.config.TestSecurityDisableConfig;
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
public class ShippingControllerITest extends AbstractWebITest {

    private static final String SHIPPING_API = "/api/v1/shipping";

    @Test
    @DataSet(value = "datasets/delivery/delivery-shipping-controller-test.yml")
    @DisplayName("when Find By Delivery Id then List Of Product Shipping Dto Returned")
    public void whenFindByDeliveryId_thenProductShippingDtoListReturned() throws Exception {
        SQLStatementCountValidator.reset();

        mockMvc.perform(MockMvcRequestBuilders
                        .get(SHIPPING_API)
                        .param("deliveries", "00000000-0000-0000-0000-000000000111")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content()
                        .json(getResponseAsString("response/shipping-controller-get_by_delivery_id-test.json")));

        assertSelectCount(1);
    }
}