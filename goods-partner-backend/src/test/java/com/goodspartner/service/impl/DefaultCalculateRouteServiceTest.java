package com.goodspartner.service.impl;

import com.goodspartner.dto.CarDto;
import com.goodspartner.dto.RoutePointDto;
import com.goodspartner.dto.StoreDto;
import com.goodspartner.entity.RoutePointStatus;
import com.goodspartner.entity.RouteStatus;
import com.goodspartner.service.CarLoadingService;
import com.goodspartner.service.GoogleApiService;
import com.goodspartner.service.impl.util.GoogleApiHelper;
import com.goodspartner.web.controller.response.RoutesCalculation;
import com.google.maps.model.DirectionsRoute;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@TestInstance(PER_CLASS)
class DefaultCalculateRouteServiceTest {
    private List<RoutePointDto> routePoints;
    private RoutePointDto firstRoutePointDto;
    private RoutePointDto secondRoutePointDto;
    private final DefaultCalculateRouteService routeService = new DefaultCalculateRouteService(null,
            null, null, null);

    @BeforeAll
    void before() {

        RoutePointDto.AddressOrderDto addressOrderDtoFirst = RoutePointDto.AddressOrderDto.builder()
                .id(12)
                .orderNumber("111")
                .orderTotalWeight(20.20)
                .build();

        RoutePointDto.AddressOrderDto addressOrderDtoSecond = RoutePointDto.AddressOrderDto.builder()
                .id(2)
                .orderNumber("222")
                .orderTotalWeight(13.90)
                .build();

        RoutePointDto.AddressOrderDto addressOrderDtoThird = RoutePointDto.AddressOrderDto.builder()
                .id(12)
                .orderNumber("111")
                .orderTotalWeight(20.20)
                .build();

        RoutePointDto.AddressOrderDto addressOrderDtoFour = RoutePointDto.AddressOrderDto.builder()
                .id(2)
                .orderNumber("222")
                .orderTotalWeight(13.90)
                .build();

        List<RoutePointDto.AddressOrderDto> addressOrderDtoListFirst = List.of(addressOrderDtoFirst, addressOrderDtoSecond);
        List<RoutePointDto.AddressOrderDto> addressOrderDtoListSecond = List.of(addressOrderDtoThird, addressOrderDtoFour);

        firstRoutePointDto = RoutePointDto.builder()
                .id(null)
                .status(RoutePointStatus.PENDING)
                .completedAt(null)
                .clientId(1)
                .clientName("ТОВ Пекарня")
                .address("м. Київ, вул. Металістів, 8, оф. 4-24")
                .addressTotalWeight(5.30)
                .routePointDistantTime(55)
                .orders(addressOrderDtoListFirst)
                .build();

        secondRoutePointDto = RoutePointDto.builder()
                .id(null)
                .status(RoutePointStatus.PENDING)
                .completedAt(null)
                .clientId(2)
                .clientName("ТОВ Кондитерська")
                .address("м. Київ, вул. Хрещатик, 1")
                .addressTotalWeight(8.45)
                .routePointDistantTime(76)
                .orders(addressOrderDtoListSecond)
                .build();
    }

