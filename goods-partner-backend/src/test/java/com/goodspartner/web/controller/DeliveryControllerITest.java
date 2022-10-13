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
import com.goodspartner.dto.VRPSolution;
import com.goodspartner.entity.DeliveryStatus;
import com.goodspartner.entity.Route;
import com.goodspartner.entity.RoutePoint;
import com.goodspartner.entity.RoutePointStatus;
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
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import static com.goodspartner.dto.MapPoint.AddressStatus.KNOWN;
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

    private final LinkedList<RoutePoint> incorrectRoutePoints = new LinkedList<>();
    private final LinkedList<RoutePoint> routePoints = new LinkedList<>();
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

    private MapPoint mapPointAutovalidatedFirst;
    private MapPoint mapPointAutovalidatedSecond;
    private MapPoint mapPointKnown;
    private MapPoint mapPointUnknown;
    private OrderDto orderDtoFirst;
    private OrderDto orderDtoSecond;
    private MapPoint storeMapPoint;

    @BeforeAll
    void before() {
        storeMapPoint = MapPoint.builder()
                .address("15, Калинова вулиця, Фастів, Фастівська міська громада, Фастівський район, Київська область, 08500, Україна")
                .latitude(50.08340335)
                .longitude(29.885050630832627)
                .status(KNOWN)
                .build();

        mapPointAutovalidatedFirst = MapPoint.builder()
                .address("вулиця Єлизавети Чавдар, 36, Київ, Україна, 02000")
                .latitude(50.3910679)
                .longitude(30.6265536)
                .status(MapPoint.AddressStatus.AUTOVALIDATED)
                .build();

        mapPointAutovalidatedSecond = MapPoint.builder()
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
                .coefficient(1.0)
                .measure("кг")
                .build();

        orderDtoFirst = OrderDto.builder()
                .id(0)
                .refKey("43cd2b8a-84c0-11ec-b3ce-00155dd72305")
                .orderNumber("00000002535")
                .createdDate(LocalDate.of(2022, 2, 3))
                .comment("comment")
                .managerFullName("Шевцова Галина")
                .isFrozen(false)
                .deliveryStart(null)
                .deliveryFinish(null)
                .clientName("Кух Плюс ТОВ (Кухмайстер) бн")
                .address("вул.Єлізавети Чавдар, буд.36")
                .products(List.of(product))
                .orderWeight(12.00)
                .build();

        orderDtoSecond = OrderDto.builder()
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

        MapPoint mapPointFirst = MapPoint.builder()
                .longitude(30.5339629)
                .latitude(50.4782535)
                .address("м. Київ, вул. Електриків 29А")
                .status(MapPoint.AddressStatus.KNOWN)
                .build();

        MapPoint mapPointSecond = MapPoint.builder()
                .longitude(30.603752)
                .latitude(50.4439883)
                .address("м.Київ, вул.Туманяна,15-А")
                .status(MapPoint.AddressStatus.KNOWN)
                .build();

        MapPoint mapPointThird = MapPoint.builder()
                .longitude(30.4936555)
                .latitude(50.4895138)
                .address("м.Київ,пр-т Бандери,21")
                .status(MapPoint.AddressStatus.KNOWN)
                .build();

        MapPoint mapPointFourth = MapPoint.builder()
                .longitude(80.4936555)
                .latitude(50.4895138)
                .address("м.Київ,пр-т Бандери,142")
                .status(MapPoint.AddressStatus.KNOWN)
                .build();

        RoutePoint.OrderReference orderReference = RoutePoint.OrderReference.builder()
                .id(1)
                .comment("бн")
                .orderTotalWeight(1500.35)
                .orderNumber("145366")
                .build();

        RoutePoint routePointFirst = RoutePoint.builder()
                .id(UUID.fromString("8a5dd1be-7584-4a6e-9981-f5c9e7cfae58"))
                .status(RoutePointStatus.PENDING)
                .completedAt(null)
                .clientName("Очеретня І.П.ФОП")
                .address("м. Київ, вул. Електриків 29А")
                .addressTotalWeight(24.0)
                .routePointDistantTime(0)
                .mapPoint(mapPointFirst)
                .orders(List.of(orderReference))
                .build();

        RoutePoint routePointSecond = RoutePoint.builder()
                .id(UUID.fromString("67fe44ab-78c2-4bf2-9a1d-f3551c9caec7"))
                .status(RoutePointStatus.PENDING)
                .completedAt(null)
                .clientName("Кендібуфет ТОВ")
                .address("м.Київ, вул.Туманяна,15-А")
                .addressTotalWeight(20.0)
                .routePointDistantTime(0)
                .mapPoint(mapPointSecond)
                .orders(List.of(orderReference))
                .build();

        RoutePoint routePointThird = RoutePoint.builder()
                .id(UUID.fromString("96b4f747-4ce7-40ca-a728-3183ca103d2f"))
                .status(RoutePointStatus.PENDING)
                .completedAt(null)
                .clientName("Фоззі-Фуд ТОВ")
                .address("м.Київ,пр-т Бандери,21")
                .addressTotalWeight(18.0)
                .routePointDistantTime(0)
                .orders(List.of(orderReference))
                .mapPoint(mapPointThird)
                .build();

        RoutePoint routePointFourth = RoutePoint.builder()
                .id(UUID.fromString("f6f73d76-8005-11ec-b3ce-00155dd72305"))
                .status(RoutePointStatus.DONE)
                .completedAt(LocalDateTime.now())
                .clientName("Кух Плюс ТОВ")
                .address("м.Київ,пр-т Бандери,142")
                .addressTotalWeight(18.0)
                .routePointDistantTime(100)
                .orders(List.of(orderReference))
                .mapPoint(mapPointFourth)
                .build();

        routePoints.add(routePointFirst);
        routePoints.add(routePointSecond);
        routePoints.add(routePointThird);

        incorrectRoutePoints.add(routePointFirst);
        incorrectRoutePoints.add(routePointFourth);
    }

    @Test
    @DataSet(value = "delivery/get_delivery.yml", skipCleaningFor = "flyway_schema_history",
            cleanAfter = true, cleanBefore = true)
    @DisplayName("when Get Delivery then OK status returned")
    void whenGetDeliveryById_thenOkStatusReturned() throws Exception {

        mockMvc.perform(get("/api/v1/deliveries/123e4567-e89b-12d3-a456-556642440000")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content()
                        .json(getResponseAsString("datasets/delivery/delivery-dataset.json")));
    }

    @Test
    @DataSet(value = "delivery/get_delivery.yml", skipCleaningFor = "flyway_schema_history",
            cleanAfter = true, cleanBefore = true)
    @DisplayName("when Get Deliveries then OK status returned")
    void whenGetDeliveries_thenOkStatusReturned() throws Exception {

        mockMvc.perform(get("/api/v1/deliveries")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content()
                        .json(getResponseAsString("datasets/delivery/short-deliveries.json")));
    }

    @Test
    @DataSet(value = "delivery/delivery.yml", skipCleaningFor = "flyway_schema_history",
            cleanAfter = true, cleanBefore = true)
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
    @DataSet(value = "delivery/delivery.yml", skipCleaningFor = "flyway_schema_history",
            cleanAfter = true, cleanBefore = true)
    @ExpectedDataSet(value = "delivery/update_delivery.yml")
    @DisplayName("when Update Delivery then Ok Status Returned")
    void whenUpdateDelivery_thenOkStatusReturned() throws Exception {

        DeliveryDto deliveryDto = DeliveryDto.builder()
                .deliveryDate(LocalDate.parse("2000-01-01"))
                .status(DeliveryStatus.APPROVED)
                .build();

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/deliveries/123e4567-e89b-12d3-a456-556642440000")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(deliveryDto)))
                .andExpect(status().isOk());
    }

    @Test
    @DataSet(value = "delivery/delivery.yml", skipCleaningFor = "flyway_schema_history",
            cleanAfter = true, cleanBefore = true)
    @ExpectedDataSet(value = "delivery/approve_delivery.yml")
    @DisplayName("when Approve Delivery then Ok Status Returned")
    void whenApproveDelivery_thenOkStatusReturned() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/deliveries/123e4567-e89b-12d3-a456-556642440000/approve"))
                .andExpect(status().isOk());
    }

    @Test
    @DataSet(value = "delivery/delivery.yml", skipCleaningFor = "flyway_schema_history",
            cleanAfter = true, cleanBefore = true)
    @DisplayName("when Approve Delivery then Correct DeliveryAndRoutesStatusDto Returned")
    void whenApproveDelivery_thenCorrectDeliveryAndRoutesStatusDtoReturned() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/deliveries/123e4567-e89b-12d3-a456-556642440000/approve"))
                .andExpect(status().isOk())
                .andExpect(content()
                        .json(getResponseAsString("datasets/delivery/delivery-and-routes-status.json")));

    }

    @Test
    @DataSet(value = "delivery/delivery.yml", skipCleaningFor = "flyway_schema_history",
            cleanAfter = true, cleanBefore = true)
    @DisplayName("when Approve Delivery By Non-Existing Id then Not Found Returned")
    void whenApproveDeliveryByNonExistingId_thenNotFoundReturned() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/deliveries/00000000-0000-0000-0000-000000000000/approve"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DataSet(value = "delivery/approve_delivery.yml")
    @DisplayName("when Approve Delivery of Non-Draft Status then Exception Thrown")
    void whenApproveDeliveryOfNonDraftStatus_thenExceptionThrown() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/deliveries/123e4567-e89b-12d3-a456-556642440000/approve"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DataSet(value = "delivery/delivery.yml")
    @DisplayName("when Approve Delivery without Routes then Exception Thrown")
    void whenApproveDeliveryWithoutRoutes_thenExceptionThrown() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/deliveries/123e4567-e89b-12d3-a456-556642440001/approve"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DataSet(value = "delivery/add_delivery.yml", skipCleaningFor = "flyway_schema_history",
            cleanAfter = true, cleanBefore = true)
    @ExpectedDataSet(value = "delivery/delete_delivery.yml")
    @DisplayName("when Delete Delivery then Ok Status Returned")
    void whenDeleteDelivery_thenOkStatusReturned() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/api/v1/deliveries/f8c3de3d-1fea-4d7c-a8b0-29f63c4c3454")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DataSet(value = "delivery/delivery.yml", skipCleaningFor = "flyway_schema_history",
            cleanAfter = true, cleanBefore = true)
    @DisplayName("when Delete Delivery By Non-Existing Id then Not Found Returned")
    void whenDeleteDelivery_byNonExistingId_thenNotFoundReturned() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/api/v1/deliveries/237e9877-e79b-12d4-a765-321741963012")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DataSet(value = "delivery/delivery.yml", skipCleaningFor = "flyway_schema_history",
            cleanAfter = true, cleanBefore = true)
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
        Route route = objectMapper.readValue(getClass().getClassLoader().getResource(MOCKED_ROUTE), Route.class);

        VRPSolution regularVrpSolution = new VRPSolution();
        regularVrpSolution.setRoutePoints(route.getRoutePoints());
        regularVrpSolution.setCar(route.getCar());
        when(vrpSolver.optimize(Collections.emptyList(), storeMapPoint, Collections.emptyList())).thenReturn(Collections.emptyList());
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
        Route route = objectMapper.readValue(getClass().getClassLoader().getResource(MOCKED_ROUTE), Route.class);

        VRPSolution regularVrpSolution = new VRPSolution();
        regularVrpSolution.setRoutePoints(route.getRoutePoints());
        regularVrpSolution.setCar(route.getCar());
        when(vrpSolver.optimize(Collections.emptyList(), storeMapPoint, Collections.emptyList())).thenReturn(Collections.emptyList());
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
    @DataSet(value = "delivery/delivery.yml", skipCleaningFor = "flyway_schema_history",
            cleanAfter = true, cleanBefore = true)
    @ExpectedDataSet(value = "delivery/save_orders.yml")
    @DisplayName("when save orders with autovalidated map point status " +
            "and one of them with already existing address in cache " +
            "then Ok status returned")
    void whenSaveOrdersWithAutovalidatedMapPointStatus_andOneOfThemWithAlreadyExistingAddressInCache_thenOkStatusReturned() throws Exception {

        orderDtoFirst.setMapPoint(mapPointAutovalidatedFirst);
        orderDtoSecond.setMapPoint(mapPointAutovalidatedSecond);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/deliveries/123e4567-e89b-12d3-a456-556642440001/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(List.of(orderDtoFirst, orderDtoSecond))))
                .andExpect(status().isOk());
    }

    @Test
    @DataSet(value = "delivery/delivery.yml", skipCleaningFor = "flyway_schema_history",
            cleanAfter = true, cleanBefore = true)
    @ExpectedDataSet(value = "delivery/save_orders_map_point_has_known_status.yml", ignoreCols = {"id"})
    @DisplayName("when save orders with known map point status then Ok status returned")
    void whenSaveOrdersWithKnownMapPointStatus_thenOkStatusReturned() throws Exception {

        orderDtoSecond.setMapPoint(mapPointKnown);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/deliveries/123e4567-e89b-12d3-a456-556642440001/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(List.of(orderDtoSecond))))
                .andExpect(status().isOk());
    }

    @Test
    @DataSet(value = "delivery/delivery.yml", skipCleaningFor = "flyway_schema_history",
            cleanAfter = true, cleanBefore = true)
    @ExpectedDataSet(value = "delivery/delivery.yml")
    @DisplayName("when save orders and non-existing delivery id is passed then not found status returned")
    void whenSaveOrdersAndNonExistingDeliveryIdIsPassed_thenNotFoundReturned() throws Exception {

        orderDtoSecond.setMapPoint(mapPointAutovalidatedSecond);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/deliveries/123e4567-e89b-12d3-a456-556642440005/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(List.of(orderDtoSecond))))
                .andExpect(status().isNotFound());
    }

    @Test
    @DataSet(value = "delivery/delivery.yml", skipCleaningFor = "flyway_schema_history",
            cleanAfter = true, cleanBefore = true)
    @ExpectedDataSet(value = "delivery/delivery.yml")
    @DisplayName("when save orders with at least one unknown address provided then not found status returned")
    void whenSaveOrdersWithAtLeastOneUnknownAddressProvided_thenNotFoundReturned() throws Exception {

        orderDtoSecond.setMapPoint(mapPointUnknown);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/deliveries/123e4567-e89b-12d3-a456-556642440001/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(List.of(orderDtoSecond))))
                .andExpect(status().isNotFound());
    }

    // TODO fix RoutePoint matching. At th emoment due to reordering/completedAt/etc results doesn't match
    @Test
    @DataSet(value = "common/recalculate_route/dataset_routes.yml", skipCleaningFor = "flyway_schema_history",
            cleanAfter = true, cleanBefore = true)
    @ExpectedDataSet(value = "datasets/common/recalculate_route/dataset_updated_routes.yml", ignoreCols = "ROUTE_POINTS")
    @DisplayName("when Reorder Route then Ok Status Returned")
    void whenReorderRoute_thenOkStatusReturned() throws Exception {

        when(graphhopperService.getRoute(anyList())).thenReturn(graphhopperResponse);
        when(graphhopperResponse.getDistance()).thenReturn(42000.0);
        when(graphhopperResponse.getTime()).thenReturn(4800000L);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/deliveries/" +
                                "123e4567-e89b-12d3-a456-556642440000/routes/1/reorder")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(routePoints)))
                .andExpect(status().isOk());
    }

    @Test
    @DataSet(value = "common/recalculate_route/dataset_routes.yml",
            cleanAfter = true, cleanBefore = true, skipCleaningFor = "flyway_schema_history")
    @DisplayName("when Reorder Route with Not Existing DeliveryId then Not Found Return")
    void whenReorderRoute_withNotExistingDeliveryId_thenNotFoundReturn() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/deliveries/" +
                                "123e4567-e89b-12d3-a456-556642440035/routes/1/reorder")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(routePoints)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DataSet(value = "common/recalculate_route/dataset_routes.yml",
            cleanAfter = true, cleanBefore = true, skipCleaningFor = "flyway_schema_history")
    @DisplayName("when Reorder Route with Not Existing Route Id then Not Found Return")
    void whenReorderRoute_withNotExistingRouteId_thenNotFoundReturn() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/deliveries/" +
                                "123e4567-e89b-12d3-a456-556642440034/routes/10/reorder")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(routePoints)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DataSet(value = "common/recalculate_route/dataset_routes.yml",
            cleanAfter = true, cleanBefore = true, skipCleaningFor = "flyway_schema_history")
    @DisplayName("when Reorder Route with Incorrect DeliveryId To RouteId then Not Found Return")
    void whenReorderRoute_withIncorrectDeliveryIdToRouteId_thenNotFoundReturn() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/deliveries/" +
                                "123e4567-e89b-12d3-a456-556642440000/routes/2/reorder")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(routePoints)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DataSet(value = "common/recalculate_route/dataset_routes.yml",
            cleanAfter = true, cleanBefore = true, skipCleaningFor = "flyway_schema_history")
    @DisplayName("when Reorder Route with Incorrect Route Status then Exception Thrown")
    void whenReorderRoute_withIncorrectRouteStatus_thenExceptionThrown() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/deliveries/" +
                                "123e4567-e89b-12d3-a456-556642440001/routes/2/reorder")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(routePoints)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DataSet(value = "common/recalculate_route/dataset_routes.yml", skipCleaningFor = "flyway_schema_history",
            cleanAfter = true, cleanBefore = true)
    @DisplayName("when Reorder Route with Incorrect Delivery Status then Exception Thrown")
    void whenReorderRoute_withIncorrectDeliveryStatus_thenExceptionThrown() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/deliveries/" +
                                "f8c3de3d-1fea-4d7c-a8b0-29f63c4c3454/routes/3/reorder")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(routePoints)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DataSet(value = "datasets/common/recalculate_route/dataset_routes.yml",
            cleanAfter = true, cleanBefore = true, skipCleaningFor = "flyway_schema_history")
    @DisplayName("when Reorder Route Route with Incorrect RoutePointStatus then Exception Thrown")
    void whenReorderRoute_withIncorrectRoutePointStatus_thenExceptionThrown() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/deliveries/" +
                                "125e4567-e89b-12d3-a456-556642440005/routes/4/reorder")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(incorrectRoutePoints)))
                .andExpect(status().isBadRequest());
    }
}