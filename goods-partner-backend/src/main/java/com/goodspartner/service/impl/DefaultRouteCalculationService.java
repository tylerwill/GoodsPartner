package com.goodspartner.service.impl;

import com.goodspartner.dto.MapPoint;
import com.goodspartner.dto.Product;
import com.goodspartner.service.dto.VRPSolution;
import com.goodspartner.service.dto.RoutingSolution;
import com.goodspartner.entity.AddressExternal;
import com.goodspartner.entity.Car;
import com.goodspartner.entity.OrderExternal;
import com.goodspartner.entity.Route;
import com.goodspartner.entity.RoutePoint;
import com.goodspartner.entity.RoutePointStatus;
import com.goodspartner.entity.RouteStatus;
import com.goodspartner.entity.Store;
import com.goodspartner.mapper.StoreMapper;
import com.goodspartner.repository.CarRepository;
import com.goodspartner.service.GraphhopperService;
import com.goodspartner.service.RouteCalculationService;
import com.goodspartner.service.StoreService;
import com.goodspartner.service.VRPSolver;
import com.goodspartner.service.dto.RouteMode;
import com.google.common.annotations.VisibleForTesting;
import com.graphhopper.ResponsePath;
import com.graphhopper.util.Instruction;
import com.graphhopper.util.InstructionList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.goodspartner.service.google.GoogleVRPSolver.SERVICE_TIME_AT_LOCATION_MIN;

@Slf4j
@Service
@RequiredArgsConstructor
public class DefaultRouteCalculationService implements RouteCalculationService {

    private final StoreService storeService;
    private final StoreMapper storeMapper;
    private final VRPSolver vrpSolver;
    private final GraphhopperService graphhopperService;
    private final CarRepository carRepository;
    private final int ARRIVAL_SIGN = 5;
    private final int FINISH_SIGN = 4;

    @Override
    public List<Route> calculateRoutes(List<OrderExternal> orders, RouteMode routeMode) {

        // TODO require clarification with client. We want to highlight orders outside of the Kyiv in response
        List<OrderExternal> kyivOrders = orders.stream()
                .filter(orderExternal -> orderExternal.getAddressExternal().getValidAddress().contains("Київська обл")
                        || orderExternal.getAddressExternal().getValidAddress().contains("Київ"))
                .toList();

        List<OrderExternal> filteredOrders = kyivOrders
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

        log.info("Start route optimisation for {} orders", orders.size());
        VRPSolution solution = vrpSolver.optimize(cars, storeMapper.getMapPoint(store), routePoints);
        log.info("Finished route optimisation for {} orders", orders.size());

        updateDroppedOrders(filteredOrders, solution);

        return solution.getRoutings().stream()
                .map(vrpSolution -> mapToRoute(vrpSolution, store))
                .collect(Collectors.toList());

    }

    private void updateDroppedOrders(List<OrderExternal> orders, VRPSolution solution) {
        if (solution.getDroppedPoints() == null || solution.getDroppedPoints().isEmpty()) {
            return;
        }

        Map<Integer, OrderExternal> orderMap = orders.stream()
                .collect(Collectors.toMap(OrderExternal::getId, Function.identity()));

        solution.getDroppedPoints()
                .stream()
                .map(RoutePoint::getOrders)
                .flatMap(Collection::stream)
                .map(orderReference -> orderMap.get(orderReference.getId()))
                .forEach(orderExternal -> orderExternal.setDropped(true));
    }

    @VisibleForTesting
    Route mapToRoute(RoutingSolution RoutingSolution, Store store) {

        List<MapPoint> mapPoints = new ArrayList<>();
        mapPoints.add(storeMapper.getMapPoint(store));
        mapPoints.addAll(RoutingSolution.getRoutePoints().stream().map(RoutePoint::getMapPoint).toList());
        mapPoints.add(storeMapper.getMapPoint(store)); // Return back to Store

        ResponsePath routePath = graphhopperService.getRoute(mapPoints);

        List<RoutePoint> routePoints = RoutingSolution.getRoutePoints();

        Route route = new Route();
        route.setStatus(RouteStatus.DRAFT);
        route.setTotalWeight(getRouteOrdersTotalWeight(routePoints));
        route.setTotalPoints(routePoints.size());
        route.setTotalOrders(getTotalOrders(routePoints));
        route.setDistance(BigDecimal.valueOf(routePath.getDistance() / 1000)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue());

        int totalWaitTimeMin = SERVICE_TIME_AT_LOCATION_MIN * routePoints.size();
        route.setEstimatedTime(Duration.ofMillis(routePath.getTime()).toMinutes() + totalWaitTimeMin);

        route.setRoutePoints(routePoints);
        route.setOptimization(true);
        route.setCar(RoutingSolution.getCar());
        route.setStore(store);

        return route;
    }

