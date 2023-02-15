package com.goodspartner.service.google;

import com.goodspartner.configuration.properties.ClientRoutingProperties;
import com.goodspartner.entity.Car;
import com.goodspartner.entity.RoutePoint;
import com.goodspartner.service.GraphhopperService;
import com.goodspartner.service.dto.RoutingSolution;
import com.google.common.annotations.VisibleForTesting;
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

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@AllArgsConstructor
public class AbstractGoogleSolver {

    protected static final int ROUTE_START_INDEX = 0;

    // Capacity
    protected static final String VEHICLE_CAPACITY_DIMENSION_NAME = "Capacity";
    protected static final int SLACK_CAPACITY = 0;
    protected static final boolean START_FROM_DEPOT_TRUE = true;

    // TimeWindow
    protected static final String VEHICLE_TIME_DIMENSION_NAME = "Time";
    protected static final long SLACK_TIME_WINDOW_MIN = 120L;
    protected static final boolean START_FROM_DEPOT_FALSE = false;

    // Props
    protected final ClientRoutingProperties clientRoutingProperties;
    // Services
    protected final GraphhopperService graphhopperService;

    protected Long[][] calculateTimeWindows(List<RoutePoint> routePoints,
                                            LocalTime depotStartTime, LocalTime depotFinishTime) {
        if (routePoints.isEmpty()) {
            return new Long[][]{};
        }

        int allPoints = routePoints.size() + 1;
        Long[][] timeWindows = new Long[allPoints][2];

        // Set depot time
        timeWindows[0][0] = normalizeTravelTime(depotStartTime);
        timeWindows[0][1] = normalizeTravelTime(depotFinishTime);

        for (int i = 0; i < routePoints.size(); i++) {

            LocalTime deliveryStartTime = routePoints.get(i).getDeliveryStart();
            timeWindows[i+1][0] = normalizeTravelTime(deliveryStartTime != null
                    ? deliveryStartTime
                    : clientRoutingProperties.getDefaultDeliveryStartTime());

            LocalTime deliveryEndTime = routePoints.get(i).getDeliveryEnd();
            timeWindows[i+1][1] = normalizeTravelTime(deliveryEndTime != null
                    ? deliveryEndTime
                    : clientRoutingProperties.getDefaultDeliveryFinishTime());
        }

        return timeWindows;
    }

    private long normalizeTravelTime(LocalTime time) {
        LocalTime normalizedTime = time.minusMinutes(clientRoutingProperties.getNormalizationTimeMinutes());
        return normalizedTime.getHour() * 60; // minutes
    }

    protected Assignment getSolution(RoutingModel routing) {
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

    protected List<RoutePoint> droppedNodesInspection(List<RoutePoint> routePoints, RoutingIndexManager manager,
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

    protected RoutingSolution getRoutingSolution(Car car, int carIndex, List<RoutePoint> routePoints,
                                                 RoutingIndexManager manager, RoutingModel routing,
                                                 Assignment solution, RoutingDimension timeDimension) {
        List<RoutePoint> carRoutePoints = new ArrayList<>();

        long index = routing.start(carIndex);
        while (!routing.isEnd(index)) {
            long nodeIndex = manager.indexToNode(index);

            IntVar timeVar = timeDimension.cumulVar(index);
            long solutionInMinutes = solution.max(timeVar) + clientRoutingProperties.getNormalizationTimeMinutes(); // MIN-MAX solution window is equal. Tested

            if (nodeIndex == 0) { // Depot
                LocalTime routeStartTimeFromDepot = LocalTime.ofSecondOfDay(solutionInMinutes * 60L);
                log.debug("Depot start time: {} for car: {}", routeStartTimeFromDepot, car);
            } else { // RoutePoints
                RoutePoint routePoint = routePoints.get((int) (nodeIndex - 1)); // exclude depot

                log.trace("RoutePoint range: {} - {}, solution range: {} - {}",
                        routePoint.getDeliveryStart(), routePoint.getDeliveryEnd(), solutionInMinutes, solutionInMinutes);

                routePoint.setExpectedCompletion(LocalTime.ofSecondOfDay(solutionInMinutes * 60L));
                long arrivalTime = solutionInMinutes - clientRoutingProperties.getUnloadingTimeMinutes(); // Solution contains time with service
                routePoint.setExpectedArrival(LocalTime.ofSecondOfDay(arrivalTime * 60L));

                graphhopperService.checkDeliveryTimeRange(routePoint);
                carRoutePoints.add(routePoint);
            }
            index = solution.value(routing.nextVar(index));
        }

        return RoutingSolution.builder()
                .car(car)
                .routePoints(carRoutePoints)
                .build();
    }

    /**
     * Store index 0 - should be 0 demand
     */
    @VisibleForTesting
    protected long[] calculateWeightDemands(List<RoutePoint> routePoints) {
        long[] demands = new long[routePoints.size() + 1];
        List<Long> collect = routePoints.stream()
                .map(point -> Math.round(point.getAddressTotalWeight()))
                .toList();
        for (int i = 0; i < collect.size(); i++) {
            demands[i + 1] = collect.get(i);
        }
        return demands;
    }

    protected void configureCapacityDimensions(RoutingModel routing, RoutingIndexManager manager,
                                             long[] weightDemand, long[] vehicleCapacities) {
        int demandCallbackIndex = routing.registerUnaryTransitCallback((long fromIndex) -> {
            int fromNode = manager.indexToNode(fromIndex);
            return weightDemand[fromNode];
        });
        routing.addDimensionWithVehicleCapacity(demandCallbackIndex,
                SLACK_CAPACITY,
                vehicleCapacities,
                START_FROM_DEPOT_TRUE,
                VEHICLE_CAPACITY_DIMENSION_NAME);
    }

    protected void configureTimeDimensions(RoutingModel routing, RoutingIndexManager manager,
                                         Long[][] timeMatrix, Long[][] timeWindows, int carsAmount) {
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
        // Penalties. No Penalties
        for (int i = 1; i < timeMatrix.length; ++i) {
            routing.addDisjunction(new long[]{manager.nodeToIndex(i)}, Long.MAX_VALUE);
        }
    }

    protected void configureVehicleCost(RoutingModel routing, long[] vehicleCosts, int carsAmount) {
        // Vehicle cost
        for (int i = 0; i < carsAmount; i++) {
            routing.setFixedCostOfVehicle(vehicleCosts[i], i);
        }
    }
}
