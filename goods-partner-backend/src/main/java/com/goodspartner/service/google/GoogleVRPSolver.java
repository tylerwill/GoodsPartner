package com.goodspartner.service.google;

import com.goodspartner.dto.MapPoint;
import com.goodspartner.entity.Car;
import com.goodspartner.entity.RoutePoint;
import com.goodspartner.entity.Store;
import com.goodspartner.mapper.RoutePointMapper;
import com.goodspartner.mapper.StoreMapper;
import com.goodspartner.service.GraphhopperService;
import com.goodspartner.service.VRPSolver;
import com.goodspartner.service.dto.DistanceMatrix;
import com.goodspartner.service.dto.GoogleVRPSolverStatus;
import com.goodspartner.service.dto.RoutingSolution;
import com.goodspartner.service.dto.VRPSolution;
import com.google.common.annotations.VisibleForTesting;
import com.google.ortools.Loader;
import com.google.ortools.constraintsolver.Assignment;
import com.google.ortools.constraintsolver.FirstSolutionStrategy;
import com.google.ortools.constraintsolver.IntVar;
import com.google.ortools.constraintsolver.LocalSearchMetaheuristic;
import com.google.ortools.constraintsolver.RoutingDimension;
import com.google.ortools.constraintsolver.RoutingIndexManager;
import com.google.ortools.constraintsolver.RoutingModel;
import com.google.ortools.constraintsolver.RoutingSearchParameters;
import com.google.ortools.constraintsolver.main;
import com.google.protobuf.Duration;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class GoogleVRPSolver implements VRPSolver {

    public static final int SERVICE_TIME_AT_LOCATION_MIN = 15; // minutes

    private static final int ROUTE_START_INDEX = 0;
    private static final int SOLUTION_PROCESSING_TIME_SEC = 30;
    // Capacity
    private static final String VEHICLE_CAPACITY_DIMENSION_NAME = "Capacity";
    private static final int SLACK_CAPACITY = 0;
    private static final boolean START_FROM_DEPOT_TRUE = true;
    // TimeWindow
    private static final String VEHICLE_TIME_DIMENSION_NAME = "Time";
    private static final long SLACK_TIME_WINDOW_MIN = 120L;
    private static final long MAX_ROUTE_TIME_MIN = 10 * 60L;
    private static final boolean START_FROM_DEPOT_FALSE = false;

    private static final LocalTime DEFAULT_DEPOT_START_TIME = LocalTime.of(8, 0); // 0 start of a day
    private static final LocalTime DEFAULT_DEPOT_FINISH_TIME = LocalTime.of(20, 0);
    private static final LocalTime DEFAULT_DELIVERY_START_TIME = LocalTime.of(9, 0);
    private static final LocalTime DEFAULT_DELIVERY_FINISH_TIME = LocalTime.of(19, 0);

    // Normalization requires to provide recalculation between absolute drive time, and relative arrival time
    private static final long NORMALIZATION_TIME_SHIFT_MINUTES = DEFAULT_DEPOT_START_TIME.getHour() * 60L; // 0 start of a day

    private final GraphhopperService graphhopperService;

    private final StoreMapper storeMapper;
    private final RoutePointMapper routePointMapper;

    @PostConstruct
    public void init() {
        log.info("Initializing OR-Tools Native libraries");
        Loader.loadNativeLibraries();
    }

    @Override
    public VRPSolution optimize(List<Car> cars, Store store, List<RoutePoint> routePoints) {

        List<MapPoint> mapPoints = new ArrayList<>();
        mapPoints.add(storeMapper.getMapPoint(store));
        mapPoints.addAll(routePointMapper.getMapPoints(routePoints));

        DistanceMatrix matrix = graphhopperService.getMatrix(mapPoints);
        log.info("DistanceMatrix has been calculated for {} route points", routePoints.size());

        return solve(cars, routePoints, matrix);
    }

    private VRPSolution solve(List<Car> cars,
                              List<RoutePoint> routePoints,
                              DistanceMatrix routePointsMatrix) {
        Long[][] distanceMatrix = routePointsMatrix.getDistance();
        Long[][] timeMatrix = routePointsMatrix.getDuration(); // discuss
        Long[][] timeWindows = calculateTimeWindows(routePoints);
        long[] demands = calculateDemands(routePoints);
        long[] vehicleCapacities = cars.stream()
                .mapToLong(Car::getWeightCapacity).toArray();
        long[] vehicleCosts = cars.stream()
                .mapToLong(Car::getTravelCost).toArray();
        int carsAmount = cars.size();

        RoutingIndexManager manager =
                new RoutingIndexManager(distanceMatrix.length, carsAmount, ROUTE_START_INDEX);
        RoutingModel routing =
                configureRoutingModel(manager, distanceMatrix, timeMatrix, timeWindows, demands, vehicleCapacities, vehicleCosts, carsAmount);

        return getVRPSolution(cars, routePoints, manager, routing);
    }

    private VRPSolution getVRPSolution(List<Car> cars, List<RoutePoint> routePoints,
                                       RoutingIndexManager manager, RoutingModel routing) {
        Assignment solution = getSolution(routing);
        log.info("Solution solved with status: {} and cost: {}",
                GoogleVRPSolverStatus.getByCode(solution.solver().state()).name(),
                solution.objectiveValue());

        List<RoutePoint> droppedRoutePoints = droppedNodesInspection(routePoints, manager, routing, solution);

        RoutingDimension timeDimension = routing.getMutableDimension(VEHICLE_TIME_DIMENSION_NAME);

        List<RoutingSolution> routingSolutions = new ArrayList<>(1);
        for (int i = 0; i < cars.size(); ++i) {
            List<RoutePoint> carRoutePoints = new ArrayList<>(1);
            long index = routing.start(i);
            if (routing.isVehicleUsed(solution, i)) {
                while (!routing.isEnd(index)) {
                    long nodeIndex = manager.indexToNode(index);
                    if (nodeIndex != 0) {
                        RoutePoint routePoint = routePoints.get((int) (nodeIndex - 1));

                        IntVar timeVar = timeDimension.cumulVar(index);
                        long pointCompletionMinutes = (solution.max(timeVar) + NORMALIZATION_TIME_SHIFT_MINUTES);
                        routePoint.setExpectedCompletion(LocalTime.ofSecondOfDay(pointCompletionMinutes * 60L));

                        long pointArrivalMinutes = pointCompletionMinutes - SERVICE_TIME_AT_LOCATION_MIN;
                        routePoint.setExpectedArrival(LocalTime.ofSecondOfDay(pointArrivalMinutes * 60L));

                        graphhopperService.checkDeliveryTimeRange(routePoint);
                        carRoutePoints.add(routePoint);
                    }
                    index = solution.value(routing.nextVar(index));
                }

                routingSolutions.add(RoutingSolution.builder()
                        .car(cars.get(i))
                        .routePoints(carRoutePoints)
                        .build());
            }
        }
        return VRPSolution.builder()
                .routings(routingSolutions)
                .droppedPoints(droppedRoutePoints)
                .build();
    }

    private List<RoutePoint> droppedNodesInspection(List<RoutePoint> routePoints, RoutingIndexManager manager,
                                                    RoutingModel routing, Assignment solution) {
        List<RoutePoint> droppedNodes = new ArrayList<>();
        for (int node = 0; node < routing.size(); ++node) {
            if (routing.isStart(node) || routing.isEnd(node)) {
                continue;
            }
            if (solution.value(routing.nextVar(node)) == node) {
                droppedNodes.add(routePoints.get(manager.indexToNode(node) - 1));
            }
        }
        log.warn("Dropped nodes size: {}", droppedNodes.size());
        return droppedNodes;
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

    private RoutingModel configureRoutingModel(RoutingIndexManager manager,
                                               Long[][] distanceMatrix, Long[][] timeMatrix, Long[][] timeWindows, long[] demands,
                                               long[] vehicleCapacities, long[] vehicleCosts, int carsAmount) {
        RoutingModel routing = new RoutingModel(manager);

        // Capacity dimension
        int demandCallbackIndex = routing.registerUnaryTransitCallback((long fromIndex) -> {
            int fromNode = manager.indexToNode(fromIndex);
            return demands[fromNode];
        });
        routing.addDimensionWithVehicleCapacity(demandCallbackIndex,
                SLACK_CAPACITY,
                vehicleCapacities,
                START_FROM_DEPOT_TRUE,
                VEHICLE_CAPACITY_DIMENSION_NAME);

        // Time Windows dimension
        final int timeCallbackIndex =
                routing.registerTransitCallback((long fromIndex, long toIndex) -> {
                    // Convert from routing variable Index to user NodeIndex.
                    int fromNode = manager.indexToNode(fromIndex);
                    int toNode = manager.indexToNode(toIndex);
                    // TODO Required formula to relate demand size with calulated serviceTimeAtLocation
                    return timeMatrix[fromNode][toNode] + SERVICE_TIME_AT_LOCATION_MIN;
                });
        routing.setArcCostEvaluatorOfAllVehicles(timeCallbackIndex);

        routing.addDimension(timeCallbackIndex,
                SLACK_TIME_WINDOW_MIN,
                MAX_ROUTE_TIME_MIN,
                START_FROM_DEPOT_FALSE,
                VEHICLE_TIME_DIMENSION_NAME
        );
        RoutingDimension timeDimension = routing.getMutableDimension(VEHICLE_TIME_DIMENSION_NAME);
//        timeDimension.setGlobalSpanCostCoefficient(9*60L); // Global boundaries for all routes. Hack!
        // Add time window constraints for each location except depot.
        for (int i = 1; i < timeWindows.length; ++i) {
            long index = manager.nodeToIndex(i);
            timeDimension.cumulVar(index).setRange(timeWindows[i][0], timeWindows[i][1]);
        }
        // Add time window constraints for each vehicle start node.
        for (int i = 0; i < carsAmount; ++i) {
            long index = routing.start(i);
            timeDimension.cumulVar(index).setRange(timeWindows[0][0], timeWindows[0][1]);
        }
        // Instantiate route start and end times to produce feasible times.
        for (int i = 0; i < carsAmount; ++i) {
            routing.addVariableMinimizedByFinalizer(timeDimension.cumulVar(routing.start(i)));
            routing.addVariableMinimizedByFinalizer(timeDimension.cumulVar(routing.end(i)));
        }

        // Vehicle cost
        for (int i = 0; i < carsAmount; i++) {
            routing.setFixedCostOfVehicle(vehicleCosts[i], i);
        }

        // Penalties. No Penalties
        for (int i = 1; i < distanceMatrix.length; ++i) {
            routing.addDisjunction(new long[]{manager.nodeToIndex(i)}, Long.MAX_VALUE);
        }

        return routing;
    }

    @VisibleForTesting
    long[] calculateDemands(List<RoutePoint> routePoints) {
        long[] demands = new long[routePoints.size() + 1];
        List<Long> collect = routePoints.stream()
                .map(point -> Math.round(point.getAddressTotalWeight())).toList();
        for (int i = 0; i < collect.size(); i++) {
            demands[i + 1] = collect.get(i);
        }
        return demands;
    }

    private Long[][] calculateTimeWindows(List<RoutePoint> routePoints) {
        if (routePoints.isEmpty()) {
            return new Long[][]{};
        }

        Long[][] timeWindows = new Long[routePoints.size()][2];

        // Set depot time
        timeWindows[0][0] = normalizeTravelTime(DEFAULT_DEPOT_START_TIME);
        timeWindows[0][1] = normalizeTravelTime(DEFAULT_DEPOT_FINISH_TIME);

        for (int i = 1; i < routePoints.size(); i++) {

            LocalTime deliveryStartTime = routePoints.get(i).getDeliveryStart();
            timeWindows[i][0] = normalizeTravelTime(deliveryStartTime != null
                    ? deliveryStartTime
                    : DEFAULT_DELIVERY_START_TIME);

            LocalTime deliveryEndTime = routePoints.get(i).getDeliveryEnd();
            timeWindows[i][1] = normalizeTravelTime(deliveryEndTime != null
                    ? deliveryEndTime
                    : DEFAULT_DELIVERY_FINISH_TIME); // 19:00
        }

        return timeWindows;
    }

    private long normalizeTravelTime(LocalTime time) {
        LocalTime normalizedTime = time.minusMinutes(NORMALIZATION_TIME_SHIFT_MINUTES);
        return normalizedTime.getHour() * 60; // minutes
    }

}
