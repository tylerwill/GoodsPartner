package com.goodspartner.service.google;

import com.goodspartner.configuration.properties.ClientRoutingProperties;
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

/*
 * *Normalisation*
 *   Requested delivery time range : 10:00 - 17-00 (more complex evaluation)
 *   Normalisation: 10:15 - 17:00. (adding only for arrival time)
 *     Solution: 10:15
 *       - Completion: solution -> 10:15 - also contains service time
 *       - Arrival: solution - service time -> 10:15 - 15 = 10:00
 *     Solution: 17:00
 *       - Completion: 17:00
 *       - Arrival: 16:45
 *
 *
 */
@Slf4j
@Service
@AllArgsConstructor
public class GoogleVRPSolver implements VRPSolver {

    private static final int ROUTE_START_INDEX = 0;

    // Capacity
    private static final String VEHICLE_CAPACITY_DIMENSION_NAME = "Capacity";
    private static final int SLACK_CAPACITY = 0;
    private static final boolean START_FROM_DEPOT_TRUE = true;
    // TimeWindow
    private static final String VEHICLE_TIME_DIMENSION_NAME = "Time";
    private static final long SLACK_TIME_WINDOW_MIN = 30L;
    private static final boolean START_FROM_DEPOT_FALSE = false;

    // Props
    private final ClientRoutingProperties clientRoutingProperties;
    // Services
    private final GraphhopperService graphhopperService;
    // Mappers
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
        mapPoints.addAll(routePointMapper.toMapPoints(routePoints));

        DistanceMatrix matrix = graphhopperService.getMatrix(mapPoints);
        log.info("DistanceMatrix has been calculated for {} route points", routePoints.size());

