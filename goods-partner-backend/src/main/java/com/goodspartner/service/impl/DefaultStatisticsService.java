package com.goodspartner.service.impl;

import com.goodspartner.dto.CarDto;
import com.goodspartner.dto.DeliveryDto;
import com.goodspartner.dto.RouteDto;
import com.goodspartner.entity.Car;
import com.goodspartner.entity.Delivery;
import com.goodspartner.entity.RoutePointStatus;
import com.goodspartner.exceptions.CarNotFoundException;
import com.goodspartner.exceptions.DeliveryNotFoundException;
import com.goodspartner.mapper.CarMapper;
import com.goodspartner.mapper.DeliveryRouteMapper;
import com.goodspartner.repository.CarRepository;
import com.goodspartner.repository.DeliveryRepository;
import com.goodspartner.service.StatisticsService;
import com.goodspartner.web.controller.response.statistics.CarStatisticsCalculation;
import com.goodspartner.web.controller.response.statistics.DailyCarStatisticsCalculation;
import com.goodspartner.web.controller.response.statistics.StatisticsCalculation;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static com.goodspartner.entity.DeliveryStatus.COMPLETED;

@Service
@RequiredArgsConstructor
public class DefaultStatisticsService implements StatisticsService {

    private final DeliveryRepository deliveryRepository;
    private final CarRepository carRepository;
    private final DeliveryRouteMapper deliveryRouteMapper;
    private final CarMapper carMapper;

    @Override
    public StatisticsCalculation getStatistics(LocalDate dateFrom, LocalDate dateTo) {

        List<DeliveryDto> deliveries = getCompletedDeliveriesInRange(dateFrom, dateTo);
        long averageDeliveryDuration = getAverageDeliveryDuration(deliveries);

        List<RouteDto> routes = getRoutes(deliveries);

        CalculationRoutesResult calculationRoutesResult = calculateRoutesStatistic(routes);

//        For future
//        Map<LocalDate, Integer> routesPerDayMap = deliveries.stream()
//                .collect(Collectors.toMap(DeliveryDto::getDeliveryDate,
//                        delivery -> delivery.getRoutes().size()));

        return StatisticsCalculation.builder()
                .routeCount(routes.size())
                .orderCount(calculationRoutesResult.ordersCount)
                .weight(calculationRoutesResult.totalWeight)
                .fuelConsumption(calculationRoutesResult.fuelConsumption)
                .averageDeliveryDuration(averageDeliveryDuration)
                .build();
    }

    @Override
    public CarStatisticsCalculation getCarStatistics(LocalDate dateFrom, LocalDate dateTo, int carId) {

        CarDto car = getCar(carId);
        List<DeliveryDto> deliveries = getCompletedDeliveriesInRange(dateFrom, dateTo);

        List<RouteDto> routes = getRoutes(deliveries)
                .stream()
                .filter(routeDto -> routeDto.getCar().getId() == carId)
                .toList();

        CalculationRoutesResult calculationRoutesResult = calculateRoutesStatistic(routes, car);

        return CarStatisticsCalculation.builder()
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
    public DailyCarStatisticsCalculation getDailyCarStatistics(LocalDate date, int carId) {

        CarDto car = getCar(carId);
        DeliveryDto delivery = getCompletedDelivery(date);

        RouteDto routeDto = delivery.getRoutes()
                .stream()
                .filter(route -> route.getCar().getId() == carId)
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);// This car was not used at this date

        return DailyCarStatisticsCalculation.builder()
                .car(car)
                .orderCount(routeDto.getTotalOrders())
                .build();
    }

    private CarDto getCar(int carId) {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new CarNotFoundException(carId));
        return carMapper.carToCarDto(car);
    }

    private List<DeliveryDto> getCompletedDeliveriesInRange(LocalDate dateFrom, LocalDate dateTo) {

        List<Delivery> deliveries = deliveryRepository.findByStatusAndDeliveryDateBetween(COMPLETED, dateFrom, dateTo);
        if (deliveries.isEmpty()) {
            throw new DeliveryNotFoundException(COMPLETED, dateFrom, dateTo);
        }

        return deliveryRouteMapper.mapDeliveriesWithRoutes(deliveries);
    }

    private DeliveryDto getCompletedDelivery(LocalDate date) {
        Optional<Delivery> optionalDelivery = deliveryRepository.findByStatusAndDeliveryDate(COMPLETED, date);

        if (optionalDelivery.isEmpty()) {
            throw new DeliveryNotFoundException(COMPLETED, date);
        }

        return deliveryRouteMapper.mapDeliveryWithRoute(optionalDelivery.get());
    }

    private long getAverageDeliveryDuration(List<DeliveryDto> deliveries) {
        double averageDeliveryDuration = deliveries
                .stream()
                .mapToLong(this::getDeliveryDuration)
                .summaryStatistics()
                .getAverage();

        return BigDecimal.valueOf(averageDeliveryDuration)
                .setScale(0, RoundingMode.HALF_UP)
                .longValue();
    }

    private List<RouteDto> getRoutes(List<DeliveryDto> deliveries) {
        return deliveries.stream().map(DeliveryDto::getRoutes).flatMap(List::stream).toList();
    }

    private long getDeliveryDuration(DeliveryDto delivery) {
        return delivery.getRoutes()
                .stream()
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