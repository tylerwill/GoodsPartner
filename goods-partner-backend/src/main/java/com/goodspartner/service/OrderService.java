package com.goodspartner.service;

import com.goodspartner.dto.OrderDto;

import java.time.LocalDate;
import java.util.List;

;

public interface OrderService {

    List<OrderDto> findAllByShippingDate(LocalDate date);

}