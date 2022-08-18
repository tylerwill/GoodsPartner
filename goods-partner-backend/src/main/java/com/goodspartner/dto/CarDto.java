package com.goodspartner.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class CarDto {
    private int id;
    private String name;
    private String licencePlate;
    private String driver;
    private int weightCapacity;
    private Boolean cooler;
    private Boolean available;
    private double loadSize;
    private int travelCost;
}