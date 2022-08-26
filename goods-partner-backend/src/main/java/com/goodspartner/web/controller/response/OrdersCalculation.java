package com.goodspartner.web.controller.response;

import com.goodspartner.dto.OrderDto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Builder
@Getter
@Setter
public class OrdersCalculation {
    private LocalDate date;
    private List<OrderDto> validOrders;
    private List<OrderDto> invalidOrders;
    private double totalOrdersWeight;
}