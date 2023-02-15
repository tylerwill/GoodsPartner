package com.goodspartner.service.google;

import com.goodspartner.configuration.properties.ClientRoutingProperties;
import com.goodspartner.entity.Car;
import com.goodspartner.entity.RoutePoint;
import com.goodspartner.service.GraphhopperService;
import com.goodspartner.service.dto.DistanceMatrix;
import com.goodspartner.service.dto.RoutingSolution;
import com.goodspartner.service.dto.VRPSolution;
import com.google.ortools.constraintsolver.Assignment;
import com.google.ortools.constraintsolver.RoutingDimension;
import com.google.ortools.constraintsolver.RoutingIndexManager;
import com.google.ortools.constraintsolver.RoutingModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class GoogleVRPSolver extends AbstractGoogleSolver {

    public GoogleVRPSolver(ClientRoutingProperties clientRoutingProperties,
                           GraphhopperService graphhopperService) {
        super(clientRoutingProperties, graphhopperService);
    }

    public VRPSolution solve(List<Car> cars,
                             List<RoutePoint> routePoints,
                             DistanceMatrix routePointsMatrix) {
        LocalTime depotStartTime = clientRoutingProperties.getDepotStartTime();
        LocalTime depotFinishTime = clientRoutingProperties.getDepotFinishTime();
        Long[][] timeWindows = calculateTimeWindows(routePoints, depotStartTime, depotFinishTime);
        Long[][] timeMatrix = routePointsMatrix.getDuration(); // discuss
        long[] weightDemands = calculateWeightDemands(routePoints);
        long[] vehicleCapacities = cars.stream().mapToLong(Car::getWeightCapacity).toArray();
        long[] vehicleCosts = cars.stream().mapToLong(Car::getTravelCost).toArray();
        int carsAmount = cars.size();

        RoutingIndexManager manager = new RoutingIndexManager(timeMatrix.length, carsAmount, ROUTE_START_INDEX);

        RoutingModel routing = new RoutingModel(manager);
        configureCapacityDimensions(routing, manager, weightDemands, vehicleCapacities);
        configureTimeDimensions(routing, manager, timeMatrix, timeWindows, carsAmount);
        configureVehicleCost(routing, vehicleCosts, carsAmount);

        return getVRPSolution(cars, routePoints, manager, routing);
    }

    private VRPSolution getVRPSolution(List<Car> cars, List<RoutePoint> routePoints,
                                       RoutingIndexManager manager, RoutingModel routing) {
        Assignment solution = getSolution(routing);
        log.info("Solution solved with status: {} and cost: {}",
                GoogleSolverStatus.getByCode(solution.solver().state()).name(),
                solution.objectiveValue());

        List<RoutePoint> droppedRoutePoints = droppedNodesInspection(routePoints, manager, routing, solution);

        RoutingDimension timeDimension = routing.getMutableDimension(VEHICLE_TIME_DIMENSION_NAME);

        List<RoutingSolution> routingSolutions = new ArrayList<>(1);
        for (int carIndex = 0; carIndex < cars.size(); ++carIndex) {
            if (routing.isVehicleUsed(solution, carIndex)) {
                Car car = cars.get(carIndex);
                RoutingSolution routingSolution =
                        getRoutingSolution(car, carIndex, routePoints, manager, routing, solution, timeDimension);
                routingSolutions.add(routingSolution);
            }
        }
        return VRPSolution.builder()
                .routings(routingSolutions)
                .droppedPoints(droppedRoutePoints)
                .build();
    }

}
