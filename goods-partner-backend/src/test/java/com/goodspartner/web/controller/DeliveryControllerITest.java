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
import com.goodspartner.service.CalculateRouteService;
import com.goodspartner.web.controller.response.RoutesCalculation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DBRider
@Import({TestSecurityDisableConfig.class})
@AutoConfigureMockMvc(addFilters = false)
class DeliveryControllerITest extends AbstractWebITest {

    private static final String MOCKED_DELIVERY_DTO = "datasets/common/delivery/calculate/deliveryDto.json";
    private static final String MOCKED_ROUTE = "datasets/common/delivery/calculate/RouteDto.json";
    @MockBean
    private CalculateRouteService calculateRouteService;

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

        System.out.println(objectMapper.writeValueAsString(deliveryDto));

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
    void whenUpdateDelivery_withIncorrectId_thenBadRequestReturn() throws Exception {
        objectMapper.registerModule(new JavaTimeModule());

        DeliveryDto deliveryDto = DeliveryDto.builder()
                .deliveryDate(LocalDate.parse("2022-08-21"))
                .status(DeliveryStatus.APPROVED)
                .build();

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/deliveries/237e9877-e79b-12d4-a765-321741963012")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(deliveryDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DataSet(value = "common/delivery/calculate/sqlDump.json", skipCleaningFor = "flyway_schema_history",
            cleanAfter = true, cleanBefore = true,
            executeStatementsBefore = "ALTER SEQUENCE routes_sequence RESTART WITH 50")
    @DisplayName("when Calculate Delivery With Correct Id then DeliveryDto Return")
    void whenCalculateDelivery_withCorrectId_thenDeliveryDtoReturn() throws Exception {
        objectMapper.registerModule(new JavaTimeModule());

        RoutesCalculation.RouteDto routeDto =
                objectMapper.readValue(getClass().getClassLoader().getResource(MOCKED_ROUTE), RoutesCalculation.RouteDto.class);
        List<RoutesCalculation.RouteDto> routes = List.of(routeDto);
        when(calculateRouteService.calculateRoutes(anyList(), any())).thenReturn(routes);

        DeliveryDto deliveryDto =
                objectMapper.readValue(getClass().getClassLoader().getResource(MOCKED_DELIVERY_DTO), DeliveryDto.class);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/deliveries/70574dfd-48a3-40c7-8b0c-3e5defe7d080/calculate")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(deliveryDto)));
    }

    @Test
    @DataSet(value = "common/delivery/calculate/sqlDump.json", skipCleaningFor = "flyway_schema_history",
            cleanAfter = true, cleanBefore = true,
            executeStatementsBefore = "ALTER SEQUENCE routes_sequence RESTART WITH 1")
    @DisplayName("when RECalculate Delivery With Correct Id then DeliveryDto Return")
    void whenRECalculateDelivery_withCorrectId_thenDeliveryDtoReturn() throws Exception {
        objectMapper.registerModule(new JavaTimeModule());

        RoutesCalculation.RouteDto routeDto =
                objectMapper.readValue(getClass().getClassLoader().getResource(MOCKED_ROUTE), RoutesCalculation.RouteDto.class);
        List<RoutesCalculation.RouteDto> routes = List.of(routeDto);
        when(calculateRouteService.calculateRoutes(anyList(), any())).thenReturn(routes);

        DeliveryDto deliveryDto =
                objectMapper.readValue(getClass().getClassLoader().getResource(MOCKED_DELIVERY_DTO), DeliveryDto.class);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/deliveries/70574dfd-48a3-40c7-8b0c-3e5defe7d080/recalculate")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(deliveryDto)));
    }


    @Test
    @DataSet(value = "common/delivery/calculate/sqlDump.json", skipCleaningFor = "flyway_schema_history",
            cleanAfter = true, cleanBefore = true,
            executeStatementsBefore = "ALTER SEQUENCE routes_sequence RESTART WITH 1")
    @DisplayName("when Calculate Delivery With empty orders then Not Found Return")
    void whenCalculateDelivery_withIncorrectId_thenNotFoundReturn() throws Exception {
        objectMapper.registerModule(new JavaTimeModule());

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/deliveries/70574dfd-48a3-40c7-8b0c-3e5defe7d081/calculate")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}