        return solve(cars, routePoints, matrix);
    }

    private VRPSolution solve(List<Car> cars,
                              List<RoutePoint> routePoints,
                              DistanceMatrix routePointsMatrix) {
        Long[][] timeMatrix = routePointsMatrix.getDuration(); // discuss
        Long[][] timeWindows = calculateTimeWindows(routePoints);
        long[] demands = calculateDemands(routePoints);
        long[] vehicleCapacities = cars.stream()
                .mapToLong(Car::getWeightCapacity).toArray();
        long[] vehicleCosts = cars.stream()
                .mapToLong(Car::getTravelCost).toArray();
        int carsAmount = cars.size();

        RoutingIndexManager manager =
                new RoutingIndexManager(timeMatrix.length, carsAmount, ROUTE_START_INDEX);
        RoutingModel routing =
                configureRoutingModel(manager, timeMatrix, timeWindows, demands, vehicleCapacities, vehicleCosts, carsAmount);

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

            if (routing.isVehicleUsed(solution, i)) {
                LocalTime routeStartTimeFromDepot = clientRoutingProperties.getDepotStartTime();

                long index = routing.start(i);
                while (!routing.isEnd(index)) {
                    long nodeIndex = manager.indexToNode(index);

                    IntVar timeVar = timeDimension.cumulVar(index);
                    long solutionInMinutes = solution.max(timeVar); // MIN-MAX solution window is equal. Tested

                    if (nodeIndex == 0) { // Depot
                        routeStartTimeFromDepot = LocalTime.ofSecondOfDay(solutionInMinutes * 60L);
                        log.debug("Depot start time: {} for car: {}", routeStartTimeFromDepot, cars.get(i));
                    } else { // RoutePoints
                        RoutePoint routePoint = routePoints.get((int) (nodeIndex - 1)); // exclude depot

                        log.trace("RoutePoint range: {} - {}, solution:: {}",
                                routePoint.getDeliveryStart(), routePoint.getDeliveryEnd(), solutionInMinutes);

                        routePoint.setExpectedCompletion(LocalTime.ofSecondOfDay(solutionInMinutes * 60L)); // SolutionTime is completion time

                        long serviceTimeMinutes = routePoint.getMapPoint().getServiceTimeMinutes();
                        long arrivalTime = solutionInMinutes - serviceTimeMinutes; // Solution contains time with unloading
                        routePoint.setExpectedArrival(LocalTime.ofSecondOfDay(arrivalTime * 60L));

                        graphhopperService.checkDeliveryTimeRange(routePoint);
                        carRoutePoints.add(routePoint);
                    }
                    index = solution.value(routing.nextVar(index));
                }

                routingSolutions.add(RoutingSolution.builder()
                        .car(cars.get(i))
                        .routePoints(carRoutePoints)
                        .routeStartTimeFromDepot(routeStartTimeFromDepot)
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
                        .setFirstSolutionStrategy(FirstSolutionStrategy.Value.CHRISTOFIDES)
                        .setLocalSearchMetaheuristic(LocalSearchMetaheuristic.Value.GUIDED_LOCAL_SEARCH)
                        .setTimeLimit(Duration.newBuilder()
                                .setSeconds(clientRoutingProperties.getMaxTimeProcessingSolutionSeconds())
                                .build())
                        .build();
        return routing.solveWithParameters(searchParameters);
    }

    private RoutingModel configureRoutingModel(RoutingIndexManager manager,
                                               Long[][] timeMatrix, Long[][] timeWindows, long[] demands,
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
                    // TODO Required formula to relate demand size with calculated serviceTimeAtLocation
                    return timeMatrix[fromNode][toNode] + clientRoutingProperties.getUnloadingTimeMinutes();
                });
        routing.setArcCostEvaluatorOfAllVehicles(timeCallbackIndex);

        routing.addDimension(timeCallbackIndex,
                SLACK_TIME_WINDOW_MIN,
                clientRoutingProperties.getMaxRouteTimeMinutes(),
                START_FROM_DEPOT_FALSE,
                VEHICLE_TIME_DIMENSION_NAME
        );
        RoutingDimension timeDimension = routing.getMutableDimension(VEHICLE_TIME_DIMENSION_NAME);
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
        for (int i = 1; i < timeMatrix.length; ++i) {
            routing.addDisjunction(new long[]{manager.nodeToIndex(i)}, Long.MAX_VALUE);
        }

        return routing;
    }

    /**
     * Store index 0 - should be 0 demand
     */
    @VisibleForTesting
    long[] calculateDemands(List<RoutePoint> routePoints) {
        long[] demands = new long[routePoints.size() + 1];
        List<Long> collect = routePoints.stream()
                .map(point -> Math.round(point.getAddressTotalWeight()))
                .toList();
        for (int i = 0; i < collect.size(); i++) {
            demands[i + 1] = collect.get(i);
        }
        return demands;
    }

    // TODO: refactor ternary operation
    private Long[][] calculateTimeWindows(List<RoutePoint> routePoints) {
        if (routePoints.isEmpty()) {
            return new Long[][]{};
        }

        int allPoints = routePoints.size() + 1;
        Long[][] timeWindows = new Long[allPoints][2];

        // Set depot time
        timeWindows[0][0] = normalizeTravelTime(clientRoutingProperties.getDepotStartTime());
        timeWindows[0][1] = normalizeTravelTime(clientRoutingProperties.getDepotFinishTime());

        for (int i = 0; i < routePoints.size(); i++) {

            RoutePoint routePoint = routePoints.get(i);
            MapPoint mapPoint = routePoint.getMapPoint();
            long serviceTimeMinutes = mapPoint.getServiceTimeMinutes();

            LocalTime deliveryStartTime = routePoint.getDeliveryStart(); //
            timeWindows[i+1][0] = normalizeWithServiceTime(deliveryStartTime != null
                    ? deliveryStartTime
                    : clientRoutingProperties.getDefaultDeliveryStartTime(), serviceTimeMinutes);

            LocalTime deliveryEndTime = routePoint.getDeliveryEnd();
            timeWindows[i+1][1] = normalizeTravelTime(deliveryEndTime != null // DeliveryEndTime should be treated without service time
                    ? deliveryEndTime
                    : clientRoutingProperties.getDefaultDeliveryFinishTime());
        }

        return timeWindows;
    }

    private long normalizeTravelTime(LocalTime time) {
        return time.getHour() * 60L; // minutes
    }

    private long normalizeWithServiceTime(LocalTime time, long serviceTimeMinutes) {
        LocalTime normalizedTime = time.plusMinutes(serviceTimeMinutes);
        return normalizedTime.getHour() * 60L; // minutes
    }

}
