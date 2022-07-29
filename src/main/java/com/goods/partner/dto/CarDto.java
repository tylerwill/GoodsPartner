package com.goods.partner.dto;

import com.goods.partner.entity.CarStatus;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CarDto {
    private int id;
    private String name;
    private String licence_plate;
    private String driver;
    private int weight_capacity;
    private boolean cooler;
    private CarStatus status;
}