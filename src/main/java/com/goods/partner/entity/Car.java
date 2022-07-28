package com.goods.partner.entity;

import lombok.*;

import javax.persistence.*;

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

    @Column(name = "car_status")
    @Enumerated(EnumType.STRING)
    private CarStatus status;
}