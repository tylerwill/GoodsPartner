package com.goodspartner.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class CalculationOrdersDto {
    private LocalDate date;
    private List<OrderDto> orders;
}