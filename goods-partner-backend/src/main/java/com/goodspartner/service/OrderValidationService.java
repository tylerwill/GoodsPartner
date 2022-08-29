package com.goodspartner.service;

import com.goodspartner.dto.OrderDto;
import com.goodspartner.service.dto.OrderValidationDto;

import java.util.List;

public interface OrderValidationService {
    List<OrderDto> getValidOrders(List<OrderDto> orderDtos);

    List<OrderDto> getInvalidOrders(List<OrderDto> orderDtos);

    OrderValidationDto validateOrders(List<OrderDto> orderDtos);
}