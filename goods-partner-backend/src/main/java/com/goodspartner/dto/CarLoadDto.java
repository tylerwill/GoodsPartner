package com.goodspartner.dto;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class CarLoadDto {
    private CarDto car;
    private List<OrderInfoDto> orders;
}
