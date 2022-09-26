package com.goodspartner.web.controller;

import com.github.database.rider.core.api.dataset.CompareOperation;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import com.github.database.rider.spring.api.DBRider;
import com.goodspartner.AbstractWebITest;
import com.goodspartner.config.TestSecurityDisableConfig;
import com.goodspartner.dto.DeliveryDto;
import com.goodspartner.dto.MapPoint;
import com.goodspartner.dto.OrderDto;
import com.goodspartner.dto.Product;
import com.goodspartner.dto.StoreDto;
import com.goodspartner.dto.VRPSolution;
import com.goodspartner.entity.DeliveryStatus;
import com.goodspartner.entity.Route;
import com.goodspartner.repository.CarRepository;
import com.goodspartner.service.GraphhopperService;
import com.goodspartner.service.StoreService;
import com.goodspartner.service.VRPSolver;
import com.graphhopper.ResponsePath;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.AdditionalMatchers;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DBRider
@Import({TestSecurityDisableConfig.class})
@AutoConfigureMockMvc(addFilters = false)
@TestInstance(PER_CLASS)
class DeliveryControllerITest extends AbstractWebITest {

    private static final String MOCKED_DELIVERY_DTO = "datasets/common/delivery/calculate/deliveryDto.json";
    private static final String MOCKED_ROUTE = "datasets/common/delivery/calculate/RouteDto.json";

    @Autowired
    private StoreService storeService;

    @MockBean
    private CarRepository carRepository;
    @MockBean
    private VRPSolver vrpSolver;
    @MockBean
    private GraphhopperService graphhopperService;
    @MockBean
    private ResponsePath graphhopperResponse;

    private MapPoint mapPointAutovalidated;
    private MapPoint mapPointKnown;
    private MapPoint mapPointUnknown;
    private OrderDto orderDto;

    @BeforeAll
    void before() {

        mapPointAutovalidated = MapPoint.builder()
                .address("16B, вулиця Княжий Затон, 16Б, Київ, Україна, 02000")
                .latitude(50.403193)
                .longitude(30.6163764)
                .status(MapPoint.AddressStatus.AUTOVALIDATED)
                .build();

        mapPointKnown = MapPoint.builder()
                .address("16B, вулиця Княжий Затон, 16Б, Київ, Україна, 02000")
                .latitude(50.403193)
                .longitude(30.6163764)
                .status(MapPoint.AddressStatus.KNOWN)
                .build();

        mapPointUnknown = MapPoint.builder()
                .status(MapPoint.AddressStatus.UNKNOWN)
                .build();

        Product product = Product.builder()
                .amount(1)
                .storeName("Склад №1")
                .unitWeight(12.00)
                .productName("Наповнювач фруктово-ягідний (декоргель) (12 кг)")
                .totalProductWeight(12.00)
                .build();

        orderDto = OrderDto.builder()
                .id(0)
                .refKey("f6bc11b6-8264-11ec-b3ce-00155dd72305")
                .orderNumber("00000002124")
                .createdDate(LocalDate.of(2022, 1, 31))
                .comment("нал без оплати  0675438404 Світлана")
                .managerFullName("Шевцова Галина")
                .isFrozen(false)
                .deliveryStart(LocalTime.of(12, 0))
                .deliveryFinish(LocalTime.of(14, 0))
                .clientName("Ексклюзив Кейк")
                .address("м. Київ, Княжий затон 16 б")
                .products(List.of(product))
                .orderWeight(12.00)
                .build();
    }

