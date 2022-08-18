package com.goodspartner.service.impl;

import com.goodspartner.dto.OrderDto;
import com.goodspartner.dto.RoutePointDto;
import com.goodspartner.dto.StoreDto;
import com.goodspartner.entity.RouteStatus;
import com.goodspartner.mapper.CalculationRoutePointMapper;
import com.goodspartner.service.CalculateRouteService;
import com.goodspartner.service.CarLoadingService;
import com.goodspartner.service.GoogleApiService;
import com.goodspartner.service.impl.util.GoogleApiHelper;
import com.goodspartner.web.controller.response.RoutesCalculation;
import com.google.common.annotations.VisibleForTesting;
import com.google.maps.model.DirectionsRoute;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DefaultCalculateRouteService implements CalculateRouteService {
    private final CalculationRoutePointMapper calculationRoutePointMapper;
    private final CarLoadingService carLoadingService;
    private final GoogleApiService googleApiService;
    private final GoogleApiHelper googleApiHelper;

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
        List<String> pointsAddresses = routePoints.stream()
                .map(RoutePointDto::getAddress).toList();

        DirectionsRoute route = googleApiService.getDirectionRoute(storeDto.getAddress(), pointsAddresses);

        double totalDistance = googleApiHelper.getRouteTotalDistance(route);
        long totalTime = googleApiHelper.getRouteTotalTime(route);
        googleApiHelper.addRoutPointDistantTime(routePoints, route);

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
