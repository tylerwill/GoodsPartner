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
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@TestInstance(PER_CLASS)
public class DefaultCarLoadingServiceTest extends AbstractWebITest {
    private static final String DISTANCE_MATRIX_FOR_LOAD_CARS = "datasets/common/cars_loading/distanceMatrix_for_load_cars.json";
    private static final String DESTINATION_ADDRESSES = "destinationAddresses";
    private static final String ORIGIN_ADDRESSES = "originAddresses";
    private static final String DISTANCE_MATRIX_ROWS = "rows";
    @Autowired
    private DefaultCarLoadingService defaultCarLoadingService;
    private final ObjectMapper mapper = new ObjectMapper();
    private CarLoadingService.CarRoutesDto carRoutesDto;
    @MockBean
    private GoogleApiService googleApiService;
    private RoutePointDto routePointsFirst;
    private RoutePointDto routePointsSecond;
    @MockBean
    private CarService carService;
    private StoreDto storeDto;
    private CarDto carDto;

    @BeforeAll
    void before() {

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

        routePointsFirst = RoutePointDto.builder()
                .id(null)
                .status(RoutePointStatus.PENDING)
                .completedAt(null)
                .clientId(1)
                .clientName("ТОВ Пекарня")
                .address("м. Київ, вул. Металістів, 8, оф. 4-24")
                .addressTotalWeight(5.30)
                .routePointDistantTime(55)
                .orders(List.of(addressOrderDtoFirst))
                .build();

        routePointsSecond = RoutePointDto.builder()
                .id(null)
                .status(RoutePointStatus.PENDING)
                .completedAt(null)
                .clientId(1)
                .clientName("ТОВ Фудком")
                .address("м. Київ, вул. Пирогівський шлях 138")
                .addressTotalWeight(5.30)
                .routePointDistantTime(55)
                .orders(List.of(addressOrderDtoSecond))
                .build();

        carRoutesDto = CarLoadingService.CarRoutesDto.builder()
                .car(carDto)
                .routePoints(List.of(routePointsFirst, routePointsSecond))
                .build();

        storeDto = StoreDto.builder()
                .name("Склад 1")
                .address("м. Київ, вул. Некрасова 138")
                .build();
    }

    @Test
    @DisplayName("Check loadCars Correctly Load Car")
    void testLoadCars() throws IOException {

        JsonNode jsonNode = mapper.readTree(getClass().getClassLoader().getResource(DISTANCE_MATRIX_FOR_LOAD_CARS));
        JsonNode originAddresses = jsonNode.get(ORIGIN_ADDRESSES);
        JsonNode destinationAddresses = jsonNode.get(DESTINATION_ADDRESSES);
        JsonNode rows = jsonNode.get(DISTANCE_MATRIX_ROWS);

        String[] origin = mapper.readValue(originAddresses.traverse(), String[].class);
        String[] dest = mapper.readValue(destinationAddresses.traverse(), String[].class);

        DistanceMatrixRow[] distanceMatrixRows = mapper.readValue(rows.traverse(), DistanceMatrixRow[].class);
        DistanceMatrix distanceMatrix = new DistanceMatrix(origin, dest, distanceMatrixRows);

        when(carService.findByAvailableCars()).thenReturn(List.of(carDto));
        when(googleApiService.getDistanceMatrix(anyList())).thenReturn(distanceMatrix);

        List<CarLoadingService.CarRoutesDto> actualCarRoutesDtos = defaultCarLoadingService
                .loadCars(storeDto, List.of(routePointsFirst, routePointsSecond));

        CarDto actualCarDto = actualCarRoutesDtos.get(0).getCar();

        List<RoutePointDto> actualRoutePoints = actualCarRoutesDtos.get(0).getRoutePoints();
        List<RoutePointDto> expectedRoutePoints = List.of(routePointsFirst, routePointsSecond);

        Assertions.assertFalse(actualCarRoutesDtos.isEmpty());
        Assertions.assertEquals(expectedRoutePoints, actualRoutePoints);
        Assertions.assertEquals(carDto, actualCarDto);
    }

