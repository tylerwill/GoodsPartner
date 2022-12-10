package com.goodspartner.web.controller.delivery;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.spring.api.DBRider;
import com.goodspartner.AbstractWebITest;
import com.goodspartner.config.TestSecurityDisableConfig;
import com.goodspartner.dto.DeliveryDto;
import com.goodspartner.dto.MapPoint;
import com.goodspartner.dto.OrderDto;
import com.goodspartner.dto.Product;
import com.goodspartner.entity.AddressStatus;
import com.goodspartner.entity.DeliveryStatus;
import com.goodspartner.entity.DeliveryType;
import com.goodspartner.entity.Route;
import com.goodspartner.entity.RoutePoint;
import com.goodspartner.repository.CarRepository;
import com.goodspartner.service.GraphhopperService;
import com.goodspartner.service.StoreService;
import com.goodspartner.service.VRPSolver;
import com.goodspartner.service.dto.RoutingSolution;
import com.goodspartner.service.dto.VRPSolution;
import com.graphhopper.ResponsePath;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
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
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DBRider
@Import({TestSecurityDisableConfig.class})
@AutoConfigureMockMvc(addFilters = false)
@TestInstance(PER_CLASS)
@Disabled
// TODO rework after moving RoutePoints to separate table
class DeliveryControllerITest extends AbstractWebITest {

    // TODO require further refactoring
    private static final String MOCKED_DELIVERY_DTO = "response/delivery/delivery-calculated-response.json";

    private static final String MOCKED_ROUTE_NEW = "mock/route/mocked-route-entity.json";
    private static final String RESPONSE_DELIVERY_DROPPED_POINTS = "response/delivery/delivery_with_dropped_order.json";

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
                .status(AddressStatus.KNOWN)
                .build();

        mapPointAutovalidatedFirst = MapPoint.builder()
                .address("вулиця Єлизавети Чавдар, 36, Київ, Україна, 02000")
                .latitude(50.3910679)
                .longitude(30.6265536)
                .status(AddressStatus.AUTOVALIDATED)
                .build();

        mapPointAutovalidatedSecond = MapPoint.builder()
                .address("16B, вулиця Княжий Затон, 16Б, Київ, Україна, 02000")
                .latitude(50.403193)
                .longitude(30.6163764)
                .status(AddressStatus.AUTOVALIDATED)
                .build();

        mapPointKnown = MapPoint.builder()
                .address("16B, вулиця Княжий Затон, 16Б, Київ, Україна, 02000")
                .latitude(50.403193)
                .longitude(30.6163764)
                .status(AddressStatus.KNOWN)
                .build();

        mapPointUnknown = MapPoint.builder()
                .status(AddressStatus.UNKNOWN)
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
                .shippingDate(LocalDate.of(2022, 2, 3))
                .comment("comment")
                .managerFullName("Шевцова Галина")
                .frozen(false)
                .deliveryType(DeliveryType.REGULAR)
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
                .shippingDate(LocalDate.of(2022, 1, 31))
                .comment("нал без оплати  0675438404 Світлана")
                .managerFullName("Шевцова Галина")
                .frozen(false)
                .deliveryType(DeliveryType.REGULAR)
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
                .status(AddressStatus.KNOWN)
                .build();

        MapPoint mapPointSecond = MapPoint.builder()
                .longitude(30.603752)
                .latitude(50.4439883)
                .address("м.Київ, вул.Туманяна,15-А")
                .status(AddressStatus.KNOWN)
                .build();

        MapPoint mapPointThird = MapPoint.builder()
                .longitude(30.4936555)
                .latitude(50.4895138)
                .address("м.Київ,пр-т Бандери,21")
                .status(AddressStatus.KNOWN)
                .build();

        MapPoint mapPointFourth = MapPoint.builder()
                .longitude(80.4936555)
                .latitude(50.4895138)
                .address("м.Київ,пр-т Бандери,142")
                .status(AddressStatus.KNOWN)
                .build();

        /*RoutePoint.OrderReference orderReference = RoutePoint.OrderReference.builder()
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
        incorrectRoutePoints.add(routePointFourth);*/
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
    @Disabled("Due to changed delivery flow, Refactor")
    void whenCalculateDelivery_withCorrectId_thenDeliveryDtoReturn() throws Exception {
        Route route = objectMapper.readValue(getClass().getClassLoader().getResource(MOCKED_ROUTE_NEW), Route.class);

        RoutingSolution regularRoutingSolution = RoutingSolution.builder()
                .routePoints(route.getRoutePoints())
                .car(route.getCar())
                .build();
        VRPSolution vrpSolution = VRPSolution.builder()
                .routings(List.of(regularRoutingSolution))
                .build();
        VRPSolution emptySolution = VRPSolution.builder().build();

        when(vrpSolver.optimize(Collections.emptyList(), storeService.getMainStore(), Collections.emptyList())).thenReturn(emptySolution);
        when(vrpSolver.optimize(
                AdditionalMatchers.not(ArgumentMatchers.eq(Collections.emptyList())),
                any(),
                AdditionalMatchers.not(ArgumentMatchers.eq(Collections.emptyList()))))
                .thenReturn(vrpSolution);

        when(graphhopperService.getRoute(anyList())).thenReturn(graphhopperResponse);
        when(graphhopperResponse.getDistance()).thenReturn(80000.0);
        when(graphhopperResponse.getTime()).thenReturn(4800000L);

        when(carRepository.findByAvailableTrueAndCoolerIs(false)).thenReturn(List.of(route.getCar()));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/deliveries/70574dfd-48a3-40c7-8b0c-3e5defe7d080/calculate")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isAccepted())
                .andExpect(content().json(getResponseAsString(MOCKED_DELIVERY_DTO)));
    }

