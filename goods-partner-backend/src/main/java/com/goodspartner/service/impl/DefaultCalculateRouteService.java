package com.goodspartner.service.impl;

import com.goodspartner.dto.OrderDto;
import com.goodspartner.dto.RoutePointDto;
import com.goodspartner.dto.StoreDto;
import com.goodspartner.entity.RouteStatus;
import com.goodspartner.mapper.CalculationRoutePointMapper;
import com.goodspartner.service.CalculateRouteService;
import com.goodspartner.service.CarLoadingService;
import com.goodspartner.service.GoogleApiService;
import com.goodspartner.web.controller.response.RoutesCalculation;
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
public class DefaultCalculateRouteService implements CalculateRouteService {

    private final CarLoadingService carLoadingService;
    private final CalculationRoutePointMapper calculationRoutePointMapper;
    private final GoogleApiService googleApiService;

    @Override
    public List<RoutesCalculation.RouteDto> calculateRoutes(List<OrderDto> orders, StoreDto storeDto) {
        List<RoutePointDto> routePoints = calculationRoutePointMapper.mapOrders(orders);
        List<CarLoadingService.CarRoutesDto> carRoutesDtos = carLoadingService.loadCars(storeDto, routePoints);

        return carRoutesDtos.stream()
                .map(car -> calculateRoute(car, storeDto))
                .collect(Collectors.toList());
    }

    private RoutesCalculation.RouteDto calculateRoute(CarLoadingService.CarRoutesDto carLoad, StoreDto storeDto) {
        List<RoutePointDto> routePoints = carLoad.getRoutePoints();
        List<String> pointsAddresses = routePoints.stream()
                .map(RoutePointDto::getAddress).toList();
        DirectionsRoute route = googleApiService.getDirectionRoute(storeDto.getAddress(), pointsAddresses);
        double totalDistance = getRouteTotalDistance(route);
        long totalTime = getRouteTotalTime(route);
        addRoutPointDistantTime(routePoints, route);

        return RoutesCalculation.RouteDto.builder()
                .id(carLoad.getCar().getId())
                .status(RouteStatus.DRAFT)
                .totalWeight(getRouteOrdersTotalWeight(routePoints))
                .totalPoints(routePoints.size())
                .totalOrders(getTotalOrders(routePoints))
                .distance(totalDistance)
                .estimatedTime(Duration.ofSeconds(totalTime).toMinutes())
                .storeName(storeDto.getName())
                .storeAddress(storeDto.getAddress())
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
            routePoints.get(i).setRoutePointDistantTime(Duration.ofSeconds(duration).toMinutes());
        }
    }
}
