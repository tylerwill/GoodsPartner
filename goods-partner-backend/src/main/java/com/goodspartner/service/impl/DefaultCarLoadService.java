package com.goodspartner.service.impl;

import com.goodspartner.dto.CarLoadDto;
import com.goodspartner.entity.Car;
import com.goodspartner.entity.CarLoad;
import com.goodspartner.entity.Delivery;
import com.goodspartner.entity.OrderExternal;
import com.goodspartner.entity.Route;
import com.goodspartner.entity.RoutePoint;
import com.goodspartner.mapper.CarLoadMapper;
import com.goodspartner.repository.CarLoadRepository;
import com.goodspartner.service.CarLoadService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class DefaultCarLoadService implements CarLoadService {

    private final CarLoadRepository carLoadRepository;
    private final CarLoadMapper carLoadMapper;


    @Override
    public List<CarLoadDto> findCarLoad(Delivery delivery, Car car) {
        return carLoadRepository.findByDeliveryAndCar(delivery, car)
                .stream()
                .map(carLoadMapper::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<CarLoad> buildCarLoad(List<Route> routes, List<OrderExternal> orders){
        return routes.stream().map(route -> routeToCarDetails(route, orders)).toList();
    }

    @Override
    public CarLoad routeToCarDetails(Route route, List<OrderExternal> orders) {
        List<OrderExternal> carLoadOrders = route.getRoutePoints()
                .stream()
                .map(routePoint -> {
                    List<String> routeOrderNumbers = routePoint.getOrders()
                            .stream()
                            .map(OrderExternal::getOrderNumber)
                            .toList();
                    return orders
                            .stream()
                            .filter(order -> routeOrderNumbers.contains(order.getOrderNumber()))
                            .toList();
                }).flatMap(List::stream)
                .collect(Collectors.toList());

        Collections.reverse(carLoadOrders); // TODO does it really make sense now?

        CarLoad carLoad = new CarLoad();
        carLoad.setCar(route.getCar());
        carLoad.setOrders(carLoadOrders);
        return carLoad;
    }
}
