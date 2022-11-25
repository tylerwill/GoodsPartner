package com.goodspartner.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.spring.api.DBRider;
import com.goodspartner.AbstractWebITest;
import com.goodspartner.dto.UserDto;
import com.goodspartner.entity.AddressStatus;
import com.goodspartner.entity.User;
import com.goodspartner.facade.DeliveryFacade;
import com.goodspartner.web.action.RouteAction;
import com.goodspartner.config.TestSecurityDisableConfig;
import com.goodspartner.dto.CarDto;
import com.goodspartner.dto.DeliveryDto;
import com.goodspartner.dto.MapPoint;
import com.goodspartner.dto.RouteDto;
import com.goodspartner.dto.StoreDto;
import com.goodspartner.entity.DeliveryStatus;
import com.goodspartner.entity.Route;
import com.goodspartner.entity.RoutePoint;
import com.goodspartner.entity.RouteStatus;
import com.goodspartner.entity.Store;
import com.goodspartner.event.DeliveryAuditEvent;
import com.goodspartner.repository.CarRepository;
import com.goodspartner.service.DeliveryService;
import com.goodspartner.service.GraphhopperService;
import com.goodspartner.service.RouteService;
import com.goodspartner.service.StoreService;
import com.goodspartner.service.VRPSolver;
import com.goodspartner.service.dto.RoutingSolution;
import com.goodspartner.service.dto.VRPSolution;
import com.graphhopper.ResponsePath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.AdditionalMatchers;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static com.goodspartner.entity.AddressStatus.KNOWN;
import static com.goodspartner.web.action.DeliveryAction.APPROVE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DBRider
@Import({TestSecurityDisableConfig.class})
@AutoConfigureMockMvc(addFilters = false)
@RecordApplicationEvents
@Disabled
// TODO rework after moving RoutePoints to separate table
class DefaultDeliveryHistoryServiceTest extends AbstractWebITest {
    private static final String EXPECTED_DELIVERY_RESPONSE = "response/delivery/delivery-calculated-response.json";
    private static final String MOCKED_ROUTE = "mock/route/mocked-route-entity.json";

    @Autowired
    private DeliveryService deliveryService;

    @Autowired
    private DeliveryFacade deliveryFacade;

    @Autowired
    private ApplicationEvents applicationEvents;
    @Autowired
    private RouteService routeService;

    private RoutePoint routePoint;
    private RoutePoint anotherRoutePoint;
    private RouteDto routeDto;
    private MapPoint storeMapPoint;

    @MockBean
    private GraphhopperService graphhopperService;
    @MockBean
    private ResponsePath graphhopperResponse;
    @MockBean
    private VRPSolver vrpSolver;
    @MockBean
    private CarRepository carRepository;
    @MockBean
    private StoreService storeService;

    @BeforeEach
    public void before() throws JsonProcessingException {
        var routePointString = "{\"id\": \"00000000-0000-0000-0000-000000000001\", \"orders\": null, \"status\": \"DONE\", \"address\": \"Хрещатик 1А\", \"clientId\": 0, \"mapPoint\": null, \"clientName\": \"ТОВ ПЕКАРНЯ\", \"completedAt\": null, \"addressTotalWeight\": 0.0, \"routePointDistantTime\": 0}";
        routePoint = objectMapper.readValue(routePointString, RoutePoint.class);

        var anotherRoutePointString = "{\"id\": \"00000000-0000-0000-0000-000000000002\", \"orders\": null, \"status\": \"DONE\", \"address\": null, \"clientId\": 0, \"mapPoint\": null, \"clientName\": null, \"completedAt\": null, \"addressTotalWeight\": 0.0, \"routePointDistantTime\": 0}";
        anotherRoutePoint = objectMapper.readValue(anotherRoutePointString, RoutePoint.class);

        Store store = new Store(UUID.fromString("5688492e-ede4-45d3-923b-5f9773fd3d4b"),
                "Склад №1",
                "15, Калинова вулиця, Фастів, Фастівська міська громада, Фастівський район, Київська область, 08500, Україна",
                50.08340335,
                29.885050630832627);

        when(storeService.getMainStore()).thenReturn(store);

        UserDto userDto = new UserDto(555,
                "Oleg",
                "userEmail@gmail",
                User.UserRole.DRIVER.getName(),
                true);

        CarDto carDto = CarDto.builder()
                .id(1)
                .name("Mercedes Sprinter")
                .licencePlate("AA 1111 CT")
                .available(Boolean.TRUE)
                .driver(userDto)
                .weightCapacity(2500)
                .cooler(Boolean.TRUE)
                .build();

        String storeAddress = "15, Калинова вулиця, Фастів, Фастівська міська громада, Фастівський район, Київська область, 08500, Україна";
        StoreDto storeDto = StoreDto.builder()
                .address(storeAddress)
                .name("Склад №1")
                .mapPoint(MapPoint.builder()
                        .address(storeAddress)
                        .latitude(50.08340335)
                        .longitude(29.885050630832627)
                        .status(KNOWN)
                        .build())
                .build();

        routeDto = new RouteDto();
        routeDto.setCar(carDto);
        routeDto.setStatus(RouteStatus.COMPLETED);
        routeDto.setDistance(0.0);
        routeDto.setStore(storeDto);

        storeMapPoint = MapPoint.builder()
                .status(AddressStatus.KNOWN)
                .address("м. Київ, вул. Некрасова 138")
                .latitude(72.12)
                .longitude(85.15)
                .build();
    }

