package com.goodspartner.service.impl;

import com.goodspartner.dto.MapPoint;
import com.goodspartner.entity.RoutePoint;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.TestInstance;

import java.util.List;

import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

@TestInstance(PER_CLASS)
@Disabled
// TODO rework after moving RoutePoints to separate table
class DefaultRouteCalculationServiceTest {
    private final DefaultRouteCalculationService routeService = new DefaultRouteCalculationService(
            null, null, null, null, null, null, null);
    private List<RoutePoint> routePoints;
    private RoutePoint firstRoutePoint;
    private RoutePoint secondRoutePoint;
    private MapPoint storeMapPoint;

    /*@BeforeAll
    void before() {
        storeMapPoint = MapPoint.builder()
                .address("15, Калинова вулиця, Фастів, Фастівська міська громада, Фастівський район, Київська область, 08500, Україна")
                .latitude(50.08340335)
                .longitude(29.885050630832627)
                .status(KNOWN)
                .build();

        RoutePoint.OrderReference orderReferenceFirst = RoutePoint.OrderReference.builder()
                .id(12)
                .orderNumber("111")
                .orderTotalWeight(20.20)
                .build();

        RoutePoint.OrderReference orderReferenceSecond = RoutePoint.OrderReference.builder()
                .id(2)
                .orderNumber("222")
                .orderTotalWeight(13.90)
                .build();

        RoutePoint.OrderReference orderReferenceThird = RoutePoint.OrderReference.builder()
                .id(12)
                .orderNumber("111")
                .orderTotalWeight(20.20)
                .build();

        RoutePoint.OrderReference orderReferenceFour = RoutePoint.OrderReference.builder()
                .id(2)
                .orderNumber("222")
                .orderTotalWeight(13.90)
                .build();

        List<RoutePoint.OrderReference> orderReferenceListFirst = List.of(orderReferenceFirst, orderReferenceSecond);
        List<RoutePoint.OrderReference> orderReferenceListSecond = List.of(orderReferenceThird, orderReferenceFour);

        firstRoutePoint = RoutePoint.builder()
                .id(null)
                .status(RoutePointStatus.PENDING)
                .completedAt(null)
                .clientId(1)
                .clientName("ТОВ Пекарня")
                .address("м. Київ, вул. Металістів, 8, оф. 4-24")
                .addressTotalWeight(5.30)
                .routePointDistantTime(55)
                .orders(orderReferenceListFirst)
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
                .orders(orderReferenceListSecond)
                .build();
    }

    @Test
    @Disabled
    @DisplayName("test calculateRoute Should Create And Return Correct RouteDto Object")
    void testCalculateRoute() {
        Car car = new Car(
                1,
                "Mercedes Vito",
                "Ivan Piddubny",
                true,
                false,
                "AA 2222 CT",
                1000,
                10,
                false);

        Store store = new Store(UUID.fromString("5688492e-ede4-45d3-923b-5f9773fd3d4b"),
                "Склад №1",
                "15, Калинова вулиця, Фастів, Фастівська міська громада, Фастівський район, Київська область, 08500, Україна",
                50.08340335,
                29.885050630832627);

        routePoints = List.of(firstRoutePoint);

        RoutingSolution routingSolution = RoutingSolution.builder()
                .routePoints(routePoints)
                .car(car)
                .build();

        //    -----------------   fake objects for Assertion  ----------------

        RoutePoint.OrderReference fakeOrderReferenceFirst = RoutePoint.OrderReference.builder()
                .id(12)
                .orderNumber("111")
                .orderTotalWeight(20.20)
                .build();

        RoutePoint.OrderReference fakeOrderReferenceSecond = RoutePoint.OrderReference.builder()
                .id(2)
                .orderNumber("222")
                .orderTotalWeight(13.90)
                .build();

        List<RoutePoint.OrderReference> fakeOrderReferenceList = List.of(fakeOrderReferenceFirst, fakeOrderReferenceSecond);

        RoutePoint fakeRoutePoint = RoutePoint.builder()
                .id(null)
                .status(RoutePointStatus.PENDING)
                .completedAt(null)
                .clientId(1)
                .clientName("ТОВ Пекарня")
                .address("м. Київ, вул. Металістів, 8, оф. 4-24")
                .addressTotalWeight(5.30)
                .routePointDistantTime(55)
                .orders(fakeOrderReferenceList)
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
        expectedRoute.setRoutePoints(fakeRoutePointList);
        expectedRoute.setCar(car);
        expectedRoute.setStore(store);

        //when
        Route actualRoute = routeService.mapToRoute(routingSolution, store);

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
    }*/
}