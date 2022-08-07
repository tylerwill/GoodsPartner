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
@Table(name = "managers")
public class Manager {

    @Id
    private int id;

    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String email;
}
