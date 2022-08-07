package com.goodspartner.entity;

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
@Table(name = "stores")
public class Store {

    @Id
    private int id;

    private String name;
    private String address;

}