    @Test
    @DataSet(value = "delivery/delivery.yml")
    @DisplayName("when Get Delivery then OK status returned")
    void whenGetDeliveryById_thenOkStatusReturned() throws Exception {

        mockMvc.perform(get("/api/v1/deliveries/123e4567-e89b-12d3-a456-556642440000")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DataSet(value = "delivery/delivery.yml")
    @DisplayName("when Get Deliveries then OK status returned")
    void whenGetDeliveries_thenOkStatusReturned() throws Exception {

        mockMvc.perform(get("/api/v1/deliveries")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DataSet(value = "delivery/delivery.yml")
    @ExpectedDataSet(value = "delivery/add_delivery.yml",
            ignoreCols = "id", compareOperation = CompareOperation.CONTAINS)
    @DisplayName("when Add Delivery then then OK status returned")
    void whenAddDelivery_thenOkStatusReturned() throws Exception {

        DeliveryDto deliveryDto = DeliveryDto.builder()
                .deliveryDate(LocalDate.parse("2022-07-10"))
                .status(DeliveryStatus.DRAFT)
                .build();

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/deliveries")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(deliveryDto)))
                .andExpect(status().isOk());
    }

    @Test
    @DataSet(value = "delivery/delivery.yml")
    @ExpectedDataSet(value = "delivery/update_delivery.yml")
    @DisplayName("when Update Delivery then Ok Status Returned")
    void whenUpdateDelivery_thenOkStatusReturned() throws Exception {

        DeliveryDto deliveryDto = DeliveryDto.builder()
                .deliveryDate(LocalDate.parse("2000-01-01"))
                .status(DeliveryStatus.APPROVED)
                .build();

        System.out.println(objectMapper.writeValueAsString(deliveryDto));

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/deliveries/123e4567-e89b-12d3-a456-556642440000")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(deliveryDto)))
                .andExpect(status().isOk());
    }

    @Test
    @DataSet(value = "delivery/add_delivery.yml")
    @ExpectedDataSet(value = "delivery/delivery.yml")
    @DisplayName("when Delete Delivery then Ok Status Returned")
    void whenDeleteDelivery_thenOkStatusReturned() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .delete("/api/v1/deliveries/f8c3de3d-1fea-4d7c-a8b0-29f63c4c3454")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DataSet(value = "delivery/delivery.yml")
    @DisplayName("when Delete Delivery By Non-Existing Id then Not Found Returned")
    void whenDeleteDelivery_byNonExistingId_thenNotFoundReturned() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders
                .delete("/api/v1/deliveries/237e9877-e79b-12d4-a765-321741963012")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DataSet(value = "delivery/delivery.yml")
    @DisplayName("when Update Delivery By Non-Existing Id then Not Found Returned")
    void whenUpdateDelivery_byNonExistingId_thenNotFoundReturned() throws Exception {

        DeliveryDto deliveryDto = DeliveryDto.builder()
                .deliveryDate(LocalDate.parse("2022-08-21"))
                .status(DeliveryStatus.APPROVED)
                .build();

        mockMvc.perform(MockMvcRequestBuilders
                .put("/api/v1/deliveries/237e9877-e79b-12d4-a765-321741963012")
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

        StoreDto store = storeService.getMainStore();

        Route route = objectMapper.readValue(getClass().getClassLoader().getResource(MOCKED_ROUTE), Route.class);

        VRPSolution regularVrpSolution = new VRPSolution();
        regularVrpSolution.setRoutePoints(route.getRoutePoints());
        regularVrpSolution.setCar(route.getCar());
        when(vrpSolver.optimize(Collections.emptyList(), store, Collections.emptyList())).thenReturn(Collections.emptyList());
        when(vrpSolver.optimize(
                AdditionalMatchers.not(ArgumentMatchers.eq(Collections.emptyList())),
                any(),
                AdditionalMatchers.not(ArgumentMatchers.eq(Collections.emptyList()))))
                .thenReturn(List.of(regularVrpSolution));

        when(graphhopperService.getRoute(anyList())).thenReturn(graphhopperResponse);
        when(graphhopperResponse.getDistance()).thenReturn(80000.0);
        when(graphhopperResponse.getTime()).thenReturn(4800000L);

        when(carRepository.findByAvailableTrueAndCoolerIs(false)).thenReturn(List.of(route.getCar()));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/deliveries/70574dfd-48a3-40c7-8b0c-3e5defe7d080/calculate")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(getResponseAsString(MOCKED_DELIVERY_DTO)));
    }

    @Test
    @DataSet(value = "common/delivery/calculate/sqlDump.json", skipCleaningFor = "flyway_schema_history",
            cleanAfter = true, cleanBefore = true,
            executeStatementsBefore = "ALTER SEQUENCE routes_sequence RESTART WITH 1")
    @DisplayName("when RECalculate Delivery With Correct Id then DeliveryDto Return")
    void whenRECalculateDelivery_withCorrectId_thenDeliveryDtoReturn() throws Exception {

        StoreDto store = storeService.getMainStore();

        Route route = objectMapper.readValue(getClass().getClassLoader().getResource(MOCKED_ROUTE), Route.class);

        VRPSolution regularVrpSolution = new VRPSolution();
        regularVrpSolution.setRoutePoints(route.getRoutePoints());
        regularVrpSolution.setCar(route.getCar());
        when(vrpSolver.optimize(Collections.emptyList(), store, Collections.emptyList())).thenReturn(Collections.emptyList());
        when(vrpSolver.optimize(
                AdditionalMatchers.not(ArgumentMatchers.eq(Collections.emptyList())),
                any(),
                AdditionalMatchers.not(ArgumentMatchers.eq(Collections.emptyList()))))
                .thenReturn(List.of(regularVrpSolution));

        when(graphhopperService.getRoute(anyList())).thenReturn(graphhopperResponse);
        when(graphhopperResponse.getDistance()).thenReturn(80000.0);
        when(graphhopperResponse.getTime()).thenReturn(4800000L);

        when(carRepository.findByAvailableTrueAndCoolerIs(false)).thenReturn(List.of(route.getCar()));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/deliveries/70574dfd-48a3-40c7-8b0c-3e5defe7d080/calculate")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(getResponseAsString(MOCKED_DELIVERY_DTO)));
    }


    @Test
    @DataSet(value = "common/delivery/calculate/sqlDump.json", skipCleaningFor = "flyway_schema_history",
            cleanAfter = true, cleanBefore = true,
            executeStatementsBefore = "ALTER SEQUENCE routes_sequence RESTART WITH 1")
    @DisplayName("when Calculate Delivery With empty orders then Not Found Return")
    void whenCalculateDelivery_withIncorrectId_thenBadRequestReturn() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/deliveries/70574dfd-48a3-40c7-8b0c-3e5defe7d081/calculate")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DataSet(value = "delivery/delivery.yml")
    @ExpectedDataSet(value = "delivery/save_orders.yml")
    @DisplayName("when save orders with autovalidated map point status then Ok status returned")
    void whenSaveOrdersWithAutovalidatedMapPointStatus_thenOkStatusReturned() throws Exception {

        orderDto.setMapPoint(mapPointAutovalidated);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/v1/deliveries/123e4567-e89b-12d3-a456-556642440001/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(List.of(orderDto))))
                .andExpect(status().isOk());
    }

    @Test
    @DataSet(value = "delivery/delivery.yml")
    @ExpectedDataSet(value = "delivery/save_orders.yml", ignoreCols = {"id"})
    @DisplayName("when save orders with known map point status then Ok status returned")
    void whenSaveOrdersWithKnownMapPointStatus_thenOkStatusReturned() throws Exception {

        orderDto.setMapPoint(mapPointKnown);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/v1/deliveries/123e4567-e89b-12d3-a456-556642440001/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(List.of(orderDto))))
                .andExpect(status().isOk());
    }

    @Test
    @DataSet(value = "delivery/delivery.yml")
    @ExpectedDataSet(value = "delivery/delivery.yml")
    @DisplayName("when save orders and non-existing delivery id is passed then not found status returned")
    void whenSaveOrdersAndNonExistingDeliveryIdIsPassed_thenNotFoundReturned() throws Exception {

        orderDto.setMapPoint(mapPointAutovalidated);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/v1/deliveries/123e4567-e89b-12d3-a456-556642440005/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(List.of(orderDto))))
                .andExpect(status().isNotFound());
    }

    @Test
    @DataSet(value = "delivery/delivery.yml")
    @ExpectedDataSet(value = "delivery/delivery.yml")
    @DisplayName("when save orders with at least one unknown address provided then not found status returned")
    void whenSaveOrdersWithAtLeastOneUnknownAddressProvided_thenNotFoundReturned() throws Exception {

        orderDto.setMapPoint(mapPointUnknown);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/v1/deliveries/123e4567-e89b-12d3-a456-556642440001/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(List.of(orderDto))))
                .andExpect(status().isNotFound());
    }
}