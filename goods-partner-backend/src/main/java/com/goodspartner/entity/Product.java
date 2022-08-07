package com.goodspartner.entity;

import com.goodspartner.factory.Store;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "products")
public class Product {

    @Id
    private int id;

    private String name;

    private double kg; // TODO rename on postgres (amount / unit)

}
