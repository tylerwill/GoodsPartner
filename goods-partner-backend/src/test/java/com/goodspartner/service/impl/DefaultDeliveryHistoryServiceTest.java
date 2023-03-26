package com.goodspartner.service.impl;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.spring.api.DBRider;
import com.goodspartner.AbstractWebITest;
import com.goodspartner.config.TestSecurityDisableConfig;
import com.goodspartner.dto.DeliveryDto;
import com.goodspartner.entity.DeliveryStatus;
import com.goodspartner.entity.Route;
import com.goodspartner.event.DeliveryAuditEvent;
import com.goodspartner.facade.DeliveryFacade;
import com.goodspartner.repository.CarRepository;
import com.goodspartner.service.DeliveryService;
import com.goodspartner.service.GraphhopperService;
import com.goodspartner.service.RoutePointService;
import com.goodspartner.service.RouteService;
import com.goodspartner.service.StoreService;
import com.goodspartner.service.VRPSolver;
import com.goodspartner.service.dto.RoutingSolution;
import com.goodspartner.service.dto.VRPSolution;
import com.goodspartner.web.action.RouteAction;
import com.goodspartner.web.action.RoutePointAction;
import com.graphhopper.ResponsePath;
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

import static com.goodspartner.web.action.DeliveryAction.APPROVE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Disabled // So far disable
@DBRider
@Import({TestSecurityDisableConfig.class})
@AutoConfigureMockMvc(addFilters = false)
@RecordApplicationEvents
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
    @Autowired
    private RoutePointService routePointService;

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
                .forEach(deliveryAuditEvent -> System.out.println(deliveryAuditEvent.action()));

        assertEquals(1, applicationEvents
                .stream(DeliveryAuditEvent.class)
                .filter(event -> event.action().equals("Анонім Anonymous створив(ла) доставку"))
                .count());
    }

    @Test
    @DataSet(value = "common/delivery/calculate/sqlDump.json", skipCleaningFor = "flyway_schema_history",
            cleanAfter = true, cleanBefore = true,
            executeStatementsBefore = "ALTER SEQUENCE routes_sequence RESTART WITH 50")
    @DisplayName("When Calculate Delivery Then History Created")
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
    @DataSet(value = "datasets/common/delivery_history/initial_routes_and_deliveries.yml",
            cleanAfter = true, cleanBefore = true, skipCleaningFor = "flyway_schema_history")
    @DisplayName("When RoutePoint updated with status completed and Delivery automatically closed then correct History Created")
    public void testWhenRoutePointUpdatedAndDeliveryAutomaticallyClosedThenCorrectHistoryCreated() {

        routePointService.updateRoutePoint(1L, RoutePointAction.COMPLETE);

        assertEquals(1, applicationEvents
                .stream(DeliveryAuditEvent.class)
                .filter(event -> event.action().equals("Доставка переведена в статус виконана"))
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
                .filter(event -> event.action().equals("Доставка переведена в статус виконана"))
                .count());
    }

    @Test
    @DataSet(value = "common/delivery_history/initial_routes_and_deliveries.yml",
            cleanAfter = true, cleanBefore = true, skipCleaningFor = "flyway_schema_history")
    @DisplayName("When RoutePoint updated then correct History Created")
    public void testWhenRoutePointUpdatedThenCorrectHistoryCreated() {
        routePointService.updateRoutePoint(1L, RoutePointAction.COMPLETE);

        assertEquals(1, applicationEvents
                .stream(DeliveryAuditEvent.class)
                .filter(event -> event.action().equals("Анонім Anonymous змінив(ла) статус точки маршрута до авто Mercedes Sprinter AA 1111 CT, " +
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
                .filter(event -> event.action().equals("Анонім Anonymous змінив(ла) статус маршрута до авто Mercedes Sprinter AA 1111 CT на COMPLETED"))
                .count());
    }

    @Test
    @DataSet(value = "common/delivery_history/initial_routes_and_deliveries.yml",
            cleanAfter = true, cleanBefore = true, skipCleaningFor = "flyway_schema_history")
    @DisplayName("When RoutePoint updated with status completed and Route automatically closed then correct History Created")
    public void testWhenRoutePointUpdatedAndRouteAutomaticallyClosedThenCorrectHistoryCreated() {

        routePointService.updateRoutePoint(2L, RoutePointAction.COMPLETE);

        assertEquals(1, applicationEvents
                .stream(DeliveryAuditEvent.class)
                .filter(event -> event.action().equals("Змінився статус маршрута до авто MAN AA 2455 CT на COMPLETED"))
                .count());
    }

    @Test
    @DataSet(value = "common/delivery_history/initial_routes_and_deliveries.yml",
            cleanAfter = true, cleanBefore = true, skipCleaningFor = "flyway_schema_history")
    @DisplayName("When route started Then Correct History Created")
    public void testRouteStarted_thenCorrectHistoryCreated() {

        routeService.updateRoute(2, RouteAction.START);

        assertEquals(1, applicationEvents
                .stream(DeliveryAuditEvent.class)
                .filter(event -> event.action().equals("Анонім Anonymous розпочав(ла) маршрут до авто MAN AA 2455 CT"))
                .count());
    }

    @Test
    @DataSet(value = "datasets/common/delivery_history/initial_routes_and_deliveries.yml",
            cleanAfter = true, cleanBefore = true, skipCleaningFor = "flyway_schema_history")
    @DisplayName("when Approve Delivery then History On Delivery And Routes Status Update Created")
    void whenApproveDelivery_thenHistoryOnDeliveryAndRoutesStatusUpdateCreated() {

        UUID deliveryId = UUID.fromString("d0000000-0000-0000-0000-000000000001");

        deliveryFacade.approve(deliveryId, APPROVE);

        assertEquals(1, applicationEvents
                .stream(DeliveryAuditEvent.class)
                .filter(event -> event.action().equals("Анонім Anonymous підтвердив(ла) доставку"))
                .count());

        assertEquals(1, applicationEvents.stream(DeliveryAuditEvent.class)
                .filter(event -> event.action().equals("Змінився статус маршрута до авто Mercedes Sprinter AA 1111 CT на APPROVED"))
                .count());
    }

}