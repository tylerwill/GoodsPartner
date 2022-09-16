package com.goodspartner.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "car_loads")
public class CarLoad {

    @Id
    @Column(name = "id", updatable = false)
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    @OneToOne
    @JoinColumn(name = "car_id")
    private Car car;

    @OneToMany(mappedBy = "carLoad", cascade = CascadeType.ALL)
    private List<OrderExternal> orders = new ArrayList<>(1);

    @ManyToOne
    @JoinColumn(name = "delivery_id", referencedColumnName = "id")
    private Delivery delivery;

    public void setOrders(List<OrderExternal> orders) {
        this.orders =Optional.ofNullable(orders)
                .orElseGet(Collections::emptyList);
        this.orders.forEach(order -> order.setCarLoad(this));

    }
}
