package com.goodspartner.service.impl;

import com.goodspartner.configuration.properties.ClientRoutingProperties;
import com.goodspartner.dto.MapPoint;
import com.goodspartner.dto.RoutePointDto;
import com.goodspartner.entity.Car;
import com.goodspartner.entity.OrderExternal;
import com.goodspartner.entity.Route;
import com.goodspartner.entity.RoutePoint;
import com.goodspartner.entity.RoutePointStatus;
import com.goodspartner.entity.RouteStatus;
import com.goodspartner.entity.Store;
import com.goodspartner.mapper.RoutePointMapper;
import com.goodspartner.mapper.StoreMapper;
import com.goodspartner.repository.CarRepository;
import com.goodspartner.repository.OrderExternalRepository;
import com.goodspartner.service.GraphhopperService;
import com.goodspartner.service.RouteCalculationService;
import com.goodspartner.service.StoreService;
import com.goodspartner.service.VRPSolver;
import com.goodspartner.service.dto.RouteMode;
import com.goodspartner.service.dto.RoutingSolution;
import com.goodspartner.service.dto.VRPSolution;
import com.google.common.annotations.VisibleForTesting;
import com.graphhopper.ResponsePath;
import com.graphhopper.util.Instruction;
import com.graphhopper.util.InstructionList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.goodspartner.entity.ExcludeReasons.DROPPED_ORDER;

@Slf4j
@Service
@RequiredArgsConstructor
public class DefaultRouteCalculationService implements RouteCalculationService {

    private static final int ARRIVAL_SIGN = 5;
    private static final int FINISH_SIGN = 4;
    // Props
    private final ClientRoutingProperties clientRoutingProperties;
    // Services
    private final StoreService storeService;
    private final VRPSolver vrpSolver;
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
        VRPSolution solution = vrpSolver.optimize(cars, store, routePoints);
        log.info("Finished route optimisation for {} orders and cooler: {}", filteredOrders.size(), routeMode.isCoolerNecessary());

        updateDroppedOrders(filteredOrders, solution);

        return solution.getRoutings()
                .stream()
                .map(vrpSolution -> mapToRoute(vrpSolution, store))
                .collect(Collectors.toList());

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
                .forEach(orderExternal -> {
                    orderExternal.setDropped(true);
                    orderExternal.setRoutePoint(null); // Do not save droppedRoutePoints
                    orderExternal.setExcludeReason(DROPPED_ORDER.formatWithOrderNumber(orderExternal.getOrderNumber()));
                });

        orderExternalRepository.saveAll(orders);
    }

    @VisibleForTesting
    Route mapToRoute(RoutingSolution routingSolution, Store store) {

        List<MapPoint> mapPoints = new ArrayList<>();
        mapPoints.add(storeMapper.getMapPoint(store));
        mapPoints.addAll(routePointMapper.toMapPoints(routingSolution.getRoutePoints()));
        mapPoints.add(storeMapper.getMapPoint(store)); // Return back to Store

        ResponsePath routePath = graphhopperService.getRoute(mapPoints);

        List<RoutePoint> routePoints = routingSolution.getRoutePoints();

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
    Route recalculateRoute(Route route, LinkedList<RoutePointDto> routePointDtos) {
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
            routePoint.setStatus(RoutePointStatus.PENDING);
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
