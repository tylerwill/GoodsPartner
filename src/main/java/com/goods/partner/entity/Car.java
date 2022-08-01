package com.goods.partner.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "cars")
public class Car {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cars_id_sequence")
    @SequenceGenerator(name = "cars_id_sequence", sequenceName = "cars_id_sequence")
    private int id;
    private String name;
    private String driver;
    private boolean available;
    private boolean cooler;

    @Column(name = "licence_plate")
    private String licencePlate;

    @Column(name = "weight_capacity")
    private int weightCapacity;
}