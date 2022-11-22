package com.goodspartner.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder
public class CarDto {
    private int id;
    private String name;
    private String licencePlate;
    private UserDto driver;
    private int weightCapacity;
    private Boolean cooler;
    private Boolean available;
    private int travelCost;
}