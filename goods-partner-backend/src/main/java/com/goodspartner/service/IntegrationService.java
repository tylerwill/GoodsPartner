package com.goodspartner.service;

import com.goodspartner.dto.OrderDto;

import java.time.LocalDate;
import java.util.List;

public interface IntegrationService {

    List<OrderDto> findAllByShippingDate(LocalDate date);

    double calculateTotalOrdersWeight(List<OrderDto> ordersByDate);
}