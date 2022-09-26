package com.goodspartner.service.impl;

import com.goodspartner.dto.MapPoint;
import com.goodspartner.dto.Product;
import com.goodspartner.dto.StoreDto;
import com.goodspartner.dto.VRPSolution;
import com.goodspartner.entity.AddressExternal;
import com.goodspartner.entity.Car;
import com.goodspartner.entity.OrderExternal;
import com.goodspartner.entity.Route;
import com.goodspartner.entity.RoutePoint;
import com.goodspartner.entity.RoutePointStatus;
import com.goodspartner.entity.RouteStatus;
import com.goodspartner.repository.CarRepository;
import com.goodspartner.service.GraphhopperService;
import com.goodspartner.service.RouteCalculationService;
import com.goodspartner.service.StoreService;
import com.goodspartner.service.VRPSolver;
import com.goodspartner.service.dto.RouteMode;
import com.google.common.annotations.VisibleForTesting;
import com.graphhopper.ResponsePath;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DefaultRouteCalculationService implements RouteCalculationService {

    private final StoreService storeFactory;
    private final VRPSolver vrpSolver;
    private final GraphhopperService graphhopperService;
    private final CarRepository carRepository;

    @Override
    public List<Route> calculateRoutes(List<OrderExternal> orders, RouteMode coolerRoute) {

        List<OrderExternal> filteredOrders = orders
                .stream()
                .filter(orderExternal -> orderExternal.isFrozen() == coolerRoute.isCoolerNecessary())
                .toList();

        StoreDto store = storeFactory.getMainStore();
        List<Car> cars = carRepository.findByAvailableTrueAndCoolerIs(coolerRoute.isCoolerNecessary());
        List<RoutePoint> routePoints = mapToRoutePoints(filteredOrders);

        List<VRPSolution> vrpSolutions = vrpSolver.optimize(cars, store, routePoints);
        return vrpSolutions.stream()
                .map(vrpSolution -> mapToRoute(vrpSolution, store))
                .collect(Collectors.toList());

    }

    @VisibleForTesting
    Route mapToRoute(VRPSolution carRoute, StoreDto storeDto) {
        List<RoutePoint> routePoints = carRoute.getRoutePoints();

        List<MapPoint> mapPoints = new ArrayList<>();
        mapPoints.add(storeDto.getMapPoint());
        mapPoints.addAll(carRoute.getRoutePoints().stream().map(RoutePoint::getMapPoint).toList());

        ResponsePath routePath = graphhopperService.getRoute(mapPoints);

        Route route = new Route();
        route.setStatus(RouteStatus.DRAFT);
        route.setTotalWeight(getRouteOrdersTotalWeight(routePoints));
        route.setTotalPoints(routePoints.size());
        route.setTotalOrders(getTotalOrders(routePoints));
        route.setDistance(BigDecimal.valueOf(routePath.getDistance() / 1000)
                .setScale(2, RoundingMode.HALF_UP).doubleValue());
        route.setEstimatedTime(Duration.ofMillis(routePath.getTime()).toMinutes());
        route.setStoreName(storeDto.getName());
        route.setStoreAddress(storeDto.getMapPoint().getAddress());
        route.setRoutePoints(routePoints);
        route.setOptimization(true);
        route.setCar(carRoute.getCar());

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
