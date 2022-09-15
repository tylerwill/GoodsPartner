package com.goodspartner.web.controller;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.database.rider.core.api.dataset.CompareOperation;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import com.github.database.rider.spring.api.DBRider;
import com.goodspartner.AbstractWebITest;
import com.goodspartner.config.TestSecurityDisableConfig;
import com.goodspartner.dto.DeliveryDto;
import com.goodspartner.entity.DeliveryStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.util.NestedServletException;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.Assert.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DBRider
@Import({TestSecurityDisableConfig.class})
@AutoConfigureMockMvc(addFilters = false)
class DeliveryControllerITest extends AbstractWebITest {

    @Test
    @DataSet("common/delivery/dataset_delivery.yml")
    @DisplayName("when Get Delivery then OK status returned")
    void whenGetDeliveryById_thenJsonReturned() throws Exception {

        mockMvc.perform(get("/api/v1/deliveries/237e9877-e79b-12d4-a765-321741963000")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DataSet("common/delivery/dataset_delivery.yml")
    @DisplayName("when Get Deliveries then OK status returned")
    void whenGetDeliveries_thenJsonReturned() throws Exception {

        mockMvc.perform(get("/api/v1/deliveries")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DataSet("common/delivery/dataset_delivery.yml")
    @ExpectedDataSet(value = "common/delivery/dataset_add_delivery.yml",
            ignoreCols = "id", compareOperation = CompareOperation.CONTAINS)
    @DisplayName("when Add Delivery then then OK status returned")
    void whenAddDelivery_thenOkStatusReturned() throws Exception {
        objectMapper.registerModule(new JavaTimeModule());

        DeliveryDto deliveryDto = DeliveryDto.builder()
                .id(UUID.fromString("2222222-2222-2222-2222-222222222222"))
                .deliveryDate(LocalDate.parse("2022-07-10"))
                .status(DeliveryStatus.DRAFT)
                .build();

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/deliveries")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(deliveryDto)))
                .andExpect(status().isOk());
    }

    @Test
    @DataSet("common/delivery/dataset_delivery.yml")
    @ExpectedDataSet("common/delivery/dataset_updated_delivery.yml")
    @DisplayName("when Update Delivery then Ok Status Returned")
    void whenUpdateDelivery_thenOkStatusReturned() throws Exception {
        objectMapper.registerModule(new JavaTimeModule());

        DeliveryDto deliveryDto = DeliveryDto.builder()
                .deliveryDate(LocalDate.parse("2022-08-21"))
                .status(DeliveryStatus.APPROVED)
                .build();

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/deliveries/237e9877-e79b-12d4-a765-321741963000")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(deliveryDto)))
                .andExpect(status().isOk());
    }

    @Test
    @DataSet("common/delivery/dataset_delivery.yml")
    @ExpectedDataSet("common/delivery/dataset_delete_delivery.yml")
    @DisplayName("when Delete Delivery then Ok Status Returned")
    void whenDeleteDelivery_thenOkStatusReturned() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/deliveries/237e9877-e79b-12d4-a765-321741963001")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DataSet("common/delivery/dataset_delivery.yml")
    @DisplayName("when Delete Delivery With Incorrect Id then Bad Request Return")
    void whenDeleteDelivery_withIncorrectId_thenBadRequestReturn() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/deliveries/123")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DataSet("common/delivery/dataset_delivery.yml")
    @DisplayName("when Update Delivery With Incorrect Id then Bad Request Return")
    void whenUpdateDelivery_withIncorrectId_thenBadRequestReturn() {
        objectMapper.registerModule(new JavaTimeModule());

        DeliveryDto deliveryDto = DeliveryDto.builder()
                .deliveryDate(LocalDate.parse("2022-08-21"))
                .status(DeliveryStatus.APPROVED)
                .build();

        assertThrows(NestedServletException.class, () ->
                mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/deliveries/237e9877-e79b-12d4-a765-321741963012")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(deliveryDto)))
                        .andExpect(status().isNotFound()));
    }
}