    @Test
    @DataSet(value = "common/delivery/calculate/sqlDump.json", skipCleaningFor = "flyway_schema_history",
            cleanAfter = true, cleanBefore = true,
            executeStatementsBefore = "ALTER SEQUENCE routes_sequence RESTART WITH 50")
    @DisplayName("when Calculate Delivery With Correct Id then DeliveryDto Return")
    @Disabled("Due to changed delivery flow, Refactor")
    void whenCalculateDelivery_withCorrectId_thenDroppedPointsFound() throws Exception {

        UUID includedRoutePoint = UUID.fromString("1a3f567f-ed97-43b5-be8e-cc644172aad1");
        UUID droppedRoutePoint = UUID.fromString("8acc3bd5-3d98-48f9-82f9-24360659dcb8");
        Route route = objectMapper.readValue(getClass().getClassLoader().getResource(MOCKED_ROUTE_NEW), Route.class);

        Map<Long, RoutePoint> routePointMap = route.getRoutePoints().stream()
                .collect(Collectors.toMap(RoutePoint::getId, Function.identity()));

        RoutingSolution regularRoutingSolution = RoutingSolution.builder()
                .routePoints(List.of(routePointMap.get(includedRoutePoint)))
                .car(route.getCar())
                .build();
        VRPSolution vrpSolution = VRPSolution.builder()
                .routings(List.of(regularRoutingSolution))
                .droppedPoints(List.of(routePointMap.get(droppedRoutePoint)))
                .build();
        VRPSolution emptySolution = VRPSolution.builder().build();

        when(vrpSolver.optimize(Collections.emptyList(), storeService.getMainStore(), Collections.emptyList())).thenReturn(emptySolution);
        when(vrpSolver.optimize(
                AdditionalMatchers.not(ArgumentMatchers.eq(Collections.emptyList())),
                any(),
                AdditionalMatchers.not(ArgumentMatchers.eq(Collections.emptyList()))))
                .thenReturn(vrpSolution);

        when(graphhopperService.getRoute(anyList())).thenReturn(graphhopperResponse);
        when(graphhopperResponse.getDistance()).thenReturn(80000.0);
        when(graphhopperResponse.getTime()).thenReturn(4800000L);

        when(carRepository.findByAvailableTrueAndCoolerIs(false)).thenReturn(List.of(route.getCar()));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/deliveries/70574dfd-48a3-40c7-8b0c-3e5defe7d080/calculate")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isAccepted())
                .andExpect(content().json(getResponseAsString(RESPONSE_DELIVERY_DROPPED_POINTS)));
    }

    @Test
    @DataSet(value = "common/delivery/calculate/sqlDump.json", skipCleaningFor = "flyway_schema_history",
            cleanAfter = true, cleanBefore = true,
            executeStatementsBefore = "ALTER SEQUENCE routes_sequence RESTART WITH 1")
    @DisplayName("when RECalculate Delivery With Correct Id then DeliveryDto Return")
    @Disabled("Due to changed delivery flow, Refactor")
    void whenRECalculateDelivery_withCorrectId_thenDeliveryDtoReturn() throws Exception {
        Route route = objectMapper.readValue(getClass().getClassLoader().getResource(MOCKED_ROUTE_NEW), Route.class);

        RoutingSolution regularRoutingSolution = RoutingSolution.builder()
                .routePoints(route.getRoutePoints())
                .car(route.getCar())
                .build();
        VRPSolution vrpSolution = VRPSolution.builder()
                .routings(List.of(regularRoutingSolution))
                .build();

        when(vrpSolver.optimize(Collections.emptyList(), storeService.getMainStore(), Collections.emptyList())).thenReturn(VRPSolution.builder().build());
        when(vrpSolver.optimize(
                AdditionalMatchers.not(ArgumentMatchers.eq(Collections.emptyList())),
                any(),
                AdditionalMatchers.not(ArgumentMatchers.eq(Collections.emptyList()))))
                .thenReturn(vrpSolution);

        when(graphhopperService.getRoute(anyList())).thenReturn(graphhopperResponse);
        when(graphhopperResponse.getDistance()).thenReturn(80000.0);
        when(graphhopperResponse.getTime()).thenReturn(4800000L);

        when(carRepository.findByAvailableTrueAndCoolerIs(false)).thenReturn(List.of(route.getCar()));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/deliveries/70574dfd-48a3-40c7-8b0c-3e5defe7d080/calculate")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isAccepted())
                .andExpect(content().json(getResponseAsString(MOCKED_DELIVERY_DTO)));
    }

}