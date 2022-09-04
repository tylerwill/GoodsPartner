package com.goodspartner.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.goodspartner.AbstractWebITest;
import com.goodspartner.dto.CarDto;
import com.goodspartner.dto.RoutePointDto;
import com.goodspartner.dto.StoreDto;
import com.goodspartner.entity.RoutePointStatus;
import com.goodspartner.service.CarLoadingService;
import com.goodspartner.service.CarService;
import com.goodspartner.service.GoogleApiService;
import com.google.maps.model.DistanceMatrix;
import com.google.maps.model.DistanceMatrixRow;
import com.google.ortools.constraintsolver.RoutingIndexManager;
import com.google.ortools.constraintsolver.RoutingModel;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Disabled
@TestInstance(PER_CLASS)
public class DefaultCarLoadingServiceTest extends AbstractWebITest {

    private static final String DISTANCE_MATRIX_FOR_LOAD_CARS = "datasets/route/distanceMatrix_for_loadCars.json";
    private static final String DESTINATION_ADDRESSES = "destinationAddresses";
    private static final String ORIGIN_ADDRESSES = "originAddresses";
    private static final String DISTANCE_MATRIX_ROWS = "rows";

    private final ObjectMapper mapper = new ObjectMapper();

    @MockBean
    private GoogleApiService googleApiServiceMock;
    @MockBean
    private CarService carServiceMock;

    @Autowired
    private DefaultCarLoadingService defaultCarLoadingService;

    private CarLoadingService.CarRoutesDto carRoutesDto;
    private List<RoutePointDto> routePointDtoList;
    private DistanceMatrix distanceMatrix;
    private StoreDto storeDto;
    private CarDto carDto;

    @BeforeAll
    void setUp() throws IOException {

        JsonNode jsonNode = mapper.readTree(getClass().getClassLoader().getResource(DISTANCE_MATRIX_FOR_LOAD_CARS));
        JsonNode originAddresses = jsonNode.get(ORIGIN_ADDRESSES);
        JsonNode destinationAddresses = jsonNode.get(DESTINATION_ADDRESSES);
        JsonNode rows = jsonNode.get(DISTANCE_MATRIX_ROWS);

        String[] origin = mapper.readValue(originAddresses.traverse(), String[].class);
        String[] dest = mapper.readValue(destinationAddresses.traverse(), String[].class);
        DistanceMatrixRow[] distanceMatrixRows = mapper.readValue(rows.traverse(), DistanceMatrixRow[].class);

        distanceMatrix = new DistanceMatrix(origin, dest, distanceMatrixRows);

        carDto = CarDto.builder()
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

        RoutePointDto routePointFirst = RoutePointDto.builder()
                .id(new UUID(1, 1))
                .status(RoutePointStatus.PENDING)
                .completedAt(null)
                .clientId(1)
                .clientName("ТОВ Пекарня")
                .address("м. Київ, вул. Металістів, 8, оф. 4-24")
                .addressTotalWeight(5.30)
                .routePointDistantTime(55)
                .orders(List.of(addressOrderDtoFirst))
                .build();

        RoutePointDto routePointSecond = RoutePointDto.builder()
                .id(new UUID(2, 2))
                .status(RoutePointStatus.PENDING)
                .completedAt(null)
                .clientId(1)
                .clientName("ТОВ Фудком")
                .address("м. Київ, вул. Пирогівський шлях 138")
                .addressTotalWeight(10.90)
                .routePointDistantTime(55)
                .orders(List.of(addressOrderDtoSecond))
                .build();

        routePointDtoList = List.of(routePointFirst, routePointSecond);

        carRoutesDto = CarLoadingService.CarRoutesDto.builder()
                .car(carDto)
                .routePoints(routePointDtoList)
                .build();

        storeDto = StoreDto.builder()
                .name("Склад 1")
                .address("м. Київ, вул. Некрасова 138")
                .build();
    }

