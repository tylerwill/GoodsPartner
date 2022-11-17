package com.goodspartner.service.impl;

import com.goodspartner.dto.CarDto;
import com.goodspartner.dto.DeliveryDto;
import com.goodspartner.dto.RouteDto;
import com.goodspartner.entity.RoutePointStatus;
import com.goodspartner.service.CarService;
import com.goodspartner.service.DeliveryService;
import com.goodspartner.service.RouteService;
import com.goodspartner.service.StatisticsService;
import com.goodspartner.web.controller.response.statistics.CarStatisticsResponse;
import com.goodspartner.web.controller.response.statistics.DailyCarStatisticsResponse;
import com.goodspartner.web.controller.response.statistics.StatisticsResponse;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.goodspartner.entity.DeliveryStatus.COMPLETED;

@Service
@RequiredArgsConstructor
public class DefaultStatisticsService implements StatisticsService {

    private final DeliveryService deliveryService;
    private final RouteService routeService;
    private final CarService carService;

    @Override
    public StatisticsResponse getStatistics(LocalDate dateFrom, LocalDate dateTo) {

        List<DeliveryDto> deliveries = getCompletedDeliveriesInRange(dateFrom, dateTo);
        long averageDeliveryDuration = getAverageDeliveryDuration(deliveries);

        List<RouteDto> routes = getRoutes(deliveries);

        CalculationRoutesResult calculationRoutesResult = calculateRoutesStatistic(routes);

        Map<LocalDate, List<RouteDto>> collectedRoutesByDate = collectRoutesByDate(deliveries);

        // Plots data
        Map<LocalDate, Integer> routesPerDay = collectedRoutesByDate.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, m -> m.getValue().size()));

        Map<LocalDate, Integer> ordersPerDay = collectedRoutesByDate.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, m -> m.getValue().stream()
                        .map(RouteDto::getTotalOrders)
                        .mapToInt(Integer::intValue)
                        .sum()));

        Map<LocalDate, Integer> weightPerDay = collectedRoutesByDate.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, m -> m.getValue().stream()
                        .map(RouteDto::getTotalWeight)
                        .mapToInt(Double::intValue)
                        .sum()));

        Map<LocalDate, Integer> fuelConsumptionPerDay = collectedRoutesByDate.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, m -> m.getValue().stream()
                        .map(route -> route.getCar().getTravelCost() * route.getDistance() / 100)
                        .mapToInt(Double::intValue)
                        .sum()));

        return StatisticsResponse.builder()
                .routeCount(routes.size())
                .orderCount(calculationRoutesResult.ordersCount)
                .weight(calculationRoutesResult.totalWeight)
                .fuelConsumption(calculationRoutesResult.fuelConsumption)
                .averageDeliveryDuration(averageDeliveryDuration)
                .routesForPeriodPerDay(getDataForPeriodPerDay(dateFrom, dateTo, routesPerDay))
                .ordersForPeriodPerDay(getDataForPeriodPerDay(dateFrom, dateTo, ordersPerDay))
                .weightForPeriodPerDay(getDataForPeriodPerDay(dateFrom, dateTo, weightPerDay))
                .fuelConsumptionForPeriodPerDay(getDataForPeriodPerDay(dateFrom, dateTo, fuelConsumptionPerDay))
                .build();
    }

    private Map<LocalDate, Integer> getDataForPeriodPerDay(LocalDate dateFrom,
                                                           LocalDate dateTo,
                                                           Map<LocalDate, Integer> mappingData) {
        Map<LocalDate, Integer> dataForPeriodPerDay = new LinkedHashMap<>();
        LocalDate day = dateFrom;
        while (day.isBefore(dateTo) || day.isEqual(dateTo)) {
            Integer count = mappingData.get(day);
            dataForPeriodPerDay.put(day, count != null ? count : 0);
            day = day.plusDays(1);
        }
        return dataForPeriodPerDay;
    }

    @Override
    public CarStatisticsResponse getCarStatistics(LocalDate dateFrom, LocalDate dateTo, int carId) {

        CarDto car = getCar(carId);
        List<DeliveryDto> deliveries = getCompletedDeliveriesInRange(dateFrom, dateTo);

        List<RouteDto> routes = getRoutes(deliveries)
                .stream()
                .filter(routeDto -> routeDto.getCar().getId() == carId)
                .toList();

        CalculationRoutesResult calculationRoutesResult = calculateRoutesStatistic(routes, car);

        return CarStatisticsResponse.builder()
                .routes(routes)
                .totalTimeInRoutes(calculationRoutesResult.totalTimeInRoutes)
                .orderCount(calculationRoutesResult.ordersCount)
                .weight(calculationRoutesResult.totalWeight)
                .fuelConsumption(calculationRoutesResult.fuelConsumption)
                .incompleteRoutePointsCount(calculationRoutesResult.incompleteRoutePointsCount)
                .car(car)
                .build();
    }

    @Override
    public DailyCarStatisticsResponse getDailyCarStatistics(LocalDate date, int carId) {

        CarDto car = getCar(carId);
        DeliveryDto delivery = getCompletedDelivery(date);

        RouteDto routeResponse = routeService.findByDeliveryId(delivery.getId()).stream()
                .filter(route -> route.getCar().getId() == carId)
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);// This car was not used at this date

        return DailyCarStatisticsResponse.builder()
                .car(car)
                .orderCount(routeResponse.getTotalOrders())
                .build();
    }

    private CarDto getCar(int carId) {
        return carService.findById(carId);
    }

    private List<DeliveryDto> getCompletedDeliveriesInRange(LocalDate dateFrom, LocalDate dateTo) {
        return deliveryService.findByStatusAndDeliveryDateBetween(COMPLETED, dateFrom, dateTo);
    }

    private DeliveryDto getCompletedDelivery(LocalDate date) {
        return deliveryService.findByStatusAndDeliveryDate(COMPLETED, date);
    }

    private long getAverageDeliveryDuration(List<DeliveryDto> deliveries) {
        double averageDeliveryDuration = deliveries.stream()
                .mapToLong(this::getDeliveryDuration)
                .summaryStatistics()
                .getAverage();

        return BigDecimal.valueOf(averageDeliveryDuration)
                .setScale(0, RoundingMode.HALF_UP)
                .longValue();
    }

    private List<RouteDto> getRoutes(List<DeliveryDto> deliveries) {
        return deliveries.stream()
                .map(DeliveryDto::getId)
                .map(routeService::findByDeliveryId)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    private long getDeliveryDuration(DeliveryDto delivery) {
        return routeService.findByDeliveryId(delivery.getId()).stream()
                .mapToLong(RouteDto::getSpentTime)
                .summaryStatistics()
                .getMax();
    }

    CalculationRoutesResult calculateRoutesStatistic(List<RouteDto> routes, CarDto carDto) {
        int travelCost = carDto.getTravelCost();
        int ordersCount = 0;
        double totalWeight = 0.0;
        double fuelConsumption = 0.0;
        long totalTimeInRoutes = 0L;
        int incompleteRoutePointsCount = 0;

        for (RouteDto routeDto : routes) {
            ordersCount += routeDto.getTotalOrders();
            totalWeight += routeDto.getTotalWeight();
            fuelConsumption += travelCost * routeDto.getDistance() / 100;
            totalTimeInRoutes += routeDto.getSpentTime();
            incompleteRoutePointsCount += routeDto.getRoutePoints().stream()
                    .filter(routePoint -> !routePoint.getStatus().equals(RoutePointStatus.DONE))
                    .map(e -> 1).reduce(0, Integer::sum);
        }

        int roundedTravelCost = new BigDecimal(fuelConsumption).setScale(0, RoundingMode.HALF_UP).intValue();

        return new CalculationRoutesResult(ordersCount, totalWeight, roundedTravelCost, totalTimeInRoutes, incompleteRoutePointsCount);
    }

    CalculationRoutesResult calculateRoutesStatistic(List<RouteDto> routes) {
        int ordersCount = 0;
        double totalWeight = 0.0;
        double fuelConsumption = 0.0;

        for (RouteDto routeDto : routes) {
            ordersCount += routeDto.getTotalOrders();
            totalWeight += routeDto.getTotalWeight();
            fuelConsumption += routeDto.getCar().getTravelCost() * routeDto.getDistance() / 100;
        }

        int roundedTravelCost = new BigDecimal(fuelConsumption).setScale(0, RoundingMode.HALF_UP).intValue();

        return new CalculationRoutesResult(ordersCount, totalWeight, roundedTravelCost);
    }

    private Map<LocalDate, List<RouteDto>> collectRoutesByDate(List<DeliveryDto> deliveries) {
        return deliveries.stream()
                .collect(Collectors.toMap(DeliveryDto::getDeliveryDate,
                        delivery -> routeService.findByDeliveryId(delivery.getId())));
    }

    @AllArgsConstructor
    @EqualsAndHashCode
    static class CalculationRoutesResult {
        private int ordersCount;
        private double totalWeight;
        private int fuelConsumption;
        private long totalTimeInRoutes;
        private int incompleteRoutePointsCount;

        public CalculationRoutesResult(int ordersCount, double totalWeight, int fuelConsumption) {
            this.ordersCount = ordersCount;
            this.totalWeight = totalWeight;
            this.fuelConsumption = fuelConsumption;
        }
    }
}