    @Test
    @DataSet(value = "datasets/common/delivery_history/initial_data.yml",
            cleanAfter = true, cleanBefore = true, skipCleaningFor = "flyway_schema_history")
    @DisplayName("When Create Delivery Then History Created")
    public void testWhenCreateDeliveryThenCorrectHistoryCreated() {
        DeliveryDto deliveryResponse = DeliveryDto.builder()
                .deliveryDate(LocalDate.parse("2022-07-10"))
                .status(DeliveryStatus.DRAFT)
                .build();

        deliveryService.add(deliveryResponse);

        applicationEvents
                .stream(DeliveryAuditEvent.class)
                .forEach(deliveryAuditEvent -> System.out.println(deliveryAuditEvent.getAction()));

        assertEquals(1, applicationEvents
                .stream(DeliveryAuditEvent.class)
                .filter(event -> event.getAction().equals("Анонім Anonymous створив(ла) доставку"))
                .count());
    }

    @Test
    @DataSet(value = "common/delivery/calculate/sqlDump.json", skipCleaningFor = "flyway_schema_history",
            cleanAfter = true, cleanBefore = true,
            executeStatementsBefore = "ALTER SEQUENCE routes_sequence RESTART WITH 50")
    @DisplayName("When Calculate Delivery Then History Created")
    @Disabled("Due to changed delivery flow, Refactor")
    public void testWhenCalculatedDeliveryThenCorrectHistoryCreated() throws Exception {
        Route route = objectMapper.readValue(getClass().getClassLoader().getResource(MOCKED_ROUTE), Route.class);

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
                .andExpect(content().json(getResponseAsString(EXPECTED_DELIVERY_RESPONSE)));
    }

    @Test
    @DataSet(value = "common/delivery_history/initial_routes_and_deliveries.yml",
            cleanAfter = true, cleanBefore = true, skipCleaningFor = "flyway_schema_history")
    @DisplayName("When RoutePoint updated with status completed and Delivery automatically closed then correct History Created")
    @Disabled
    public void testWhenRoutePointUpdatedAndDeliveryAutomaticallyClosedThenCorrectHistoryCreated() {

//        UUID uuid = UUID.fromString("00000000-0000-0000-0000-000000000001");
//        routeService.updateRoutePoint(1, 1L, RoutePointAction.COMPLETE);

        assertEquals(1, applicationEvents
                .stream(DeliveryAuditEvent.class)
                .filter(event -> event.getAction().equals("Доставка переведена в статус виконана"))
                .count());
    }

    @Test
    @DataSet(value = "common/delivery_history/initial_routes_and_deliveries.yml",
            cleanAfter = true, cleanBefore = true, skipCleaningFor = "flyway_schema_history")
    @DisplayName("When update route with status completed and Delivery automatically closed then correct History Created")
    public void testUpdateRouteWithStatusCompletedAndDeliveryAutomaticallyClosedThenCorrectHistoryCreated() {
        routeService.updateRoute(5, RouteAction.COMPLETE);

        assertEquals(1, applicationEvents
                .stream(DeliveryAuditEvent.class)
                .filter(event -> event.getAction().equals("Доставка переведена в статус виконана"))
                .count());
    }