    @Test
    @DisplayName("Check load Correctly Load And Return List Of CarRoutesDto Object")
    void testLoad() throws IOException {

        JsonNode jsonNode = mapper.readTree(getClass().getClassLoader().getResource(DISTANCE_MATRIX_FOR_LOAD_CARS));
        JsonNode originAddresses = jsonNode.get(ORIGIN_ADDRESSES);
        JsonNode destinationAddresses = jsonNode.get(DESTINATION_ADDRESSES);
        JsonNode rows = jsonNode.get(DISTANCE_MATRIX_ROWS);

        String[] origin = mapper.readValue(originAddresses.traverse(), String[].class);
        String[] dest = mapper.readValue(destinationAddresses.traverse(), String[].class);

        DistanceMatrixRow[] distanceMatrixRows = mapper.readValue(rows.traverse(), DistanceMatrixRow[].class);
        DistanceMatrix distanceMatrix = new DistanceMatrix(origin, dest, distanceMatrixRows);

        when(carService.findByAvailableCars()).thenReturn(List.of(carDto));
        when(googleApiService.getDistanceMatrix(anyList())).thenReturn(distanceMatrix);

        List<CarLoadingService.CarRoutesDto> actualCarRoutesDtos = defaultCarLoadingService
                .load(List.of(carDto), List.of(routePointsFirst, routePointsSecond), distanceMatrix);

        CarDto actualCarDto = actualCarRoutesDtos.get(0).getCar();

        List<RoutePointDto> actualRoutePoints = actualCarRoutesDtos.get(0).getRoutePoints();
        List<RoutePointDto> expectedRoutePoints = List.of(routePointsFirst, routePointsSecond);

        Assertions.assertFalse(actualCarRoutesDtos.isEmpty());
        Assertions.assertEquals(expectedRoutePoints, actualRoutePoints);
        Assertions.assertEquals(carDto, actualCarDto);
    }

    @Test
    @DisplayName("Check getCarloadDtos Return Correctly CarRoutesDto List")
    void testGetCarLoadDtos() throws IOException {

        JsonNode jsonNode = mapper.readTree(getClass().getClassLoader().getResource(DISTANCE_MATRIX_FOR_LOAD_CARS));
        JsonNode originAddresses = jsonNode.get(ORIGIN_ADDRESSES);
        JsonNode destinationAddresses = jsonNode.get(DESTINATION_ADDRESSES);
        JsonNode rows = jsonNode.get(DISTANCE_MATRIX_ROWS);

        String[] origin = mapper.readValue(originAddresses.traverse(), String[].class);
        String[] dest = mapper.readValue(destinationAddresses.traverse(), String[].class);

        DistanceMatrixRow[] distanceMatrixRows = mapper.readValue(rows.traverse(), DistanceMatrixRow[].class);
        DistanceMatrix routePointsMatrix = new DistanceMatrix(origin, dest, distanceMatrixRows);

        when(carService.findByAvailableCars()).thenReturn(List.of(carDto));
        when(googleApiService.getDistanceMatrix(anyList())).thenReturn(routePointsMatrix);

        long[][] distanceMatrix = defaultCarLoadingService.calculateDistanceMatrix(routePointsMatrix);
        long[] demands = defaultCarLoadingService.calculateDemands(List.of(routePointsFirst, routePointsSecond));

        long[] vehicleCapacities = List.of(carDto).stream()
                .mapToLong(CarDto::getWeightCapacity).toArray();

        long[] vehicleCosts = List.of(carDto).stream()
                .mapToLong(CarDto::getTravelCost).toArray();
        int carsAmount = List.of(carDto).size();

        RoutingIndexManager manager = new RoutingIndexManager(distanceMatrix.length, carsAmount, 0);

        RoutingModel routing = defaultCarLoadingService
                .configureRoutingModel(manager, distanceMatrix, demands, vehicleCapacities, vehicleCosts, carsAmount);

        List<CarLoadingService.CarRoutesDto> actualCarRoutesDtos = defaultCarLoadingService
                .getCarLoadDtos(List.of(carDto), List.of(routePointsFirst, routePointsSecond), manager, routing);

        CarDto actualCarDto = actualCarRoutesDtos.get(0).getCar();
        List<RoutePointDto> actualRoutePoints = actualCarRoutesDtos.get(0).getRoutePoints();
        List<RoutePointDto> expectedRoutePoints = List.of(routePointsFirst, routePointsSecond);

        Assertions.assertFalse(actualCarRoutesDtos.isEmpty());
        Assertions.assertEquals(expectedRoutePoints, actualRoutePoints);
        Assertions.assertEquals(carDto, actualCarDto);
    }

