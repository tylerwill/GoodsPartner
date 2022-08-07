package com.goodspartner.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CalculationRoutesDto {
    private LocalDate date;
    private List<RouteDto> routes;
    private List<CarLoadDto> carLoadDetails;
}