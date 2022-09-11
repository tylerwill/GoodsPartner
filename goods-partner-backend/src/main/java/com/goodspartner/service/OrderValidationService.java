package com.goodspartner.service;

import com.goodspartner.dto.OrderDto;

import java.util.List;

public interface OrderValidationService {

    void enrichValidAddress(List<OrderDto> orders);

}