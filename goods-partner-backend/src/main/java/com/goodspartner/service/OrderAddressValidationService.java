package com.goodspartner.service;

import com.goodspartner.dto.OrderDto;

import java.util.List;
import java.util.Map;

public interface OrderAddressValidationService {
    List<OrderDto> getValidOrders(List<OrderDto> orderDtos);

    List<OrderDto> getInvalidOrders(List<OrderDto> orderDtos);

    Map<Boolean, List<OrderDto>> sortedOrders(List<OrderDto> orderDtos);
}