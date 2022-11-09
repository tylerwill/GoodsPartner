package com.goodspartner.web.controller;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.spring.api.DBRider;
import com.goodspartner.AbstractWebITest;
import com.goodspartner.config.TestSecurityDisableConfig;
import com.goodspartner.dto.UpdateDto;
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
class OrderControllerITest extends AbstractWebITest {

    @Test
    @DataSet(value = "response/order-controller-filter.json",
            cleanAfter = true, cleanBefore = true, skipCleaningFor = "flyway_schema_history")
    void getOrders() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/orders/")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DataSet(value = "response/order-controller-filter.json",
            cleanAfter = true, cleanBefore = true, skipCleaningFor = "flyway_schema_history")
    void getUpdateDeliveryDate() throws Exception {

        UpdateDto updateDto = new UpdateDto();
        List<Integer> list = List.of(251);
        LocalDate date = LocalDate.of(2022, 2, 20);
        updateDto.setDeliveryDate(date);
        updateDto.setOrdersIdList(list);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/orders/schedule")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(getResponseAsString("response/order-controller-update-delivery.json")));
    }
}