    @Test
    @DataSet(value = "common/delivery_history/initial_routes_and_deliveries.yml",
            cleanAfter = true, cleanBefore = true, skipCleaningFor = "flyway_schema_history")
    @DisplayName("When RoutePoint updated then correct History Created")
    @Disabled
    public void testWhenRoutePointUpdatedThenCorrectHistoryCreated() {
//        UUID routePointUUID = UUID.fromString("00000000-0000-0000-0000-000000000001");
//        routeService.updateRoutePoint(1, 1L, RoutePointAction.COMPLETE);

        assertEquals(1, applicationEvents
                .stream(DeliveryAuditEvent.class)
                .filter(event -> event.getAction().equals("Анонім Anonymous змінив(ла) статус точки маршрута до авто Mercedes Sprinter AA 1111 CT, " +
                        "клієнт ТОВ ПЕКАРНЯ, адреса Хрещатик 1А на DONE"))
                .count());
    }

    @Test
    @DataSet(value = "common/delivery_history/initial_routes_and_deliveries.yml",
            cleanAfter = true, cleanBefore = true, skipCleaningFor = "flyway_schema_history")
    @DisplayName("When route status Updated Then Correct History Created")
    public void testRouteStatusUpdated_thenCorrectHistoryCreated() {

        routeService.updateRoute(5, RouteAction.COMPLETE);

        assertEquals(1, applicationEvents
                .stream(DeliveryAuditEvent.class)
                .filter(event -> event.getAction().equals("Анонім Anonymous змінив(ла) статус маршрута до авто Mercedes Sprinter AA 1111 CT на COMPLETED"))
                .count());
    }

    @Test
    @DataSet(value = "common/delivery_history/initial_routes_and_deliveries.yml",
            cleanAfter = true, cleanBefore = true, skipCleaningFor = "flyway_schema_history")
    @DisplayName("When RoutePoint updated with status completed and Route automatically closed then correct History Created")
    @Disabled
    public void testWhenRoutePointUpdatedAndRouteAutomaticallyClosedThenCorrectHistoryCreated() {

//        UUID routeId = UUID.fromString("00000000-0000-0000-0000-000000000001");
//        routeService.updateRoutePoint(2, 2L, RoutePointAction.COMPLETE);

        assertEquals(1, applicationEvents
                .stream(DeliveryAuditEvent.class)
                .filter(event -> event.getAction().equals("Змінився статус маршрута до авто MAN AA 2455 CT на COMPLETED"))
                .count());
    }

    @Test
    @Disabled
    @DataSet(value = "common/delivery_history/initial_routes_and_deliveries.yml",
            cleanAfter = true, cleanBefore = true, skipCleaningFor = "flyway_schema_history")
    @DisplayName("When route started Then Correct History Created")
    public void testRouteStarted_thenCorrectHistoryCreated() {
        routeDto.setId(5);
        routeDto.setStatus(RouteStatus.INPROGRESS);
//        routeDto.setRoutePointDtos(List.of(routePoint));

        routeService.updateRoute(2, RouteAction.START);

        assertEquals(1, applicationEvents
                .stream(DeliveryAuditEvent.class)
                .filter(event -> event.getAction().equals("Анонім Anonymous розпочав(ла) маршрут до авто MAN AA 2455 CT"))
                .count());
    }

    @Test
    @DataSet(value = "common/delivery_history/initial_routes_and_deliveries.yml",
            cleanAfter = true, cleanBefore = true, skipCleaningFor = "flyway_schema_history")
    @DisplayName("when Approve Delivery then History On Delivery And Routes Status Update Created")
    void whenApproveDelivery_thenHistoryOnDeliveryAndRoutesStatusUpdateCreated() {

        UUID deliveryId = UUID.fromString("d0000000-0000-0000-0000-000000000001");

        deliveryFacade.approve(deliveryId, APPROVE);

        assertEquals(1, applicationEvents
                .stream(DeliveryAuditEvent.class)
                .filter(event -> event.getAction().equals("Анонім Anonymous підтвердив(ла) доставку"))
                .count());

        assertEquals(1, applicationEvents.stream(DeliveryAuditEvent.class)
                .filter(event -> event.getAction().equals("Змінився статус маршрута до авто Mercedes Sprinter AA 1111 CT на APPROVED"))
                .count());
    }

}