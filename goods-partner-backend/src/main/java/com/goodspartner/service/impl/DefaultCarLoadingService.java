package com.goodspartner.service.impl;

import com.goodspartner.dto.CarDto;
import com.goodspartner.dto.CarLoadDto;
import com.goodspartner.dto.RoutePointDto;
import com.goodspartner.dto.StoreDto;
import com.goodspartner.service.CarLoadingService;
import com.goodspartner.service.CarService;
import com.goodspartner.service.GoogleApiService;
import com.google.maps.model.DistanceMatrix;
import com.google.maps.model.DistanceMatrixRow;
import com.google.ortools.Loader;
import com.google.ortools.constraintsolver.*;
import com.google.protobuf.Duration;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DefaultCarLoadingService implements CarLoadingService {
    private static final int ROUTE_START_INDEX = 0;
    private static final int SOLUTION_PROCESSING_TIME_SEC = 3;
    private static final int SLACK_UPPER_BOUND = 0;
    private static final String VEHICLE_CAPACITY_DIMENSION_NAME = "Capacity";
    private static final boolean START_CUMUL_TO_ZERO = true;
    private final CarService carService;
    private final GoogleApiService googleApiService;

    public DefaultCarLoadingService(CarService carService, GoogleApiService googleApiService) {
        this.carService = carService;
        this.googleApiService = googleApiService;
        Loader.loadNativeLibraries();
    }

    public List<CarLoadDto> loadCars(StoreDto store, List<RoutePointDto> routePoints) {
        List<CarDto> cars = carService.findByAvailableCars();
        List<String> pointsAddresses = new ArrayList<>();
        pointsAddresses.add(store.getStoreAddress());
        pointsAddresses.addAll(routePoints.stream()
                .map(RoutePointDto::getAddress).toList());

        DistanceMatrix routePointsMatrix = googleApiService.getDistanceMatrix(pointsAddresses);
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

    private Assignment getSolution(RoutingModel routing) {
        RoutingSearchParameters searchParameters =
                main.defaultRoutingSearchParameters()
                        .toBuilder()
                        .setFirstSolutionStrategy(FirstSolutionStrategy.Value.PATH_CHEAPEST_ARC)
                        .setLocalSearchMetaheuristic(LocalSearchMetaheuristic.Value.GUIDED_LOCAL_SEARCH)
                        .setTimeLimit(Duration.newBuilder().setSeconds(SOLUTION_PROCESSING_TIME_SEC).build())
                        .build();
        return routing.solveWithParameters(searchParameters);
    }

    private RoutingModel configureRoutingModel(RoutingIndexManager manager, long[][] distanceMatrix, long[] demands, long[] vehicleCapacities, long[] vehicleCosts, int carsAmount) {
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
        routing.addDimensionWithVehicleCapacity(demandCallbackIndex, SLACK_UPPER_BOUND,
                vehicleCapacities,
                START_CUMUL_TO_ZERO,
                VEHICLE_CAPACITY_DIMENSION_NAME);
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