    @Test
    @DisplayName("Check testCalculateDemands Correctly Calculate Demands")
    void testCalculateDemands() {

        long[] actualDemands = defaultCarLoadingService.calculateDemands(List.of(routePointsFirst, routePointsSecond));
        long[] expectedDemands = new long[]{0, 5, 5};

        Assertions.assertNotNull(actualDemands);
        Assertions.assertArrayEquals(expectedDemands, actualDemands);
    }

    @Test
    @DisplayName("Check testCalculateDistance Correctly Calculate Distance Matrix")
    void testCalculateDistanceMatrix() throws IOException {

        JsonNode jsonNode = mapper.readTree(getClass().getClassLoader().getResource(DISTANCE_MATRIX_FOR_LOAD_CARS));
        JsonNode originAddresses = jsonNode.get(ORIGIN_ADDRESSES);
        JsonNode destinationAddresses = jsonNode.get(DESTINATION_ADDRESSES);
        JsonNode rows = jsonNode.get(DISTANCE_MATRIX_ROWS);

        String[] origin = mapper.readValue(originAddresses.traverse(), String[].class);
        String[] dest = mapper.readValue(destinationAddresses.traverse(), String[].class);

        DistanceMatrixRow[] distanceMatrixRows = mapper.readValue(rows.traverse(), DistanceMatrixRow[].class);
        DistanceMatrix distanceMatrix = new DistanceMatrix(origin, dest, distanceMatrixRows);

        when(googleApiService.getDistanceMatrix(anyList())).thenReturn(distanceMatrix);

        long[][] actualDistanceMatrix = defaultCarLoadingService.calculateDistanceMatrix(distanceMatrix);
        long[][] expectedDistanceMatrix = new long[][]{{0, 18149, 27074}, {18229, 0, 16523}, {24525, 16401, 0}};

        Assertions.assertNotNull(actualDistanceMatrix);
        Assertions.assertArrayEquals(expectedDistanceMatrix, actualDistanceMatrix);
    }

    @Test
    @DisplayName("Check getCarLoading Return Correct CarRoutesDto Object")
    void testGetCarLoad() {

        List<RoutePointDto> routePointDtoList = List.of(routePointsFirst, routePointsSecond);

        CarLoadingService.CarRoutesDto actualCarRoutesDto = defaultCarLoadingService
                .getCarLoad(carDto, routePointDtoList);

        CarDto actualCarDto = actualCarRoutesDto.getCar();
        List<RoutePointDto> actualRoutePoints = actualCarRoutesDto.getRoutePoints();

        CarDto expectedCarDto = carRoutesDto.getCar();
        List<RoutePointDto> expectedRoutePoints = carRoutesDto.getRoutePoints();

        Assertions.assertEquals(expectedCarDto, actualCarDto);
        Assertions.assertEquals(expectedRoutePoints, actualRoutePoints);
        Assertions.assertEquals(expectedLoadSize,actualCarDto.getLoadSize());

       /* here you test just the builder logic works fine in method you`re testing
        you should also assert this logic:
        double loadSize = BigDecimal.valueOf(routePoints.stream()
                        .map(RoutePointDto::getAddressTotalWeight)
                        .collect(Collectors.summarizingDouble(count -> count)).getSum())
                .setScale(2, RoundingMode.HALF_UP).doubleValue();*/
    }
}