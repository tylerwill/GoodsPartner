package com.goodspartner.service.impl;

import com.goodspartner.configuration.properties.ClientRoutingProperties;
import com.goodspartner.dto.MapPoint;
import com.goodspartner.dto.RoutePointDto;
import com.goodspartner.entity.Car;
import com.goodspartner.entity.OrderExternal;
import com.goodspartner.entity.Route;
import com.goodspartner.entity.RoutePoint;
import com.goodspartner.entity.RouteStatus;
import com.goodspartner.entity.Store;
import com.goodspartner.exception.ImppossbleRouteRecalculationException;
import com.goodspartner.mapper.RoutePointMapper;
import com.goodspartner.mapper.StoreMapper;
import com.goodspartner.repository.CarRepository;
import com.goodspartner.repository.OrderExternalRepository;
import com.goodspartner.service.GraphhopperService;
import com.goodspartner.service.RouteCalculationService;
import com.goodspartner.service.StoreService;
import com.goodspartner.service.dto.DistanceMatrix;
import com.goodspartner.service.dto.RouteMode;
import com.goodspartner.service.dto.RoutingSolution;
import com.goodspartner.service.dto.TSPSolution;
import com.goodspartner.service.dto.VRPSolution;
import com.goodspartner.service.google.GoogleTSPSolver;
import com.goodspartner.service.google.GoogleVRPSolver;
import com.google.common.annotations.VisibleForTesting;
import com.graphhopper.ResponsePath;
import com.graphhopper.util.Instruction;
import com.graphhopper.util.InstructionList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.goodspartner.entity.RoutePointStatus.PENDING;

@Slf4j
@Service
@RequiredArgsConstructor
public class DefaultRouteCalculationService implements RouteCalculationService {

    private static final int ARRIVAL_SIGN = 5;
    private static final int FINISH_SIGN = 4;
    // Props
    private final ClientRoutingProperties clientRoutingProperties;
    // Solvers
    private final GoogleVRPSolver vrpSolver;
    private final GoogleTSPSolver tspSolver;
    // Services
    private final StoreService storeService;
    private final GraphhopperService graphhopperService;
    // Mappers
    private final StoreMapper storeMapper;
    private final RoutePointMapper routePointMapper;
    // Repo
    private final CarRepository carRepository;
    private final OrderExternalRepository orderExternalRepository;

    @Override
    public List<Route> calculateRoutes(List<OrderExternal> orders, RouteMode routeMode) {

        List<OrderExternal> filteredOrders = orders
                .stream()
                .filter(orderExternal -> orderExternal.isFrozen() == routeMode.isCoolerNecessary())
                .toList();

        if (filteredOrders.isEmpty()) {
            log.info("No orders found for mode {}", routeMode.name());
            return Collections.emptyList();
        }

        Store store = storeService.getMainStore();
        List<Car> cars = carRepository.findByAvailableTrueAndCoolerIs(routeMode.isCoolerNecessary());
        List<RoutePoint> routePoints = mapToRoutePoints(filteredOrders);

        log.info("Start route optimisation for {} orders and cooler: {}", filteredOrders.size(), routeMode.isCoolerNecessary());

        List<MapPoint> mapPoints = getRouteMapPoints(store, routePoints);

        DistanceMatrix distanceMatrix = graphhopperService.getMatrix(mapPoints);
        log.info("DistanceMatrix has been calculated for {} route points", routePoints.size());

        VRPSolution solution = vrpSolver.solve(cars, routePoints, distanceMatrix);
        log.info("Finished route optimisation for {} orders and cooler: {}", filteredOrders.size(), routeMode.isCoolerNecessary());

        updateDroppedOrders(filteredOrders, solution);

        return solution.getRoutings()
                .stream()
                .map(vrpSolution -> mapToRoute(vrpSolution, store))
                .collect(Collectors.toList());

    }

