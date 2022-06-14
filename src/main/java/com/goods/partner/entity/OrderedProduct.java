package com.goods.partner.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "orders_products")
public class OrderedProduct {

    @ManyToOne(fetch = FetchType.LAZY)
    private Order order; // TODO check if bi-directional mapping required here

    @ManyToOne
    @JoinColumn(name = "products_id", referencedColumnName = "id")
    private Product product;

    private int count; // rename to amount
}
