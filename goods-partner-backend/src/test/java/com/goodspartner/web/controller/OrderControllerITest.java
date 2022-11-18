package com.goodspartner.web.controller;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.spring.api.DBRider;
import com.goodspartner.AbstractWebITest;
import com.goodspartner.config.TestSecurityDisableConfig;
import com.goodspartner.web.controller.request.RescheduleOrdersRequest;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import({TestSecurityDisableConfig.class})
@AutoConfigureMockMvc(addFilters = false)
@DBRider
@Disabled
class OrderControllerITest extends AbstractWebITest {

    @Test
    @DataSet(value = "datasets/order-controller/order-controller-filter.json",
            cleanAfter = true, cleanBefore = true, skipCleaningFor = "flyway_schema_history")
    void getOrders() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/orders/completed")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DataSet(value = "datasets/order-controller/order-controller-filter.json",
            cleanAfter = true, cleanBefore = true, skipCleaningFor = "flyway_schema_history")
    void getUpdateDeliveryDate() throws Exception {

        RescheduleOrdersRequest rescheduleOrdersRequest = new RescheduleOrdersRequest();
        List<Integer> list = List.of(251);
        LocalDate date = LocalDate.of(2022, 2, 20);
        rescheduleOrdersRequest.setRescheduleDate(date);
        rescheduleOrdersRequest.setOrderIds(list);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/orders/reschedule")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rescheduleOrdersRequest)))
                .andExpect(status().isOk())
                .andExpect(content().json(getResponseAsString("response/order-controller-update-delivery.json")));
    }
}