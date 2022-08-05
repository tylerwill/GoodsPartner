package com.goods.partner.dto;

import lombok.*;

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
    private List<CarLoadDetailsDto> carLoadDetails;
}