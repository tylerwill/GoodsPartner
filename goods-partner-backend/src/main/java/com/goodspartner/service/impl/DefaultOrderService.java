package com.goodspartner.service.impl;

import com.goodspartner.dto.OrderDto;
import com.goodspartner.entity.Order;
import com.goodspartner.mapper.OrderMapper;
import com.goodspartner.repository.OrderRepository;
import com.goodspartner.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class DefaultOrderService implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    @Transactional
    @Override
    public List<OrderDto> findAllByShippingDate(LocalDate date) {

        List<Order> ordersByDate = orderRepository.findAllByShippingDateEquals(date);
        return orderMapper.mapOrders(ordersByDate);
    }

    @Override
    public double calculateTotalOrdersWeight(List<OrderDto> ordersByDate) {
        double totalOrdersWeight = ordersByDate
                .stream()
                .mapToDouble(OrderDto::getOrderWeight)
                .sum();
        return totalOrdersWeight;
    }

}