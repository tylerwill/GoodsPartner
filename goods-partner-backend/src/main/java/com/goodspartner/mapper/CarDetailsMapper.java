package com.goodspartner.mapper;

import com.goodspartner.dto.OrderDto;
import com.goodspartner.dto.RoutePointDto;
import com.goodspartner.web.controller.response.RoutesCalculation;
import com.google.common.annotations.VisibleForTesting;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CarDetailsMapper {

    public List<RoutesCalculation.CarLoadDto> map(List<RoutesCalculation.RouteDto> routes, List<OrderDto> orders) {
        return routes.stream().map(route -> routeToCarDetails(route, orders)).toList();
    }

    // TODO refactor stream mapping
    @VisibleForTesting
    RoutesCalculation.CarLoadDto routeToCarDetails(RoutesCalculation.RouteDto route, List<OrderDto> orders) {
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
        return RoutesCalculation.CarLoadDto.builder()
                .car(route.getCar())
                .orders(ordersInfo)
                .build();
    }
}
