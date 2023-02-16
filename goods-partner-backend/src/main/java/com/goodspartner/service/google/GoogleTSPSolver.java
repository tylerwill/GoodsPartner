package com.goodspartner.service.google;

import com.goodspartner.configuration.properties.ClientRoutingProperties;
import com.goodspartner.entity.Car;
import com.goodspartner.entity.RoutePoint;
import com.goodspartner.service.GraphhopperService;
import com.goodspartner.service.dto.DistanceMatrix;
import com.goodspartner.service.dto.RoutingSolution;
import com.goodspartner.service.dto.TSPSolution;
import com.google.ortools.constraintsolver.Assignment;
import com.google.ortools.constraintsolver.RoutingDimension;
import com.google.ortools.constraintsolver.RoutingIndexManager;
import com.google.ortools.constraintsolver.RoutingModel;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalTime;
import java.time.temporal.ChronoField;
import java.util.List;

@Slf4j
public class GoogleTSPSolver extends AbstractGoogleSolver {

    private final LocalTime routeStartTime;
    private final LocalTime routeFinishTime;

    public GoogleTSPSolver(ClientRoutingProperties clientRoutingProperties,
                           GraphhopperService graphhopperService,
                           LocalTime routeStartTime,
                           LocalTime routeFinishTime) {
        super(clientRoutingProperties, graphhopperService);
        this.routeStartTime = routeStartTime;
        this.routeFinishTime = routeFinishTime;
    }

    public TSPSolution solve(Car car,
                             List<RoutePoint> routePoints,
                             DistanceMatrix distanceMatrix) {
        Long[][] timeWindows = calculateTimeWindows(routePoints, routeStartTime, routeFinishTime);
        Long[][] timeMatrix = distanceMatrix.getDuration();
        int carsAmount = 1;

        RoutingIndexManager manager = new RoutingIndexManager(timeMatrix.length, carsAmount, ROUTE_START_INDEX);

        RoutingModel routing = new RoutingModel(manager);
        configureTimeDimensions(routing, manager, timeMatrix, timeWindows, carsAmount);

        return getTSPSolution(car, routePoints, manager, routing);
    }

    private TSPSolution getTSPSolution(Car car, List<RoutePoint> routePoints, RoutingIndexManager manager, RoutingModel routing) {
        Assignment solution = getSolution(routing);
        log.info("TSP Solution solved with status: {} and cost: {}",
                GoogleSolverStatus.getByCode(solution.solver().state()).name(),
                solution.objectiveValue());

        List<RoutePoint> droppedRoutePoints = droppedNodesInspection(routePoints, manager, routing, solution);

        RoutingDimension timeDimension = routing.getMutableDimension(VEHICLE_TIME_DIMENSION_NAME);

        int carIndex = 0; // For TSP Always take first car
        RoutingSolution routingSolution =
                getRoutingSolution(car, carIndex, routePoints, manager, routing, solution, timeDimension);

        return TSPSolution.builder()
                .routing(routingSolution)
                .droppedPoints(droppedRoutePoints)
                .build();
    }

    // Normalization requires to provide recalculation between absolute drive time, and relative arrival time
    protected long getNormalizationTimeMinutes() {
        return routeStartTime.getLong(ChronoField.MINUTE_OF_DAY);
    }
}
