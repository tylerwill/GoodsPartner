package com.goodspartner.service.impl;

import com.goodspartner.dto.OrderDto;
import com.goodspartner.dto.RoutePointDto;
import com.goodspartner.service.CarLoadService;
import com.goodspartner.web.controller.response.RoutesCalculation;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DefaultCarLoadService implements CarLoadService {

    @Override
    public List<RoutesCalculation.CarLoadDto> map(List<RoutesCalculation.RouteDto> routes, List<OrderDto> orders) {
        return routes.stream().map(route -> routeToCarDetails(route, orders)).toList();
    }

    @Override
    public RoutesCalculation.CarLoadDto routeToCarDetails(RoutesCalculation.RouteDto route, List<OrderDto> orders) {
        List<OrderDto> ordersInfo = route.getRoutePoints()
                .stream()
                .map(routePoint -> {
                    List<String> routeOrderNumbers = routePoint.getOrders()
                            .stream()
                            .map(RoutePointDto.AddressOrderDto::getOrderNumber)
                            .toList();
                    return orders
                            .stream()
                            .filter(order -> routeOrderNumbers.contains(order.getOrderNumber()))
                            .toList();
                }).flatMap(List::stream)
                .collect(Collectors.toList());

        Collections.reverse(ordersInfo);
        return new RoutesCalculation.CarLoadDto(route.getCar(), ordersInfo);
    }
}
