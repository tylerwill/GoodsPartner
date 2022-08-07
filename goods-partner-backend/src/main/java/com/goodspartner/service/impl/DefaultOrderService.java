package com.goodspartner.service.impl;

import com.goodspartner.dto.*;
import com.goodspartner.entity.Order;
import com.goodspartner.mapper.CarDetailsMapper;
import com.goodspartner.mapper.OrderMapper;
import com.goodspartner.repository.OrderRepository;
import com.goodspartner.web.controller.response.OrdersCalculation;
import com.goodspartner.web.controller.response.RoutesCalculation;
import com.goodspartner.service.OrderService;
import com.goodspartner.service.RouteService;
import com.goodspartner.service.StoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DefaultOrderService implements OrderService {

    private final OrderRepository orderRepository;
    private final RouteService routeService;
    private final OrderMapper orderMapper;
    private final StoreService storeFactory;
    private final CarDetailsMapper carDetailsMapper;

    @Override
    @Transactional
    public OrdersCalculation calculateOrders(LocalDate date) {

        List<Order> ordersByDate = orderRepository.findAllByShippingDateEquals(date);
        List<OrderDto> orderDtos = orderMapper.mapOrders(ordersByDate);

        OrdersCalculation ordersCalculation = new OrdersCalculation();
        ordersCalculation.setDate(date);
        ordersCalculation.setOrders(orderDtos);
        return ordersCalculation;
    }

    @Override
    @Transactional
    public RoutesCalculation calculateRoutes(LocalDate date) {

        List<Order> orders = orderRepository.findAllByShippingDateEquals(date);

        StoreDto storeDto = storeFactory.getMainStore();
        List<RoutesCalculation.RouteDto> routes = routeService.calculateRoutes(orders, storeDto);

        List<RoutesCalculation.CarLoadDto> carsDetailsList = carDetailsMapper.map(routes, orders);

        RoutesCalculation routesCalculation = new RoutesCalculation();
        routesCalculation.setDate(date);
        routesCalculation.setRoutes(routes);
        routesCalculation.setCarLoadDetails(carsDetailsList);

        return routesCalculation;
    }
}