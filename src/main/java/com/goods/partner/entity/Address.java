package com.goods.partner.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "addresses")
public class Address {

    @Id
    private int id;

    private String address;

    @ManyToOne(fetch = FetchType.LAZY)
    private Client client;

    @OneToMany(
            mappedBy = "address"
    )
    private List<Order> orders;

}
