package com.goodspartner.service.impl;

import com.goodspartner.dto.*;
import com.goodspartner.service.CarLoadingService;
import com.goodspartner.service.CarService;
import com.goodspartner.service.GraphhopperService;
import com.google.common.annotations.VisibleForTesting;
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

    private final GraphhopperService graphhopperService;

    public DefaultCarLoadingService(CarService carService, GraphhopperService graphhopperService) {
        this.carService = carService;
        this.graphhopperService = graphhopperService;
        Loader.loadNativeLibraries();
    }

    public List<CarRoutesDto> loadCars(StoreDto storeDto, List<RoutePointDto> routePoints) {
        List<CarDto> cars = carService.findByAvailableCars();

//        TODO: REMOVE!
        List<RoutePointDto> kievPoints = routePoints.stream().filter(routePointDto ->
                routePointDto.getMapPoint().getAddress().contains("Київська обл")
                        || routePointDto.getMapPoint().getAddress().contains("Київ")).toList();

        List<MapPoint> mapPoints = new ArrayList<>();
        mapPoints.add(storeDto.getMapPoint());
        mapPoints.addAll(kievPoints.stream().map(RoutePointDto::getMapPoint).toList());

        DistanceMatrix matrix = graphhopperService.getMatrix(mapPoints);

        return load(cars, kievPoints, matrix);
    }

    @VisibleForTesting
    List<CarRoutesDto> load(List<CarDto> cars,
                            List<RoutePointDto> routePoints,
                            DistanceMatrix routePointsMatrix) {
        Long[][] distanceMatrix = routePointsMatrix.getDistance();
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

    @VisibleForTesting
    List<CarRoutesDto> getCarLoadDtos(List<CarDto> cars, List<RoutePointDto> routePoints, RoutingIndexManager manager, RoutingModel routing) {
        Assignment solution = getSolution(routing);
        List<CarRoutesDto> loadCars = new ArrayList<>(1);
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

    @VisibleForTesting
    RoutingModel configureRoutingModel(RoutingIndexManager manager, Long[][] distanceMatrix, long[] demands, long[] vehicleCapacities, long[] vehicleCosts, int carsAmount) {
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

    @VisibleForTesting
    long[] calculateDemands(List<RoutePointDto> routePoints) {
        long[] demands = new long[routePoints.size() + 1];
        List<Long> collect = routePoints.stream()
                .map(point -> Math.round(point.getAddressTotalWeight())).toList();
        for (int i = 0; i < collect.size(); i++) {
            demands[i + 1] = collect.get(i);
        }
        return demands;
    }

    @VisibleForTesting
    CarRoutesDto getCarLoad(CarDto car, List<RoutePointDto> routePoints) {
        double loadSize = BigDecimal.valueOf(routePoints.stream()
                        .map(RoutePointDto::getAddressTotalWeight)
                        .collect(Collectors.summarizingDouble(count -> count)).getSum())
                .setScale(2, RoundingMode.HALF_UP).doubleValue();

        car.setLoadSize(loadSize);
        return CarRoutesDto.builder()
                .car(car)
                .routePoints(routePoints)
                .build();
    }

}
