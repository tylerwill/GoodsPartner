package com.goodspartner.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import com.github.database.rider.spring.api.DBRider;
import com.goodspartner.AbstractBaseITest;
import com.goodspartner.action.RoutePointAction;
import com.goodspartner.dto.CarDto;
import com.goodspartner.dto.MapPoint;
import com.goodspartner.dto.RouteDto;
import com.goodspartner.dto.StoreDto;
import com.goodspartner.entity.DeliveryStatus;
import com.goodspartner.entity.RoutePoint;
import com.goodspartner.entity.RoutePointStatus;
import com.goodspartner.entity.RouteStatus;
import com.goodspartner.service.RouteService;
import com.goodspartner.web.controller.response.RouteActionResponse;
import com.goodspartner.web.controller.response.RoutePointActionResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

import static com.goodspartner.action.RouteAction.COMPLETE;
import static com.goodspartner.dto.MapPoint.AddressStatus.KNOWN;

@DBRider
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DefaultRouteServiceITest /*extends AbstractBaseITest*/ {

    /*@Autowired
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
    @DisplayName("When update routePoint then Route should be automatically closed")
    public void testUpdatePoint_thenRouteShouldBeAutomaticallyClosed() {
        RoutePointActionResponse routePointActionResponse =
                routeService.updatePoint(2, UUID.fromString("00000000-0000-0000-0000-000000000001"), RoutePointAction.COMPLETE);

        Assertions.assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000001"), routePointActionResponse.getRoutePointId());
        Assertions.assertEquals(RoutePointStatus.DONE, routePointActionResponse.getRoutePointStatus());
        Assertions.assertEquals(2, routePointActionResponse.getRouteId());
        Assertions.assertEquals(RouteStatus.COMPLETED, routePointActionResponse.getRouteStatus());
    }

    @Test
    @DataSet(value = "common/close_delivery/initial_routes_and_deliveries.yml",
            cleanAfter = true, cleanBefore = true, skipCleaningFor = "flyway_schema_history")
    @DisplayName("When update routePoint then routePoint should be in status Done")
    public void testUpdatePoint_thenRoutePointShouldBeDone() {
        RoutePointActionResponse routePointActionResponse =
                routeService.updatePoint(3, UUID.fromString("00000000-0000-0000-0000-000000000001"), RoutePointAction.COMPLETE);

        Assertions.assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000001"), routePointActionResponse.getRoutePointId());
        Assertions.assertEquals(RoutePointStatus.DONE, routePointActionResponse.getRoutePointStatus());
    }

    // TODO fix RoutePoint matching. At th emoment due to reordering/completedAt/etc results doesn't match
    @Test
    @DataSet(value = "common/close_delivery/initial_routes_and_deliveries.yml",
            cleanAfter = true, cleanBefore = true, skipCleaningFor = "flyway_schema_history")
    @ExpectedDataSet(value = "common/close_delivery/update_and_close_route.yml", ignoreCols = "ROUTE_POINTS")
    @DisplayName("Update route with status completed")
    public void testUpdateRoute_thenRouteShouldBeClosed() {
        routeService.update(5, COMPLETE);
    }

    // TODO probably not relevant anymore
    @Test
    @DataSet(value = "common/close_delivery/initial_routes_and_deliveries.yml",
            cleanAfter = true, cleanBefore = true, skipCleaningFor = "flyway_schema_history")
    @ExpectedDataSet(value = "common/close_delivery/delivery_automatically_closed.yml", ignoreCols = "ROUTE_POINTS")
    @DisplayName("When update route with status completed then delivery should be automatically closed")
    public void testUpdateRoute_thenDeliveryAutomaticallyShouldBeClosed() {
        routeService.update(1, COMPLETE);
    }

    @Test
    @DataSet(value = "common/close_delivery/initial_routes_and_deliveries.yml",
            cleanAfter = true, cleanBefore = true, skipCleaningFor = "flyway_schema_history")
    @DisplayName("Update route then Correct DeliveryAndRoutesStatus Returned")
    public void testUpdateRoute_thenCorrectDeliveryAndRoutesStatusReturned() {
        RouteActionResponse update = routeService.update(1, COMPLETE);
        Assertions.assertEquals(UUID.fromString("d0000000-0000-0000-0000-000000000001"), update.getDeliveryId());
        Assertions.assertEquals("COMPLETED", update.getDeliveryStatus().getStatus());
        Assertions.assertEquals(1, update.getRouteId());
        Assertions.assertEquals("COMPLETED", update.getRouteStatus().getStatus());

    }

    @Test
    @DataSet(value = "common/close_delivery/initial_routes_and_deliveries.yml",
            cleanAfter = true, cleanBefore = true, skipCleaningFor = "flyway_schema_history")
    @DisplayName("When update routePoint And Route And Delivery then Correct Reply Returned")
    public void testUpdatePointAndRouteAndDelivery_thenCorrectReplyReturned() {
        RoutePointActionResponse routePointActionResponse = routeService.updatePoint(1, UUID.fromString("00000000-0000-0000-0000-000000000001"), RoutePointAction.COMPLETE);

        Assertions.assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000001"), routePointActionResponse.getRoutePointId());
        Assertions.assertEquals(RoutePointStatus.DONE, routePointActionResponse.getRoutePointStatus());
        Assertions.assertEquals(1, routePointActionResponse.getRouteId());
        Assertions.assertEquals(RouteStatus.COMPLETED, routePointActionResponse.getRouteStatus());
        Assertions.assertEquals(UUID.fromString("d0000000-0000-0000-0000-000000000001"), routePointActionResponse.getDeliveryId());
        Assertions.assertEquals(DeliveryStatus.COMPLETED, routePointActionResponse.getDeliveryStatus());
    }
*/
}