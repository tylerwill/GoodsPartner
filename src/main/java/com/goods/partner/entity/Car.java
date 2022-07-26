package com.goods.partner.entity;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@NoArgsConstructor
@Getter
@Setter
@Entity
@AllArgsConstructor
@Builder
@Table(name = "cars")
public class Car {
    @Id
    private int id;
    private String name;
    private String licence_plate;
    private String driver;
    private int weight_capacity;
    private boolean cooler;
    private String status;
}