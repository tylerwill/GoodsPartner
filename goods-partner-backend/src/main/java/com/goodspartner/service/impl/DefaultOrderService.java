package com.goodspartner.service.impl;

import com.goodspartner.dto.OrderDto;
import com.goodspartner.dto.ProductDto;
import com.goodspartner.entity.Order;
import com.goodspartner.mapper.OrderMapper;
import com.goodspartner.repository.OrderRepository;
import com.goodspartner.service.OrderService;
import com.goodspartner.util.DtoCalculationHelper;
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
    private final DtoCalculationHelper dtoHelper;

    @Transactional
    @Override
    public List<OrderDto> findAllByShippingDate(LocalDate date) {

        List<Order> ordersByDate = orderRepository.findAllByShippingDateEquals(date);
        List<OrderDto> orderDtos = orderMapper.mapOrders(ordersByDate);
        for (OrderDto order : orderDtos) {
            List<ProductDto> products = order.getProducts();
            products.forEach(dtoHelper::enrichProduct);
            order.setOrderWeight(dtoHelper.calculateTotalOrderWeight(order));
        }

        return orderDtos;
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