    @VisibleForTesting
    Route recalculateRoute(Route route, LinkedList<RoutePoint> routePoints) {
        List<MapPoint> mapPoints = routePoints.stream().map(RoutePoint::getMapPoint).toList();

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

                routePoints.get(index).setRoutePointDistantTime(arrivedTimeInMinutes);
                index++;
            }
        }
        route.setRoutePoints(routePoints);
        return route;
    }

    @VisibleForTesting
    int getTotalOrders(List<RoutePoint> routePoints) {
        return routePoints.stream()
                .map(routePointDto -> routePointDto.getOrders().size())
                .mapToInt(size -> size).sum();
    }

    @VisibleForTesting
    double getRouteOrdersTotalWeight(List<RoutePoint> routePoints) {
        return BigDecimal.valueOf(routePoints.stream()
                        .map(RoutePoint::getAddressTotalWeight)
                        .collect(Collectors.summarizingDouble(amount -> amount)).getSum())
                .setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    @VisibleForTesting
    List<RoutePoint> mapToRoutePoints(List<OrderExternal> orders) {
        List<RoutePoint> routePointList = new ArrayList<>();

        Map<AddressExternal, List<OrderExternal>> addressOrderMap = orders
                .stream()
                .collect(Collectors.groupingBy(
                        OrderExternal::getAddressExternal,
                        LinkedHashMap::new,
                        Collectors.toList()));

        addressOrderMap.forEach((addressExternal, orderList) -> {
            List<RoutePoint.OrderReference> orderReferences = mapOrdersAddress(orderList);

            double addressTotalWeight = orderReferences.stream()
                    .map(RoutePoint.OrderReference::getOrderTotalWeight)
                    .collect(Collectors.summarizingDouble(amount -> amount)).getSum();

            AddressExternal.OrderAddressId orderAddressId = addressExternal.getOrderAddressId();

            RoutePoint routePoint = new RoutePoint();
            routePoint.setId(UUID.randomUUID());
            routePoint.setStatus(RoutePointStatus.PENDING);
            routePoint.setAddress(orderAddressId.getOrderAddress());
            routePoint.setClientName(orderAddressId.getClientName());
            routePoint.setOrders(orderReferences);
            routePoint.setAddressTotalWeight(addressTotalWeight);
            routePoint.setMapPoint(getMapPoint(addressExternal));

            // TODO : issue #205 how to represent several orders per one client. Now we take first
            OrderExternal firstOrder = orderList.get(0);
            routePoint.setDeliveryStart(firstOrder.getDeliveryStart());
            routePoint.setDeliveryEnd(firstOrder.getDeliveryFinish());

            routePointList.add(routePoint);
        });
        return routePointList;
    }

    private MapPoint getMapPoint(AddressExternal addressExternal) {
        return MapPoint.builder()
                .status(MapPoint.AddressStatus.KNOWN)
                .address(addressExternal.getValidAddress())
                .longitude(addressExternal.getLongitude())
                .latitude(addressExternal.getLatitude())
                .build();
    }

    private List<RoutePoint.OrderReference> mapOrdersAddress(List<OrderExternal> orders) {
        return orders.stream()
                .map(this::mapOrderAddress)
                .collect(Collectors.toList());
    }

    private RoutePoint.OrderReference mapOrderAddress(OrderExternal order) {
        List<Product> products = order.getProducts();
        double orderTotalWeight = getOrderTotalWeight(products);

        RoutePoint.OrderReference orderReference = new RoutePoint.OrderReference();
        orderReference.setId(order.getId());
        orderReference.setOrderNumber(String.valueOf(order.getOrderNumber()));
        orderReference.setComment(order.getComment());
        orderReference.setOrderTotalWeight(orderTotalWeight);

        return orderReference;
    }

    private double getOrderTotalWeight(List<Product> orderedProducts) {
        return BigDecimal.valueOf(orderedProducts.stream()
                        .map(Product::getTotalProductWeight)
                        .collect(Collectors.summarizingDouble(weight -> weight)).getSum())
                .setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}
