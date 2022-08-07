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
    private boolean available;
    private boolean cooler;
    private String licencePlate;
    private int weightCapacity;
    private int travelCost;
}