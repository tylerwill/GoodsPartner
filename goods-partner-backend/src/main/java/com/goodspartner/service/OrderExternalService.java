package com.goodspartner.service;

import com.goodspartner.dto.OrderDto;

import java.util.List;
import java.util.UUID;

public interface OrderExternalService {

    void save(List<OrderDto> orderValidationDto);

    List<OrderDto> findAllByDeliveryId(UUID id);
}