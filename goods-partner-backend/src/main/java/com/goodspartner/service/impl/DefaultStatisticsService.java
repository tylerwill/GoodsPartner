package com.goodspartner.service.impl;

import com.goodspartner.dto.*;
import com.goodspartner.entity.Car;
import com.goodspartner.entity.Delivery;
import com.goodspartner.exceptions.CarNotFoundException;
import com.goodspartner.exceptions.DeliveryNotFoundException;
import com.goodspartner.mapper.CarMapper;
import com.goodspartner.mapper.DeliveryMapper;
import com.goodspartner.repository.CarRepository;
import com.goodspartner.repository.DeliveryRepository;
import com.goodspartner.service.StatisticsService;
import com.goodspartner.web.controller.response.statistics.CarStatisticsCalculation;
import com.goodspartner.web.controller.response.statistics.DailyCarStatisticsCalculation;
import com.goodspartner.web.controller.response.statistics.StatisticsCalculation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.goodspartner.entity.DeliveryStatus.COMPLETED;

@Service
@RequiredArgsConstructor
public class DefaultStatisticsService implements StatisticsService {

    private final DeliveryRepository deliveryRepository;
    private final CarRepository carRepository;
    private final DeliveryMapper deliveryMapper;
    private final CarMapper carMapper;

    @Override
    public StatisticsCalculation getStatistics(LocalDate startDate, LocalDate finishDate) {

        List<DeliveryDto> deliveries = getCompletedDeliveriesInRange(startDate, finishDate);
        long averageDeliveryDuration = getAverageDeliveryDuration(deliveries);

        List<RouteDto> routes = getRoutes(deliveries);
        int ordersCount = getOrdersCount(routes);
        double totalWeight = getWeight(routes);
        int fuelConsumption = getFuelConsumption(routes);

        return StatisticsCalculation.builder()
                .routeCount(routes.size())
                .orderCount(ordersCount)
                .weight(totalWeight)
                .fuelConsumption(fuelConsumption)
                .averageDeliveryDuration(averageDeliveryDuration)
                .build();
    }

    @Override
    public CarStatisticsCalculation getCarStatistics(LocalDate startDate, LocalDate finishDate, int carId) {

        CarDto car = getCar(carId);
        List<DeliveryDto> deliveries = getCompletedDeliveriesInRange(startDate, finishDate);

        List<RouteDto> routes = getRoutes(deliveries)
                .stream()
                .filter(routeDto -> routeDto.getCar().equals(car))
                .toList();

        int ordersCount = getOrdersCount(routes);
        double totalWeight = getWeight(routes);
        int fuelConsumption = getFuelConsumption(routes, car);

        return CarStatisticsCalculation.builder()
                .routeCount(routes.size())
                .orderCount(ordersCount)
                .weight(totalWeight)
                .fuelConsumption(fuelConsumption)
                .car(car)
                .build();
    }

    @Override
    public DailyCarStatisticsCalculation getDailyCarStatistics(LocalDate date, int carId) {

        CarDto car = getCar(carId);
        DeliveryDto delivery = getCompletedDelivery(date);
        List<OrderDto> orderDtos = delivery.getOrders();

        return DailyCarStatisticsCalculation.builder()
                .car(car)
                .orderCount(orderDtos.size())
                .build();
    }

    private CarDto getCar(int carId) {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new CarNotFoundException(carId));
        return carMapper.carToCarDto(car);
    }

    private List<DeliveryDto> getCompletedDeliveriesInRange(LocalDate rangeStartDate, LocalDate rangeFinishDate) {
        List<Delivery> deliveries = deliveryRepository.findByDeliveryDateBetween(rangeStartDate, rangeFinishDate);

        deliveries.stream()
                .filter(delivery -> delivery.getStatus() == COMPLETED)
                .findFirst()
                .orElseThrow(() -> new DeliveryNotFoundException(rangeStartDate, rangeFinishDate));

        return deliveryMapper.deliveriesToDeliveryDtos(deliveries);
    }

    private DeliveryDto getCompletedDelivery(LocalDate date) {
        Optional<Delivery> optionalDelivery = deliveryRepository.findByDeliveryDate(date);

        Delivery delivery = optionalDelivery.stream()
                .filter(d -> d.getStatus() == COMPLETED)
                .findFirst()
                .orElseThrow(() -> new DeliveryNotFoundException(date));

        return deliveryMapper.deliveryToDeliveryDto(delivery);
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

    private int getOrdersCount(List<RouteDto> routes) {
        return (int) routes.stream()
                .mapToInt(RouteDto::getTotalOrders)
                .summaryStatistics().getSum();

    }

    private double getWeight(List<RouteDto> routes) {
        return BigDecimal.valueOf(routes.stream()
                        .map(RouteDto::getTotalWeight)
                        .collect(Collectors.summarizingDouble(weight -> weight)).getSum())
                .setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    private int getFuelConsumption(List<RouteDto> routes) {
        return BigDecimal.valueOf(routes.stream()
                        .map(route -> route.getCar().getTravelCost() * route.getDistance() / 100)
                        .collect(Collectors.summarizingDouble(fuelCount -> fuelCount)).getSum())
                .setScale(0, RoundingMode.HALF_UP).intValue();
    }

    private int getFuelConsumption(List<RouteDto> routes, CarDto car) {
        int travelCost = car.getTravelCost();
        return BigDecimal.valueOf(routes.stream()
                        .map(routeDto -> travelCost * routeDto.getDistance() / 100)
                        .collect(Collectors.summarizingDouble(fuelCount -> fuelCount)).getSum())
                .setScale(0, RoundingMode.HALF_UP).intValue();
    }

    private long getDeliveryDuration(DeliveryDto delivery) {
        return delivery.getRoutes()
                .stream()
                .mapToLong(RouteDto::getSpentTime)
                .summaryStatistics()
                .getMax();
    }
}