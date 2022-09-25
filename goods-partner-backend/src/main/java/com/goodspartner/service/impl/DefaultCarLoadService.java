package com.goodspartner.service.impl;

import com.goodspartner.entity.CarLoad;
import com.goodspartner.entity.OrderExternal;
import com.goodspartner.entity.Route;
import com.goodspartner.entity.RoutePoint;
import com.goodspartner.service.CarLoadService;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DefaultCarLoadService implements CarLoadService {

    @Override
    public List<CarLoad> map(List<Route> routes, List<OrderExternal> orders){
        return routes.stream().map(route -> routeToCarDetails(route, orders)).toList();
    }

    @Override
    public CarLoad routeToCarDetails(Route route, List<OrderExternal> orders) {
        List<OrderExternal> carLoadOrders = route.getRoutePoints()
                .stream()
                .map(routePoint -> {
                    List<String> routeOrderNumbers = routePoint.getOrders()
                            .stream()
                            .map(RoutePoint.AddressOrder::getOrderNumber)
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
