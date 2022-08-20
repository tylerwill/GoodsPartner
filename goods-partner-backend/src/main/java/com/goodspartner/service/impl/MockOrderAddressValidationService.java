package com.goodspartner.service.impl;

import com.goodspartner.dto.OrderDto;
import com.goodspartner.service.OrderAddressValidationService;
import com.goodspartner.web.controller.response.OrdersCalculation;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Service
public class MockOrderAddressValidationService implements OrderAddressValidationService {

    private OrdersCalculation ordersCalculation = new OrdersCalculation();

    //TODO: fix for real validation
    @Override
    public List<OrderDto> getValidOrders(List<OrderDto> orderDtos) {
        List<OrderDto> validOrders = orderDtos.subList(0, orderDtos.size() / 2);
        for (OrderDto orderDto : validOrders) {
            orderDto.setAddressValid(true);
        }
        return validOrders;
    }

    //TODO: fix for real validation
    @Override
    public List<OrderDto> getInvalidOrders(List<OrderDto> orderDtos) {
        List<OrderDto> invalidOrders = orderDtos.subList(orderDtos.size() / 2, orderDtos.size());
        for (OrderDto orderDto : invalidOrders) {
            orderDto.setAddressValid(false);
        }
        return invalidOrders;
    }

    @Override
    public Map<Boolean, List<OrderDto>> sortedOrders(List<OrderDto> orderDtos) {
        List<OrderDto> validOrders = getValidOrders(orderDtos);
        List<OrderDto> inValidOrders = getInvalidOrders(orderDtos);

        List<OrderDto> sortedOrders = Stream.concat(validOrders.stream(), inValidOrders.stream()).toList();

        Map<Boolean, List<OrderDto>> sortedOrderDtos = convertListOrderDtosToMap(sortedOrders);

        if (sortedOrderDtos.isEmpty()) {
            ordersCalculation.setValidOrders(new ArrayList<>());
            ordersCalculation.setInvalidOrders(new ArrayList<>());
        } else {
            ordersCalculation.setValidOrders(sortedOrderDtos.get(Boolean.TRUE));
            ordersCalculation.setInvalidOrders(sortedOrderDtos.get(Boolean.FALSE));
        }

        return sortedOrderDtos;
    }

    private Map<Boolean, List<OrderDto>> convertListOrderDtosToMap(List<OrderDto> orderDtos) {
        Map<Boolean, List<OrderDto>> sortedOrders = new HashMap<>(2);
        sortedOrders.put(Boolean.TRUE, getValidOrders(orderDtos));
        sortedOrders.put(Boolean.FALSE, getInvalidOrders(orderDtos));
        return sortedOrders;
    }
}