package com.goodspartner.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Getter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.Entity;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.SequenceGenerator;
import javax.persistence.GeneratedValue;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(name = "cars")
@SQLDelete(sql = "UPDATE cars SET deleted = true WHERE id=?")
@Where(clause = "deleted=false")
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

    private boolean deleted = Boolean.FALSE;
}