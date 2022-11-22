package com.goodspartner.service.google;

import com.goodspartner.AbstractWebITest;
import com.goodspartner.dto.MapPoint;
import com.goodspartner.entity.Car;
import com.goodspartner.entity.RoutePoint;
import com.goodspartner.entity.RoutePointStatus;
import com.graphhopper.GHResponse;
import com.graphhopper.GraphHopper;
import com.graphhopper.ResponsePath;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.io.IOException;
import java.util.List;

import static com.goodspartner.entity.AddressStatus.KNOWN;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

@TestInstance(PER_CLASS)
public class GoogleVRPSolverTest extends AbstractWebITest {

//    @MockBean
//    private GraphHopper hopper;
//
//    @Mock
//    private ResponsePath responsePath;
//    @Mock
//    private GHResponse ghResponse;
//
//    @Autowired
//    private GoogleVRPSolver googleVRPSovler;
//
//    private List<RoutePoint> routePointList;
//    private Car car;
//    private MapPoint storeMapPoint;
//
//    @BeforeAll
//    void setUp() throws IOException {
//
//        storeMapPoint = MapPoint.builder()
//                .address("15, Калинова вулиця, Фастів, Фастівська міська громада, Фастівський район, Київська область, 08500, Україна")
//                .latitude(50.08340335)
//                .longitude(29.885050630832627)
//                .status(KNOWN)
//                .build();
//
//        car = new Car(
//                1,
//                "Mercedes Vito",
//                "Ivan Piddubny",
//                true,
//                false,
//                "AA 2222 CT",
//                1000,
//                10,
//                false);
//
////        RoutePoint.OrderReference orderReferenceFirst = RoutePoint.OrderReference.builder()
////                .id(12)
////                .orderNumber("111")
////                .orderTotalWeight(20.20)
////                .build();
////
////        RoutePoint.OrderReference orderReferenceSecond = RoutePoint.OrderReference.builder()
////                .id(2)
////                .orderNumber("222")
////                .orderTotalWeight(13.90)
////                .build();
//
//        RoutePoint routePointFirst = RoutePoint.builder()
//                .id(1L)
//                .status(RoutePointStatus.PENDING)
//                .completedAt(null)
//                .clientId(1)
//                .clientName("ТОВ Пекарня")
//                .address("м. Київ, вул. Металістів, 8, оф. 4-24")
//                .addressTotalWeight(5.30)
//                .routePointDistantTime(55)
//                .orders(List.of(orderReferenceFirst))
//                .mapPoint(MapPoint.builder()
//                        .status(MapPoint.AddressStatus.KNOWN)
//                        .address("м.Київ, вул. Металістів, 8, оф. 4-24")
//                        .latitude(32.32)
//                        .longitude(35.35)
//                        .build())
//                .build();
//
//        RoutePoint routePointSecond = RoutePoint.builder()
//                .id(new UUID(2, 2))
//                .status(RoutePointStatus.PENDING)
//                .completedAt(null)
//                .clientId(1)
//                .clientName("ТОВ Фудком")
//                .address("м. Київ, вул. Пирогівський шлях 138")
//                .addressTotalWeight(10.90)
//                .routePointDistantTime(55)
//                .orders(List.of(orderReferenceSecond))
//                .mapPoint(MapPoint.builder()
//                        .status(MapPoint.AddressStatus.KNOWN)
//                        .address("м. Київ, вул. Пирогівський шлях 138")
//                        .latitude(12.12)
//                        .longitude(15.15)
//                        .build())
//                .build();
//
//        routePointList = List.of(routePointFirst, routePointSecond);
//    }
//
//    @Test
//    @DisplayName("Test loadCars Verify Correctly Invocation Of Methods findByAvailableCars And getDistanceMatrix")
//    void testLoadCars() {
//
//        when(hopper.route(any())).thenReturn(ghResponse);
//        when(ghResponse.hasErrors()).thenReturn(false);
//        when(ghResponse.getBest()).thenReturn(responsePath);
//        when(responsePath.getDistance()).thenReturn(20.2);
//        when(responsePath.getTime()).thenReturn(20L);
//
//        VRPSolution vrpOptimisation = googleVRPSovler.optimize(List.of(car), storeMapPoint, routePointList);
//
//        List<RoutingSolution> routings = vrpOptimisation.getRoutings();
//        Assertions.assertEquals(1, routings.size());
//        RoutingSolution routingSolution = routings.get(0);
//
//        Assertions.assertEquals(car, routingSolution.getCar());
//        Assertions.assertEquals(2, routingSolution.getRoutePointDtos().size());
//    }
//
//    @Test
//    @DisplayName("Test testCalculateDemands Checks Whether It Is Correctly Calculate Demands")
//    void testCalculateDemands() {
//
//        long[] actualDemands = googleVRPSovler.calculateDemands(routePointList);
//        long[] expectedDemands = new long[]{0, 5, 11};
//
//        Assertions.assertNotNull(actualDemands);
//        Assertions.assertArrayEquals(expectedDemands, actualDemands);
//    }
}