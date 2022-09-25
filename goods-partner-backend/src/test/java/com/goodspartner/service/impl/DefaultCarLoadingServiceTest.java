package com.goodspartner.service.impl;

import com.goodspartner.AbstractWebITest;
import com.goodspartner.dto.CarRouteComposition;
import com.goodspartner.dto.MapPoint;
import com.goodspartner.dto.StoreDto;
import com.goodspartner.entity.Car;
import com.goodspartner.entity.RoutePoint;
import com.goodspartner.entity.RoutePointStatus;
import com.goodspartner.repository.CarRepository;
import com.graphhopper.GHResponse;
import com.graphhopper.GraphHopper;
import com.graphhopper.ResponsePath;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@TestInstance(PER_CLASS)
public class DefaultCarLoadingServiceTest extends AbstractWebITest {

    @MockBean
    private CarRepository carRepository;
    @MockBean
    private GraphHopper hopper;

    @Mock
    private ResponsePath responsePath;
    @Mock
    private GHResponse ghResponse;

    @Autowired
    private DefaultCarLoadingService defaultCarLoadingService;

    private CarRouteComposition carRoutesDto;
    private List<RoutePoint> routePointList;
    private StoreDto storeDto;
    private Car car;

    @BeforeAll
    void setUp() throws IOException {

        car = new Car(
                1,
                "Mercedes Vito",
                "Ivan Piddubny",
                true,
                false,
                "AA 2222 CT",
                1000,
                10);

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

        RoutePoint routePointFirst = RoutePoint.builder()
                .id(new UUID(1, 1))
                .status(RoutePointStatus.PENDING)
                .completedAt(null)
                .clientId(1)
                .clientName("ТОВ Пекарня")
                .address("м. Київ, вул. Металістів, 8, оф. 4-24")
                .addressTotalWeight(5.30)
                .routePointDistantTime(55)
                .orders(List.of(addressOrderFirst))
                .mapPoint(MapPoint.builder()
                        .status(MapPoint.AddressStatus.KNOWN)
                        .address("м.Київ, вул. Металістів, 8, оф. 4-24")
                        .latitude(32.32)
                        .longitude(35.35)
                        .build())
                .build();

        RoutePoint routePointSecond = RoutePoint.builder()
                .id(new UUID(2, 2))
                .status(RoutePointStatus.PENDING)
                .completedAt(null)
                .clientId(1)
                .clientName("ТОВ Фудком")
                .address("м. Київ, вул. Пирогівський шлях 138")
                .addressTotalWeight(10.90)
                .routePointDistantTime(55)
                .orders(List.of(addressOrderSecond))
                .mapPoint(MapPoint.builder()
                        .status(MapPoint.AddressStatus.KNOWN)
                        .address("м. Київ, вул. Пирогівський шлях 138")
                        .latitude(12.12)
                        .longitude(15.15)
                        .build())
                .build();

        routePointList = List.of(routePointFirst, routePointSecond);

        carRoutesDto = CarRouteComposition.builder()
                .car(car)
                .routePoints(routePointList)
                .build();

        storeDto = StoreDto.builder()
                .name("Склад 1")
                .address("м. Київ, вул. Некрасова 138")
                .mapPoint(MapPoint.builder()
                        .status(MapPoint.AddressStatus.KNOWN)
                        .address("м. Київ, вул. Некрасова 138")
                        .latitude(72.12)
                        .longitude(85.15)
                        .build())
                .build();
    }

    @Test
    @DisplayName("Test loadCars Verify Correctly Invocation Of Methods findByAvailableCars And getDistanceMatrix")
    void testLoadCars() {

        when(carRepository.findByAvailableTrue()).thenReturn(List.of(car));
        when(hopper.route(any())).thenReturn(ghResponse);
        when(ghResponse.hasErrors()).thenReturn(false);
        when(ghResponse.getBest()).thenReturn(responsePath);
        when(responsePath.getDistance()).thenReturn(20.2);
        when(responsePath.getTime()).thenReturn(20L);

        defaultCarLoadingService.loadCars(storeDto, routePointList);

        // TODO invalid test - nothing is verified
    }

    @Test
    @DisplayName("Test testCalculateDemands Checks Whether It Is Correctly Calculate Demands")
    void testCalculateDemands() {

        long[] actualDemands = defaultCarLoadingService.calculateDemands(routePointList);
        long[] expectedDemands = new long[]{0, 5, 11};

        Assertions.assertNotNull(actualDemands);
        Assertions.assertArrayEquals(expectedDemands, actualDemands);
    }

    @Test
    @DisplayName("Test getCarLoading Checks Whether It Is Correctly Load Size And Create CarRoutesDto Object")
    void testGetCarLoad() {

        CarRouteComposition actualCarRoutesDto = defaultCarLoadingService.getCarLoad(car, routePointList);

        Car actualCar = actualCarRoutesDto.getCar();
        List<RoutePoint> actualRoutePoints = actualCarRoutesDto.getRoutePoints();

        Car expectedCar = carRoutesDto.getCar();
        List<RoutePoint> expectedRoutePoints = carRoutesDto.getRoutePoints();

        Assertions.assertEquals(expectedCar, actualCar);
        Assertions.assertEquals(expectedRoutePoints, actualRoutePoints);

    }
}