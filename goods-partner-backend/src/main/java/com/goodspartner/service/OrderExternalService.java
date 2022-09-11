package com.goodspartner.service;

import com.goodspartner.dto.OrderDto;

import java.util.List;

public interface OrderExternalService {

    void save(List<OrderDto> orderValidationDto);

}