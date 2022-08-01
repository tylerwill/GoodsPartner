package com.goods.partner.service.impl;

import com.goods.partner.dto.CarDto;
import com.goods.partner.dto.CarLoadDto;
import com.goods.partner.dto.RoutePointDto;
import com.goods.partner.dto.StoreDto;
import com.goods.partner.exceptions.GoogleApiException;
import com.goods.partner.service.CarLoadingService;
import com.goods.partner.service.CarService;
import com.google.maps.DistanceMatrixApi;
import com.google.maps.DistanceMatrixApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DistanceMatrix;
import com.google.maps.model.DistanceMatrixRow;
import com.google.maps.model.TravelMode;
import com.google.ortools.Loader;
import com.google.ortools.constraintsolver.*;
import com.google.protobuf.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DefaultCarLoadingService implements CarLoadingService {
    private static final int ROUTE_START_INDEX = 0;
    @Value("${google.api.key}")
    private String GOOGLE_API_KEY;
    private final CarService carService;


    public List<CarLoadDto> loadCars(StoreDto store, List<RoutePointDto> routePoints) {
        List<CarDto> cars = carService.findByAvailableTrue();
        DistanceMatrix routePointsMatrix = getDistanceMatrix(store, routePoints);
        return load(cars, routePoints, routePointsMatrix);
    }

    private List<CarLoadDto> load(List<CarDto> cars,
                                  List<RoutePointDto> routePoints,
                                  DistanceMatrix routePointsMatrix) {
        long[][] distanceMatrix = calculateDistanceMatrix(routePointsMatrix);
        long[] demands = calculateDemands(routePoints);
        long[] vehicleCapacities = cars.stream()
                .mapToLong(CarDto::getWeightCapacity).toArray();
        long[] vehicleCosts = cars.stream()
                .mapToLong(CarDto::getTravelCost).toArray();
        int carsAmount = cars.size();

        Loader.loadNativeLibraries();
        RoutingIndexManager manager =
                new RoutingIndexManager(distanceMatrix.length, carsAmount, ROUTE_START_INDEX);
        RoutingModel routing =
                configureRoutingModel(manager, distanceMatrix, demands, vehicleCapacities, vehicleCosts, carsAmount);

        return getCarLoadDtos(cars, routePoints, manager, routing);
    }

    private List<CarLoadDto> getCarLoadDtos(List<CarDto> cars, List<RoutePointDto> routePoints, RoutingIndexManager manager, RoutingModel routing) {
        Assignment solution = getSolution(routing);
        List<CarLoadDto> loadCars = new ArrayList<>(1);
        for (int i = 0; i < cars.size(); ++i) {
            List<RoutePointDto> carRoutePoints = new ArrayList<>(1);
            long index = routing.start(i);
            if (routing.isVehicleUsed(solution, i)) {
                while (!routing.isEnd(index)) {
                    long nodeIndex = manager.indexToNode(index);
                    if (nodeIndex != 0) {
                        carRoutePoints.add(routePoints.get((int) (nodeIndex - 1)));
                    }
                    index = solution.value(routing.nextVar(index));
                }
                loadCars.add(getCarLoad(cars.get(i), carRoutePoints));
            }
        }
        return loadCars;
    }

    private DistanceMatrix getDistanceMatrix(StoreDto store, List<RoutePointDto> routePoints) {
        List<String> pointsAddresses = new ArrayList<>();
        pointsAddresses.add(store.getStoreAddress());
        pointsAddresses.addAll(routePoints.stream()
                .map(RoutePointDto::getAddress).toList());

        try (GeoApiContext context = new GeoApiContext.Builder()
                .apiKey(GOOGLE_API_KEY)
                .build()) {
            DistanceMatrixApiRequest matrixApiRequest = DistanceMatrixApi.newRequest(context);
            return matrixApiRequest.origins(pointsAddresses.toArray(String[]::new))
                    .destinations(pointsAddresses.toArray(String[]::new))
                    .mode(TravelMode.DRIVING)
                    .language("uk-UK")
                    .await();
        } catch (IOException | ApiException | InterruptedException e) {
            throw new GoogleApiException(e);
        }
    }

    private Assignment getSolution(RoutingModel routing) {
        RoutingSearchParameters searchParameters =
                main.defaultRoutingSearchParameters()
                        .toBuilder()
                        .setFirstSolutionStrategy(FirstSolutionStrategy.Value.PATH_CHEAPEST_ARC)
                        .setLocalSearchMetaheuristic(LocalSearchMetaheuristic.Value.GUIDED_LOCAL_SEARCH)
                        .setTimeLimit(Duration.newBuilder().setSeconds(1).build())
                        .build();
        return routing.solveWithParameters(searchParameters);
    }

    private RoutingModel configureRoutingModel(RoutingIndexManager manager, long[][] distanceMatrix, long[] demands, long[] vehicleCapacities, long[] vehicleCosts, int carsAmount) {
        Loader.loadNativeLibraries();
        RoutingModel routing = new RoutingModel(manager);
        int transitCallbackIndex =
                routing.registerTransitCallback((long fromIndex, long toIndex) -> {
                    int fromNode = manager.indexToNode(fromIndex);
                    int toNode = manager.indexToNode(toIndex);
                    return distanceMatrix[fromNode][toNode];
                });
        routing.setArcCostEvaluatorOfAllVehicles(transitCallbackIndex);
        int demandCallbackIndex = routing.registerUnaryTransitCallback((long fromIndex) -> {
            int fromNode = manager.indexToNode(fromIndex);
            return demands[fromNode];
        });
        routing.addDimensionWithVehicleCapacity(demandCallbackIndex, 0,
                vehicleCapacities,
                true,
                "Capacity");
        for (int i = 0; i < carsAmount; i++) {
            routing.setFixedCostOfVehicle(vehicleCosts[i], i);
        }
        for (int i = 1; i < distanceMatrix.length; ++i) {
            routing.addDisjunction(new long[]{manager.nodeToIndex(i)}, Long.MAX_VALUE);
        }
        return routing;
    }

    private long[] calculateDemands(List<RoutePointDto> routePoints) {
        long[] demands = new long[routePoints.size() + 1];
        List<Long> collect = routePoints.stream()
                .map(point -> Math.round(point.getAddressTotalWeight())).toList();
        for (int i = 0; i < collect.size(); i++) {
            demands[i + 1] = collect.get(i);
        }
        return demands;
    }

    private long[][] calculateDistanceMatrix(DistanceMatrix routePointsMatrix) {
        DistanceMatrixRow[] rows = routePointsMatrix.rows;
        long[][] distanceMatrix = new long[rows.length][rows.length];
        for (int i = 0; i < rows.length; i++) {
            for (int j = 0; j < rows.length; j++) {
                distanceMatrix[i][j] = rows[i].elements[j].distance.inMeters;
            }
        }
        return distanceMatrix;
    }

    private CarLoadDto getCarLoad(CarDto car, List<RoutePointDto> routePoints) {
        double loadSize = BigDecimal.valueOf(routePoints.stream()
                        .map(RoutePointDto::getAddressTotalWeight)
                        .collect(Collectors.summarizingDouble(count -> count)).getSum())
                .setScale(2, RoundingMode.HALF_UP).doubleValue();

        car.setLoadSize(loadSize);
        return CarLoadDto.builder()
                .car(car)
                .routePoints(routePoints)
                .build();
    }
}
