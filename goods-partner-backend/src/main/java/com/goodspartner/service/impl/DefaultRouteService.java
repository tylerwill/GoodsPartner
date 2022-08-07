package com.goodspartner.service.impl;

import com.goodspartner.dto.CarRoutesDto;
import com.goodspartner.dto.RouteDto;
import com.goodspartner.dto.RoutePointDto;
import com.goodspartner.entity.Order;
import com.goodspartner.entity.RouteStatus;
import com.goodspartner.factory.Store;
import com.goodspartner.mapper.RoutePointMapper;
import com.goodspartner.service.CarLoadingService;
import com.goodspartner.service.GoogleApiService;
import com.goodspartner.service.RouteService;
import com.google.common.annotations.VisibleForTesting;
import com.google.maps.model.DirectionsRoute;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DefaultRouteService implements RouteService {

    private final CarLoadingService carLoadingService;
    private final RoutePointMapper routePointMapper;
    private final GoogleApiService googleApiService;

    @Override
    public List<RouteDto> calculateRoutes(List<Order> orders, Store store) {
        List<RoutePointDto> routePoints = routePointMapper.mapOrders(orders);
        List<CarRoutesDto> carRoutesDtos = carLoadingService.loadCars(store, routePoints);

        return carRoutesDtos.stream()
                .map(car -> calculateRoute(car, store))
                .collect(Collectors.toList());
    }

    private RouteDto calculateRoute(CarRoutesDto carLoad, Store store) {
        List<RoutePointDto> routePoints = carLoad.getRoutePoints();
        List<String> pointsAddresses = routePoints.stream()
                .map(RoutePointDto::getAddress).toList();
        DirectionsRoute route = googleApiService.getDirectionRoute(store.getAddress(), pointsAddresses);
        double totalDistance = getRouteTotalDistance(route);
        long totalTime = getRouteTotalTime(route);
        addRoutPointDistantTime(routePoints, route);

        return RouteDto.builder()
                .routeId(carLoad.getCar().getId())
                .status(RouteStatus.DRAFT)
                .totalWeight(getRouteOrdersTotalWeight(routePoints))
                .totalPoints(routePoints.size())
                .totalOrders(getTotalOrders(routePoints))
                .distance(totalDistance)
                .estimatedTime(Duration.ofSeconds(totalTime))
                .storeName(store.getName())
                .storeAddress(store.getAddress())
                .routePoints(routePoints)
                .car(carLoad.getCar())
                .build();
    }

    private int getTotalOrders(List<RoutePointDto> routePoints) {
        return routePoints.stream()
                .map(routePointDto -> routePointDto.getOrders().size())
                .mapToInt(size -> size).sum();
    }

    private double getRouteOrdersTotalWeight(List<RoutePointDto> routePoints) {
        return BigDecimal.valueOf(routePoints.stream()
                .map(RoutePointDto::getAddressTotalWeight)
                .collect(Collectors.summarizingDouble(amount -> amount)).getSum())
                .setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    private double getRouteTotalDistance(DirectionsRoute route) {
        return BigDecimal.valueOf(Arrays.stream(route.legs).toList().stream()
                .collect(Collectors.summarizingLong(leg -> leg.distance.inMeters)).getSum() / 1000d)
                .setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    private long getRouteTotalTime(DirectionsRoute route) {
        return Arrays.stream(route.legs).toList().stream()
                .collect(Collectors.summarizingLong(leg -> leg.duration.inSeconds)).getSum();
    }

    @VisibleForTesting
    void addRoutPointDistantTime(List<RoutePointDto> routePoints, DirectionsRoute route) {
        //TODO: Rework on stream
        for (int i = 0; i < routePoints.size(); i++) {
            long duration = route.legs[i].duration.inSeconds;
            routePoints.get(i).setRoutePointDistantTime(Duration.ofSeconds(duration));
        }
    }
}
