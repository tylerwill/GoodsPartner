package com.goodspartner.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import com.github.database.rider.spring.api.DBRider;
import com.goodspartner.AbstractBaseITest;
import com.goodspartner.dto.CarDto;
import com.goodspartner.dto.MapPoint;
import com.goodspartner.dto.RouteDto;
import com.goodspartner.dto.StoreDto;
import com.goodspartner.entity.RoutePoint;
import com.goodspartner.entity.RouteStatus;
import com.goodspartner.service.RouteService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static com.goodspartner.dto.MapPoint.AddressStatus.KNOWN;

@DBRider
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DefaultRouteServiceITest extends AbstractBaseITest {

    @Autowired
    private RouteService routeService;

    private RoutePoint routePoint;
    private RoutePoint anotherRoutePoint;
    private RouteDto routeDto;

    @BeforeAll
    public void before() throws JsonProcessingException {
        var routePointString = "{\"id\": \"00000000-0000-0000-0000-000000000001\", \"orders\": null, \"status\": \"DONE\", \"address\": null, \"clientId\": 0, \"mapPoint\": null, \"clientName\": null, \"completedAt\": null, \"addressTotalWeight\": 0.0, \"routePointDistantTime\": 0}";
        routePoint = objectMapper.readValue(routePointString, RoutePoint.class);

        var anotherRoutePointString = "{\"id\": \"00000000-0000-0000-0000-000000000002\", \"orders\": null, \"status\": \"DONE\", \"address\": null, \"clientId\": 0, \"mapPoint\": null, \"clientName\": null, \"completedAt\": null, \"addressTotalWeight\": 0.0, \"routePointDistantTime\": 0}";
        anotherRoutePoint = objectMapper.readValue(anotherRoutePointString, RoutePoint.class);

        CarDto carDto = CarDto.builder()
                .id(1)
                .name("Mercedes Sprinter")
                .licencePlate("AA 1111 CT")
                .available(Boolean.TRUE)
                .driver("Oleg Dudka")
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
    }

    @Test
    @DataSet(value = "common/close_delivery/initial_routes_and_deliveries.yml",
            cleanAfter = true, cleanBefore = true, skipCleaningFor = "flyway_schema_history")
    @ExpectedDataSet("common/close_delivery/route_and_delivery_automatically_closed.yml")
    @DisplayName("When update routePoint then Route and Delivery should be automatically closed")
    public void testUpdatePoint_thenRouteAndDeliveryShouldBeAutomaticallyClosed() {
        routeService.updatePoint(1, "00000000-0000-0000-0000-000000000001", routePoint);
    }

    @Test
    @DataSet(value = "common/close_delivery/initial_routes_and_deliveries.yml",
            cleanAfter = true, cleanBefore = true, skipCleaningFor = "flyway_schema_history")
    @ExpectedDataSet("common/close_delivery/route_automatically_closed.yml")
    @DisplayName("When update routePoint then Route should be automatically closed")
    public void testUpdatePoint_thenRouteShouldBeAutomaticallyClosed() {
        routeService.updatePoint(2, "00000000-0000-0000-0000-000000000001", routePoint);
    }

    @Test
    @DataSet(value = "common/close_delivery/initial_routes_and_deliveries.yml",
            cleanAfter = true, cleanBefore = true, skipCleaningFor = "flyway_schema_history")
    @ExpectedDataSet("common/close_delivery/route_point_closed_only.yml")
    @DisplayName("When update routePoint then routePoint should be in status Done")
    public void testUpdatePoint_thenRoutePointShouldBeDone() {
        routeService.updatePoint(3, "00000000-0000-0000-0000-000000000001", routePoint);
    }

    @Test
    @DataSet(value = "common/close_delivery/initial_routes_and_deliveries.yml",
            cleanAfter = true, cleanBefore = true, skipCleaningFor = "flyway_schema_history")
    @ExpectedDataSet("common/close_delivery/update_and_close_route.yml")
    @DisplayName("Update route with status completed")
    public void testUpdateRoute_thenRouteShouldBeClosed() {
        routeDto.setId(5);
        routeDto.setRoutePoints(List.of(routePoint));

        routeService.update(5, routeDto);
    }

    @Test
    @DataSet(value = "common/close_delivery/initial_routes_and_deliveries.yml",
            cleanAfter = true, cleanBefore = true, skipCleaningFor = "flyway_schema_history")
    @ExpectedDataSet("common/close_delivery/delivery_automatically_closed.yml")
    @DisplayName("When update route with status completed then delivery should be automatically closed")
    public void testUpdateRoute_thenDeliveryAutomaticallyShouldBeClosed() {
        routeDto.setId(1);
        routeDto.setRoutePoints(List.of(routePoint, anotherRoutePoint));

        routeService.update(1, routeDto);
    }


}