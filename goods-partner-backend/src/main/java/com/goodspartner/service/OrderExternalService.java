package com.goodspartner.service;

import com.goodspartner.dto.OrderDto;

import java.util.List;
import java.util.Map;

public interface OrderExternalService {
    void save(Map<Boolean, List<OrderDto>> sortedOrderDtos);
}