    @Test
    @DisplayName("Test loadCars Verify Correctly Invocation Of Methods findByAvailableCars And getDistanceMatrix")
    void testLoadCars() {

        when(carServiceMock.findByAvailableCars()).thenReturn(List.of(carDto));
        when(googleApiServiceMock.getDistanceMatrix(anyList())).thenReturn(distanceMatrix);

        defaultCarLoadingService.loadCars(storeDto, routePointDtoList);

        verify(carServiceMock).findByAvailableCars();
        verify(googleApiServiceMock).getDistanceMatrix(anyList());
    }

//    @Test
//    @DisplayName("Test load Checks Whether It Is Correctly Loads And Create List Of CarRoutesDto Object")
//    void testLoad() {
//
//        List<CarLoadingService.CarRoutesDto> actualCarRoutesDtos = defaultCarLoadingService
//                .load(List.of(carDto), routePointDtoList, distanceMatrix);
//
//        CarDto actualCarDto = actualCarRoutesDtos.get(0).getCar();
//
//        List<RoutePointDto> actualRoutePoints = actualCarRoutesDtos.get(0).getRoutePoints();
//        List<RoutePointDto> expectedRoutePoints = routePointDtoList;
//
//        Assertions.assertFalse(actualCarRoutesDtos.isEmpty());
//        Assertions.assertEquals(expectedRoutePoints, actualRoutePoints);
//        Assertions.assertEquals(carDto, actualCarDto);
//    }

//    @Test
//    @DisplayName("Test getCarloadDtos Checks Whether It Is Correctly Create List Of CarRoutesDto Objects")
//    void testGetCarLoadDtos() {
//
//        long[][] distanceMatrixArray = defaultCarLoadingService.calculateDistanceMatrix(distanceMatrix);
//        long[] demands = defaultCarLoadingService.calculateDemands(routePointDtoList);
//
//        long[] vehicleCapacities = Stream.of(carDto).mapToLong(CarDto::getWeightCapacity).toArray();
//        long[] vehicleCosts = Stream.of(carDto).mapToLong(CarDto::getTravelCost).toArray();
//
//        int carsAmount = List.of(carDto).size();
//
//        RoutingIndexManager manager = new RoutingIndexManager(distanceMatrixArray.length, carsAmount, 0);
//
//        RoutingModel routing = defaultCarLoadingService
//                .configureRoutingModel(manager, distanceMatrixArray, demands, vehicleCapacities, vehicleCosts, carsAmount);
//
//        List<CarLoadingService.CarRoutesDto> actualCarRoutesDtos = defaultCarLoadingService
//                .getCarLoadDtos(List.of(carDto), routePointDtoList, manager, routing);
//
//        CarDto actualCarDto = actualCarRoutesDtos.get(0).getCar();
//        List<RoutePointDto> actualRoutePoints = actualCarRoutesDtos.get(0).getRoutePoints();
//        List<RoutePointDto> expectedRoutePoints = routePointDtoList;
//
//        Assertions.assertFalse(actualCarRoutesDtos.isEmpty());
//        Assertions.assertEquals(expectedRoutePoints, actualRoutePoints);
//        Assertions.assertEquals(carDto, actualCarDto);
//    }

    @Test
    @DisplayName("Test testCalculateDemands Checks Whether It Is Correctly Calculate Demands")
    void testCalculateDemands() {

        long[] actualDemands = defaultCarLoadingService.calculateDemands(routePointDtoList);
        long[] expectedDemands = new long[]{0, 5, 11};

        Assertions.assertNotNull(actualDemands);
        Assertions.assertArrayEquals(expectedDemands, actualDemands);
    }

//    @Test
//    @DisplayName("Test testCalculateDistance Checks Whether It Is Correctly Calculate Distance Matrix")
//    void testCalculateDistanceMatrix() {
//
//        long[][] actualDistanceMatrix = defaultCarLoadingService.calculateDistanceMatrix(distanceMatrix);
//        long[][] expectedDistanceMatrix = new long[][]{{0, 18149, 27074}, {18229, 0, 16523}, {24525, 16401, 0}};
//
//        Assertions.assertNotNull(actualDistanceMatrix);
//        Assertions.assertArrayEquals(expectedDistanceMatrix, actualDistanceMatrix);
//    }

    @Test
    @DisplayName("Test getCarLoading Checks Whether It Is Correctly Load Size And Create CarRoutesDto Object")
    void testGetCarLoad() {

        CarLoadingService.CarRoutesDto actualCarRoutesDto = defaultCarLoadingService
                .getCarLoad(carDto, routePointDtoList);

        CarDto actualCarDto = actualCarRoutesDto.getCar();
        List<RoutePointDto> actualRoutePoints = actualCarRoutesDto.getRoutePoints();

        CarDto expectedCarDto = carRoutesDto.getCar();
        List<RoutePointDto> expectedRoutePoints = carRoutesDto.getRoutePoints();

        Assertions.assertEquals(expectedCarDto, actualCarDto);
        Assertions.assertEquals(expectedRoutePoints, actualRoutePoints);
        Assertions.assertEquals(16.2, actualCarDto.getLoadSize());
    }
}