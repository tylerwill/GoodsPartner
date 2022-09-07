package com.goodspartner.service.impl;

import com.goodspartner.dto.MapPoint;
import com.goodspartner.dto.OrderDto;
import com.goodspartner.dto.RoutePointDto;
import com.goodspartner.dto.StoreDto;
import com.goodspartner.entity.RouteStatus;
import com.goodspartner.mapper.CalculationRoutePointMapper;
import com.goodspartner.service.CalculateRouteService;
import com.goodspartner.service.CarLoadingService;
import com.goodspartner.service.GraphhopperService;
import com.goodspartner.web.controller.response.RoutesCalculation;
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
    private final CalculationRoutePointMapper calculationRoutePointMapper;
    private final CarLoadingService carLoadingService;
    private final GraphhopperService graphhopperService;


    @Override
    public List<RoutesCalculation.RouteDto> calculateRoutes(List<OrderDto> orders, StoreDto storeDto) {
        List<RoutePointDto> routePoints = calculationRoutePointMapper.mapOrders(orders);
        List<CarLoadingService.CarRoutesDto> carRoutesDtos = carLoadingService.loadCars(storeDto, routePoints);

        return carRoutesDtos.stream()
                .map(car -> calculateRoute(car, storeDto))
                .collect(Collectors.toList());
    }

    @VisibleForTesting
    RoutesCalculation.RouteDto calculateRoute(CarLoadingService.CarRoutesDto carLoad, StoreDto storeDto) {
        List<RoutePointDto> routePoints = carLoad.getRoutePoints();

        List<MapPoint> mapPoints = new ArrayList<>();
        mapPoints.add(storeDto.getMapPoint());
        mapPoints.addAll(carLoad.getRoutePoints().stream().map(RoutePointDto::getMapPoint).toList());

        ResponsePath route = graphhopperService.getRoute(mapPoints);

        //TODO: RoutePointDistantTime ?
        return RoutesCalculation.RouteDto.builder()
                .id(carLoad.getCar().getId())
                .status(RouteStatus.DRAFT)
                .totalWeight(getRouteOrdersTotalWeight(routePoints))
                .totalPoints(routePoints.size())
                .totalOrders(getTotalOrders(routePoints))
                .distance(route.getDistance() / 1000)
                .estimatedTime(Duration.ofMillis(route.getTime()).toMinutes())
                .storeName(storeDto.getName())
                .storeAddress(storeDto.getMapPoint().getAddress())
                .routePoints(routePoints)
                .car(carLoad.getCar())
                .build();
    }

    @VisibleForTesting
    int getTotalOrders(List<RoutePointDto> routePoints) {
        return routePoints.stream()
                .map(routePointDto -> routePointDto.getOrders().size())
                .mapToInt(size -> size).sum();
    }

    @VisibleForTesting
    double getRouteOrdersTotalWeight(List<RoutePointDto> routePoints) {
        return BigDecimal.valueOf(routePoints.stream()
                        .map(RoutePointDto::getAddressTotalWeight)
                        .collect(Collectors.summarizingDouble(amount -> amount)).getSum())
                .setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}
