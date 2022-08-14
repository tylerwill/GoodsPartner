package com.goodspartner.entity;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "orders")
public class Order {

    @Id
    private int id;

    private int number;

    //private LocalDate date;
    @Column(name = "created_date")
    private LocalDate createdDate;

    @Column(name = "shipping_date")
    private LocalDate shippingDate;

    @ManyToOne(fetch = FetchType.LAZY)
    private Address address;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id", referencedColumnName = "id")
    private Manager manager;

    @OneToMany(
            mappedBy = "order"
    )
    private List<OrderedProduct> orderedProducts;

}
