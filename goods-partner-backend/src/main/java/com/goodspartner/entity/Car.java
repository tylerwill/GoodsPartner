package com.goodspartner.entity;

import lombok.*;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(name = "cars")
public class Car {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cars_id_sequence")
    @SequenceGenerator(name = "cars_id_sequence", sequenceName = "cars_id_sequence")
    private int id;
    private String name;
    private String driver;
    private Boolean available;
    private Boolean cooler;

    @Column(name = "licence_plate")
    private String licencePlate;

    @Column(name = "weight_capacity")
    private int weightCapacity;
    private int travelCost;
}