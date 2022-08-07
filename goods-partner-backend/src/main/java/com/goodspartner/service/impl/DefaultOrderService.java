package com.goodspartner.service.impl;

import com.goodspartner.dto.*;
import com.goodspartner.entity.Order;
import com.goodspartner.mapper.CarDetailsMapper;
import com.goodspartner.mapper.OrderMapper;
import com.goodspartner.repository.OrderRepository;
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
    public CalculationOrdersDto calculateOrders(LocalDate date) {

        List<Order> ordersByDate = orderRepository.findAllByShippingDateEquals(date);
        List<OrderDto> orderDtos = orderMapper.mapOrders(ordersByDate);

        CalculationOrdersDto calculationOrdersDto = new CalculationOrdersDto();
        calculationOrdersDto.setDate(date);
        calculationOrdersDto.setOrders(orderDtos);
        return calculationOrdersDto;
    }

    @Override
    @Transactional
    public CalculationRoutesDto calculateRoutes(LocalDate date) {

        List<Order> orders = orderRepository.findAllByShippingDateEquals(date);

        StoreDto storeDto = storeFactory.getStore();
        List<RouteDto> routes = routeService.calculateRoutes(orders, storeDto);

        List<CarLoadDto> carsDetailsList = carDetailsMapper.map(routes, orders);

        CalculationRoutesDto calculationRoutesDto = new CalculationRoutesDto();
        calculationRoutesDto.setDate(date);
        calculationRoutesDto.setRoutes(routes);
        calculationRoutesDto.setCarLoadDetails(carsDetailsList);

        return calculationRoutesDto;
    }
}