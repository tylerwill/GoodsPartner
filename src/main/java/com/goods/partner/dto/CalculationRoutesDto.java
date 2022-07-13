package com.goods.partner.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class CalculationRoutesDto {
    private LocalDate date;
    private List<RouteDto> routes;
}