    @Test
    @DisplayName("test calculateRoute Should Create And Return Correct RouteDto Object")
    public void testCalculateRoute() {

        // prepare
        // ------------  objects accepted by the method  ----------------
        DirectionsRoute fakeDirectionRoute = new DirectionsRoute();
        GoogleApiService googleApiServiceMock = Mockito.mock(GoogleApiService.class);
        GoogleApiHelper googleApiHelperMock = Mockito.mock(GoogleApiHelper.class);
        DefaultCalculateRouteService routeService = new DefaultCalculateRouteService(null,
                null, googleApiServiceMock, googleApiHelperMock);

        StoreDto storeDto = mock(StoreDto.class);
        when(storeDto.getAddress()).thenReturn("м. Київ, вул. Металістів, 8, оф. 4-24");
        when(storeDto.getName()).thenReturn("Склад №1");

        when(googleApiServiceMock.getDirectionRoute(Mockito.eq(storeDto.getAddress()), any(List.class)))
                .thenReturn(fakeDirectionRoute);
        when(googleApiHelperMock.getRouteTotalDistance(fakeDirectionRoute)).thenReturn(150.05);

        CarDto carDto = CarDto.builder()
                .id(44)
                .name("Mercedes Sprinter")
                .driver("Вальдемар Кипарисович")
                .licencePlate("AA 1111 CT")
                .weightCapacity(2000)
                .cooler(true)
                .available(true)
                .loadSize(1148.78)
                .travelCost(12)
                .build();

        routePoints = List.of(firstRoutePointDto);

        CarLoadingService.CarRoutesDto carRoutesDto = new CarLoadingService.CarRoutesDto();
        carRoutesDto.setRoutePoints(routePoints);
        carRoutesDto.setCar(carDto);

        //    -----------------   fake objects for Assertion  ----------------

        RoutePointDto.AddressOrderDto fakeAddressOrderDtoFirst = RoutePointDto.AddressOrderDto.builder()
                .id(12)
                .orderNumber("111")
                .orderTotalWeight(20.20)
                .build();

        RoutePointDto.AddressOrderDto fakeAddressOrderDtoSecond = RoutePointDto.AddressOrderDto.builder()
                .id(2)
                .orderNumber("222")
                .orderTotalWeight(13.90)
                .build();

        List<RoutePointDto.AddressOrderDto> fakeAddressOrderDtoList = List.of(fakeAddressOrderDtoFirst, fakeAddressOrderDtoSecond);

        RoutePointDto fakeRoutePointDto = RoutePointDto.builder()
                .id(null)
                .status(RoutePointStatus.PENDING)
                .completedAt(null)
                .clientId(1)
                .clientName("ТОВ Пекарня")
                .address("м. Київ, вул. Металістів, 8, оф. 4-24")
                .addressTotalWeight(5.30)
                .routePointDistantTime(55)
                .orders(fakeAddressOrderDtoList)
                .build();

        List<RoutePointDto> fakeRoutePointDtoList = List.of(fakeRoutePointDto);

        // --------- expected object ----------

        RoutesCalculation.RouteDto expectedRouteDto = RoutesCalculation.RouteDto.builder()
                .id(44)
                .status(RouteStatus.DRAFT)
                .totalWeight(5.3)
                .totalPoints(1)
                .totalOrders(2)
                .distance(150.05)
                .estimatedTime(0)
                .startTime(null)
                .finishTime(null)
                .spentTime(0)
                .storeName("Склад №1")
                .storeAddress("м. Київ, вул. Металістів, 8, оф. 4-24")
                .optimization(false)
                .routePoints(fakeRoutePointDtoList)
                .car(CarDto.builder()
                        .id(44)
                        .name("Mercedes Sprinter")
                        .licencePlate("AA 1111 CT")
                        .driver("Вальдемар Кипарисович")
                        .weightCapacity(2000)
                        .cooler(true)
                        .available(true)
                        .loadSize(1148.78)
                        .travelCost(12)
                        .build())
                .build();

        //when
        RoutesCalculation.RouteDto actualRouteDto = routeService.calculateRoute(carRoutesDto, storeDto);

        //then
        Assertions.assertEquals(expectedRouteDto, actualRouteDto);
    }

    @Test
    @DisplayName("test getTotalOrders Returns Total Count Of All Orders")
    public void testGetTotalOrders() {

        //prepare
        routePoints = List.of(firstRoutePointDto, secondRoutePointDto);

        //when
        int actualTotalOrdersCount = routeService.getTotalOrders(routePoints);

        //then
        Assertions.assertEquals(4, actualTotalOrdersCount);
    }

    @Test
    @DisplayName("test getRouteOrdersTotalWeight Returns Total Weight Of All Orders")
    public void testGetRouteOrdersTotalWeight() {

        // prepare
        routePoints = List.of(firstRoutePointDto, secondRoutePointDto);

        //when
        double actualTotalWeight = routeService.getRouteOrdersTotalWeight(routePoints);

        //then
        Assertions.assertEquals(13.75, actualTotalWeight);
    }
}