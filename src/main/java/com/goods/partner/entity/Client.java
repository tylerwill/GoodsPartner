package com.goods.partner.entity;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "clients")
public class Client {

    @Id
    private int id;

    private String name;

    @OneToMany(
            mappedBy = "client"
    )
    private List<Address> address;
}
