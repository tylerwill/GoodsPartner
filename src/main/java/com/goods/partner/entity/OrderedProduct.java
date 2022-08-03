package com.goods.partner.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "orders_products")
public class OrderedProduct {

    @Id
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Order order; // TODO check if bi-directional mapping required here

    @ManyToOne
    @JoinColumn(name = "product_id", referencedColumnName = "id")
    private Product product;

    private int count; // rename to amount


}
