package com.goods.partner.service.impl;

import com.goods.partner.dto.*;
import com.goods.partner.entity.Order;
import com.goods.partner.entity.projection.StoreProjection;
import com.goods.partner.mapper.CarDetailsMapper;
import com.goods.partner.mapper.OrderMapper;
import com.goods.partner.mapper.StoreMapper;
import com.goods.partner.repository.OrderRepository;
import com.goods.partner.repository.StoreRepository;
import com.goods.partner.service.OrderService;
import com.goods.partner.service.RouteService;
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
    private final StoreRepository storeRepository;
    private final RouteService routeService;
    private final OrderMapper orderMapper;
    private final StoreMapper storeMapper;
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
        List<StoreProjection> storeProjections = storeRepository.groupStoresByOrders(date);
        List<StoreDto> stores = storeMapper.mapStoreGroup(storeProjections);

        List<RouteDto> routes = routeService.calculateRoutes(orders, stores);

        List<CarLoadDetailsDto> carsDetailsList = carDetailsMapper.map(routes, orders);

        CalculationRoutesDto calculationRoutesDto = new CalculationRoutesDto();
        calculationRoutesDto.setDate(date);
        calculationRoutesDto.setRoutes(routes);
        calculationRoutesDto.setCarLoadDetails(carsDetailsList);

        return calculationRoutesDto;
    }

    @Override
    @Transactional
    public CalculationStoresDto calculateStores(LocalDate date) {

        List<StoreProjection> storeProjections = storeRepository.groupStoresByOrders(date);
        List<StoreDto> storeDtos = storeMapper.mapStoreGroup(storeProjections);

        CalculationStoresDto calculationStoresDto = new CalculationStoresDto();
        calculationStoresDto.setDate(date);
        calculationStoresDto.setStores(storeDtos);

        return calculationStoresDto;
    }
}