    @Override
    public void recalculateRoute(Route route) {
        List<RoutePoint> routePoints = route.getRoutePoints();
        List<RoutePoint> completedRoutePoints = new ArrayList<>();
        List<RoutePoint> pendingRoutePoints = new ArrayList<>();
        RoutePoint currentRoutePoint = filterRoutePoints(routePoints, completedRoutePoints, pendingRoutePoints);

        List<MapPoint> mapPoints = getRouteMapPoints(currentRoutePoint, routePoints);
        DistanceMatrix distanceMatrix = graphhopperService.getMatrix(mapPoints);
        log.info("DistanceMatrix has been calculated for {} route points", mapPoints.size());

        LocalTime routeStartTime = getRouteStartTime(currentRoutePoint);

        log.info("Start route optimisation for car: {} and routePoints: {}", route.getCar() , pendingRoutePoints.size());
        TSPSolution solution = tspSolver.   solve(route.getCar(), routePoints, distanceMatrix, routeStartTime);
        log.info("Finished route optimisation for car: {} and routePoints: {}", route.getCar() , pendingRoutePoints.size());

        if (solution.getDroppedPoints().size() > 0) {
            throw new ImppossbleRouteRecalculationException(route);
        }

        // TODO we need to remap initial RoutePoints to update them and do not recreate the list of RoutePoints
        List<RoutePoint> recalculatedRoutePoints = solution.getRouting().getRoutePoints();
        List<RoutePoint> joinedRoutePoints = ListUtils.union(completedRoutePoints, recalculatedRoutePoints);
        route.setRoutePoints(joinedRoutePoints);
    }

    private LocalTime getRouteStartTime(RoutePoint currentRoutePoint) {
        return currentRoutePoint == null
                ? clientRoutingProperties.getDepotStartTime()
                : LocalTime.now();
    }

    private static RoutePoint filterRoutePoints(List<RoutePoint> routePoints,
                                                List<RoutePoint> completedRoutePoints,
                                                List<RoutePoint> pendingRoutePoints) {
        RoutePoint currentRoutePoint = null;
        for (RoutePoint routePoint : routePoints) {
            if (PENDING.equals(routePoint.getStatus())) {
                pendingRoutePoints.add(routePoint);
            } else {
                completedRoutePoints.add(routePoint);
                currentRoutePoint = routePoint; // last completed required to start with
            }
        }
        return currentRoutePoint;
    }

    @NotNull
    private List<MapPoint> getRouteMapPoints(Store store, List<RoutePoint> routePoints) {
        List<MapPoint> mapPoints = new ArrayList<>();
        mapPoints.add(storeMapper.getMapPoint(store));
        mapPoints.addAll(routePointMapper.toMapPoints(routePoints));
        return mapPoints;
    }

    @NotNull
    private List<MapPoint> getRouteMapPoints(RoutePoint currentRoutePoint, List<RoutePoint> routePoints) {
        MapPoint startMapPoint = currentRoutePoint == null
                ? storeMapper.getMapPoint(storeService.getMainStore())
                : routePointMapper.toMapPoint(currentRoutePoint);

        List<MapPoint> mapPoints = new ArrayList<>();
        mapPoints.add(startMapPoint);
        mapPoints.addAll(routePointMapper.toMapPoints(routePoints));
        return mapPoints;
    }

    private void updateDroppedOrders(List<OrderExternal> orders, VRPSolution solution) {
        if (solution.getDroppedPoints() == null || solution.getDroppedPoints().isEmpty()) {
            log.debug("No dropped RoutePoints found skipping Orders update");
            return;
        }

        Map<Long, OrderExternal> orderMap = orders.stream()
                .collect(Collectors.toMap(OrderExternal::getId, Function.identity()));

        solution.getDroppedPoints()
                .stream()
                .map(RoutePoint::getOrders)
                .flatMap(Collection::stream)
                .map(orderDto -> orderMap.get(orderDto.getId()))
                .forEach(orderExternal -> orderExternal.setDropped(true));

        orderExternalRepository.saveAll(orders);
    }

    @VisibleForTesting
    Route mapToRoute(RoutingSolution routingSolution, Store store) {

        List<RoutePoint> routePoints = routingSolution.getRoutePoints();
        List<MapPoint> mapPoints = getRouteMapPoints(store, routePoints);
        // TODO check if VRPSolver has calculation with return
        mapPoints.add(storeMapper.getMapPoint(store)); // Return back to Store

        ResponsePath routePath = graphhopperService.getRoute(mapPoints);

        Route route = new Route();
        route.setStatus(RouteStatus.DRAFT);
        route.setTotalWeight(getRouteOrdersTotalWeight(routePoints));
        route.setTotalPoints(routePoints.size());
        route.setTotalOrders(getTotalOrders(routePoints));
        route.setRoutePoints(routePoints);
        route.setOptimization(true);
        route.setCar(routingSolution.getCar());
        route.setStore(store);
        route.setDistance(BigDecimal.valueOf(routePath.getDistance() / 1000)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue());
        int totalWaitTimeMin = clientRoutingProperties.getUnloadingTimeMinutes() * routePoints.size();
        route.setEstimatedTime(Duration.ofMillis(routePath.getTime()).toMinutes() + totalWaitTimeMin);

//        TODO date should be a date of Delivery or route.startTime should be only LocalTime without date
//        route.setStartTime(LocalDateTime.of(LocalDate.now(), routingSolution.getRouteStartTimeFromDepot()));

        return route;
    }

