package com.goodspartner.service.impl;

import com.goodspartner.dto.CarRouteComposition;
import com.goodspartner.dto.MapPoint;
import com.goodspartner.dto.StoreDto;
import com.goodspartner.entity.OrderExternal;
import com.goodspartner.entity.Route;
import com.goodspartner.entity.RoutePoint;
import com.goodspartner.entity.RouteStatus;
import com.goodspartner.service.CalculateRouteService;
import com.goodspartner.service.CarLoadingService;
import com.goodspartner.service.GraphhopperService;
import com.goodspartner.util.RoutePointCalculator;
import com.google.common.annotations.VisibleForTesting;
import com.graphhopper.ResponsePath;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DefaultCalculateRouteService implements CalculateRouteService {

    private final RoutePointCalculator routePointCalculator;
    private final CarLoadingService carLoadingService;
    private final GraphhopperService graphhopperService;

    @Override
    public List<Route> calculateRoutes(List<OrderExternal> orders, StoreDto storeDto) {
        List<RoutePoint> routePoints = routePointCalculator.mapOrders(orders);
        List<CarRouteComposition> carRoutes = carLoadingService.loadCars(storeDto, routePoints);

        return carRoutes.stream()
                .map(carRoute -> calculateRoute(carRoute, storeDto))
                .collect(Collectors.toList());
    }

    @VisibleForTesting
    Route calculateRoute(CarRouteComposition carRoute, StoreDto storeDto) {
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
}
