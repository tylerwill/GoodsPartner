package com.goods.partner.dto;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class CarLoadDto {
    private CarDto car;
    private List<RoutePointDto> routePoints;
}
