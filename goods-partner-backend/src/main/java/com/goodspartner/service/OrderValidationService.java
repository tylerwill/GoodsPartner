package com.goodspartner.service;

import com.goodspartner.dto.OrderDto;
import com.goodspartner.service.dto.OrderValidationDto;

import java.util.List;

public interface OrderValidationService {

    OrderValidationDto validateOrders(List<OrderDto> orderDtos);
}