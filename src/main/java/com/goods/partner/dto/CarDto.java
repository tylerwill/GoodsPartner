package com.goods.partner.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CarDto {
    private int id;
    private String name;
    private String licencePlate;
    private String driver;
    private int weightCapacity;
    private boolean cooler;
    private boolean available;
    private double loadSize;
    private int travelCost;
}