    @VisibleForTesting
    Route reorderRoutePoints(Route route, LinkedList<RoutePointDto> routePointDtos) {
        List<MapPoint> mapPoints = routePointDtos.stream().map(RoutePointDto::getMapPoint).toList();

        ResponsePath routePath = graphhopperService.getRoute(mapPoints);

        route.setDistance(BigDecimal.valueOf(routePath.getDistance() / 1000)
                .setScale(2, RoundingMode.HALF_UP).doubleValue());
        route.setEstimatedTime(Duration.ofMillis(routePath.getTime()).toMinutes());

        List<Long> legTimes = new ArrayList<>();
        InstructionList pathInstructions = routePath.getInstructions();

        int index = 1;
        for (Instruction instruction : pathInstructions) {
            long legTime = instruction.getTime();
            legTimes.add(legTime);

            if (instruction.getSign() == ARRIVAL_SIGN || instruction.getSign() == FINISH_SIGN) {
                long arrivedTime = legTimes.stream().mapToLong(Long::longValue).sum();
                long arrivedTimeInMinutes = Duration.ofMillis(arrivedTime).toMinutes();
                legTimes.clear();

                routePointDtos.get(index).setRoutePointDistantTime(arrivedTimeInMinutes);
                index++;
            }
        }
        route.setRoutePoints(routePointMapper.toRoutePointList(routePointDtos));

        return route;
    }

    @VisibleForTesting
    int getTotalOrders(List<RoutePoint> routePoints) {
        return routePoints.stream()
                .map(routePoint -> routePoint.getOrders().size())
                .mapToInt(size -> size).sum();
    }

    @VisibleForTesting
    double getRouteOrdersTotalWeight(List<RoutePoint> routePoints) {
        return BigDecimal.valueOf(routePoints.stream()
                        .map(RoutePoint::getAddressTotalWeight)
                        .collect(Collectors.summarizingDouble(amount -> amount)).getSum())
                .setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    /**
     * We should group by clientName + valid address -> meaning that:
     *  1. Several clients on the same mapPoint should be treated as the different RoutePoints
     *  2. Same clients on the same mapPoint should be treated as one RoutePoint assuming driver will be able to complete and finish client delivery at once
     */
    @VisibleForTesting
    List<RoutePoint> mapToRoutePoints(List<OrderExternal> orders) {
        List<RoutePoint> routePointList = new ArrayList<>();


        Map<Pair<String, MapPoint>, List<OrderExternal>> addressOrderMap = orders
                .stream()
                .collect(Collectors.groupingBy(orderExternal ->
                        Pair.of(orderExternal.getClientName(), orderExternal.getMapPoint()),
                        Collectors.toList()));

        addressOrderMap.forEach((pair, orderList) -> {

            double addressTotalWeight = orderList.stream()
                    .map(OrderExternal::getOrderWeight)
                    .collect(Collectors.summarizingDouble(amount -> amount)).getSum();

            RoutePoint routePoint = new RoutePoint();
            routePoint.setStatus(PENDING);
            routePoint.setOrders(orderList);
            routePoint.setAddressTotalWeight(addressTotalWeight);

            routePoint.setMapPoint(pair.getRight());

            // TODO : issue #205 how to represent several orders per one client. Now we take first
            // TODO : stream over all order and choose the one where the values are not default one
            if (orderList.size() > 1) {
                log.info("Collapsed {} orders into the one routePoint: {}", orderList.size(),
                        orderList.stream().map(OrderExternal::getOrderNumber).collect(Collectors.toList()));
                if (log.isDebugEnabled()) {
                    orderList.forEach(orderExternal -> log.debug("{}", orderExternal));
                }
            }
            OrderExternal orderExternal = orderList.get(0);
            routePoint.setDeliveryStart(orderExternal.getDeliveryStart());
            routePoint.setDeliveryEnd(orderExternal.getDeliveryFinish());
            routePoint.setClientName(orderExternal.getClientName());
            routePoint.setAddress(orderExternal.getAddress());

            routePointList.add(routePoint);
        });

        return routePointList;
    }
}
