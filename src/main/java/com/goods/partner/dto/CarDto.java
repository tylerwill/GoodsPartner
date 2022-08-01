package com.goods.partner.dto;

import lombok.Data;

@Data
public class CarDto {
    private int id;
    private String name;
    private String licencePlate;
    private String driver;
    private int weightCapacity;
    private boolean cooler;
    private boolean available;
}