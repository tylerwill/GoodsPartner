package com.goodspartner.response;

import com.goodspartner.dto.OrderDto;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class OrdersCalculation {
    private LocalDate date;
    private List<OrderDto> orders;
}