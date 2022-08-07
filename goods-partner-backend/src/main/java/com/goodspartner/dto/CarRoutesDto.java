package com.goodspartner.dto;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class CarRoutesDto {
    private CarDto car;
    private List<RoutePointDto> routePoints;
}
