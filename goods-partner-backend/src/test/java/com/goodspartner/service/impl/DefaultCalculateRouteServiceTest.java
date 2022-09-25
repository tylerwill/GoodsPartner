package com.goodspartner.service.impl;

import com.goodspartner.dto.CarRouteComposition;
import com.goodspartner.dto.StoreDto;
import com.goodspartner.entity.Car;
import com.goodspartner.entity.Route;
import com.goodspartner.entity.RoutePoint;
import com.goodspartner.entity.RoutePointStatus;
import com.goodspartner.entity.RouteStatus;
import com.goodspartner.service.GoogleApiService;
import com.goodspartner.util.GoogleApiHelper;
import com.google.maps.model.DirectionsRoute;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@TestInstance(PER_CLASS)
class DefaultCalculateRouteServiceTest {
    private final DefaultCalculateRouteService routeService = new DefaultCalculateRouteService(null,
            null, null);
    private List<RoutePoint> routePoints;
    private RoutePoint firstRoutePoint;
    private RoutePoint secondRoutePoint;

    @BeforeAll
    void before() {

        RoutePoint.AddressOrder addressOrderFirst = RoutePoint.AddressOrder.builder()
                .id(12)
                .orderNumber("111")
                .orderTotalWeight(20.20)
                .build();

        RoutePoint.AddressOrder addressOrderSecond = RoutePoint.AddressOrder.builder()
                .id(2)
                .orderNumber("222")
                .orderTotalWeight(13.90)
                .build();

        RoutePoint.AddressOrder addressOrderThird = RoutePoint.AddressOrder.builder()
                .id(12)
                .orderNumber("111")
                .orderTotalWeight(20.20)
                .build();

        RoutePoint.AddressOrder addressOrderFour = RoutePoint.AddressOrder.builder()
                .id(2)
                .orderNumber("222")
                .orderTotalWeight(13.90)
                .build();

        List<RoutePoint.AddressOrder> addressOrderListFirst = List.of(addressOrderFirst, addressOrderSecond);
        List<RoutePoint.AddressOrder> addressOrderListSecond = List.of(addressOrderThird, addressOrderFour);

        firstRoutePoint = RoutePoint.builder()
                .id(null)
                .status(RoutePointStatus.PENDING)
                .completedAt(null)
                .clientId(1)
                .clientName("ТОВ Пекарня")
                .address("м. Київ, вул. Металістів, 8, оф. 4-24")
                .addressTotalWeight(5.30)
                .routePointDistantTime(55)
                .orders(addressOrderListFirst)
                .build();

        secondRoutePoint = RoutePoint.builder()
                .id(null)
                .status(RoutePointStatus.PENDING)
                .completedAt(null)
                .clientId(2)
                .clientName("ТОВ Кондитерська")
                .address("м. Київ, вул. Хрещатик, 1")
                .addressTotalWeight(8.45)
                .routePointDistantTime(76)
                .orders(addressOrderListSecond)
                .build();
    }

    @Test
    @Disabled
    @DisplayName("test calculateRoute Should Create And Return Correct RouteDto Object")
    void testCalculateRoute() {

        // prepare
        // ------------  objects accepted by the method  ----------------
        DirectionsRoute fakeDirectionRoute = new DirectionsRoute();
        GoogleApiService googleApiServiceMock = Mockito.mock(GoogleApiService.class);
        GoogleApiHelper googleApiHelperMock = Mockito.mock(GoogleApiHelper.class);
        DefaultCalculateRouteService routeService = new DefaultCalculateRouteService(null,
//                null, googleApiServiceMock, googleApiHelperMock);
                null, null);

        StoreDto storeDto = mock(StoreDto.class);
        when(storeDto.getAddress()).thenReturn("м. Київ, вул. Металістів, 8, оф. 4-24");
        when(storeDto.getName()).thenReturn("Склад №1");

        when(googleApiHelperMock.getRouteTotalDistance(fakeDirectionRoute)).thenReturn(150.05);

        Car car = new Car(
                1,
                "Mercedes Vito",
                "Ivan Piddubny",
                true,
                false,
                "AA 2222 CT",
                1000,
                10);

        routePoints = List.of(firstRoutePoint);

        CarRouteComposition carRoutesDto = new CarRouteComposition();
        carRoutesDto.setRoutePoints(routePoints);
        carRoutesDto.setCar(car);

        //    -----------------   fake objects for Assertion  ----------------

        RoutePoint.AddressOrder fakeAddressOrderFirst = RoutePoint.AddressOrder.builder()
                .id(12)
                .orderNumber("111")
                .orderTotalWeight(20.20)
                .build();

        RoutePoint.AddressOrder fakeAddressOrderSecond = RoutePoint.AddressOrder.builder()
                .id(2)
                .orderNumber("222")
                .orderTotalWeight(13.90)
                .build();

        List<RoutePoint.AddressOrder> fakeAddressOrderList = List.of(fakeAddressOrderFirst, fakeAddressOrderSecond);

        RoutePoint fakeRoutePoint = RoutePoint.builder()
                .id(null)
                .status(RoutePointStatus.PENDING)
                .completedAt(null)
                .clientId(1)
                .clientName("ТОВ Пекарня")
                .address("м. Київ, вул. Металістів, 8, оф. 4-24")
                .addressTotalWeight(5.30)
                .routePointDistantTime(55)
                .orders(fakeAddressOrderList)
                .build();

        List<RoutePoint> fakeRoutePointList = List.of(fakeRoutePoint);

        // --------- expected object ----------
        Route expectedRoute = new Route();
        expectedRoute.setId(44);
        expectedRoute.setStatus(RouteStatus.DRAFT);
        expectedRoute.setTotalWeight(5.3);
        expectedRoute.setTotalPoints(1);
        expectedRoute.setTotalOrders(2);
        expectedRoute.setDistance(150.05);
        expectedRoute.setEstimatedTime(0);
        expectedRoute.setStoreName("Склад №1");
        expectedRoute.setStoreAddress("м. Київ, вул. Металістів, 8, оф. 4-24");
        expectedRoute.setRoutePoints(fakeRoutePointList);
        expectedRoute.setCar(car);

        //when
        Route actualRoute = routeService.calculateRoute(carRoutesDto, storeDto);

        //then
        Assertions.assertEquals(expectedRoute, actualRoute);
    }

    @Test
    @DisplayName("test getTotalOrders Returns Total Count Of All Orders")
    void testGetTotalOrders() {

        //prepare
        routePoints = List.of(firstRoutePoint, secondRoutePoint);

        //when
        int actualTotalOrdersCount = routeService.getTotalOrders(routePoints);

        //then
        Assertions.assertEquals(4, actualTotalOrdersCount);
    }

    @Test
    @DisplayName("test getRouteOrdersTotalWeight Returns Total Weight Of All Orders")
    void testGetRouteOrdersTotalWeight() {

        // prepare
        routePoints = List.of(firstRoutePoint, secondRoutePoint);

        //when
        double actualTotalWeight = routeService.getRouteOrdersTotalWeight(routePoints);

        //then
        Assertions.assertEquals(13.75, actualTotalWeight);
    }
}