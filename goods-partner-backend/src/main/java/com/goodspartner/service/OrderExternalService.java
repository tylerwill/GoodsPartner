package com.goodspartner.service;

import com.goodspartner.service.dto.OrderValidationDto;

public interface OrderExternalService {

    void save(OrderValidationDto orderValidationDto);

}