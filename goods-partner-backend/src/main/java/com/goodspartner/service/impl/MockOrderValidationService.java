package com.goodspartner.service.impl;

import com.goodspartner.dto.OrderDto;
import com.goodspartner.service.OrderValidationService;
import com.goodspartner.service.dto.OrderValidationDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MockOrderValidationService implements OrderValidationService {

    //TODO: fix for real validation
    @Override
    public List<OrderDto> getValidOrders(List<OrderDto> orderDtos) {
        List<OrderDto> validOrders = orderDtos.subList(0, orderDtos.size() / 2);
        for (OrderDto orderDto : validOrders) {
            orderDto.setValidAddress(true);
        }
        return validOrders;
    }

    //TODO: fix for real validation
    @Override
    public List<OrderDto> getInvalidOrders(List<OrderDto> orderDtos) {
        List<OrderDto> invalidOrders = orderDtos.subList(orderDtos.size() / 2, orderDtos.size());
        for (OrderDto orderDto : invalidOrders) {
            orderDto.setValidAddress(false);
        }
        return invalidOrders;
    }

    @Override
    public OrderValidationDto validateOrders(List<OrderDto> orderDtos) {

        // Replce belopw with functionality to get real validation
        List<OrderDto> validOrders = getValidOrders(orderDtos);
        List<OrderDto> inValidOrders = getInvalidOrders(orderDtos);

        return OrderValidationDto.builder()
                .validOrders(validOrders)
                .invalidOrders(inValidOrders)
                .build();
    }

}