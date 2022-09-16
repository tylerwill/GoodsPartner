package com.goodspartner.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "deliveries")
public class Delivery {

    @Id
    @Column(name = "id", updatable = false)
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    @Column(name = "delivery_date")
    private LocalDate deliveryDate;

    @OneToMany(mappedBy = "delivery", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Route> routes = new ArrayList<>(1);

    @OneToMany(mappedBy = "delivery", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderExternal> orders = new ArrayList<>(1);

    @OneToMany(mappedBy = "delivery", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CarLoad> carLoads = new ArrayList<>(1);

    @Enumerated(value = EnumType.STRING)
    @Column(length = 9)
    private DeliveryStatus status;

    public void setRoutes(List<Route> routes) {
        this.routes = Optional.ofNullable(routes)
                .orElseGet(Collections::emptyList);
        this.routes.forEach(route -> route.setDelivery(this));
    }

    public void setOrders(List<OrderExternal> ordersExternal) {
        this.orders = Optional.ofNullable(ordersExternal)
                .orElseGet(Collections::emptyList);
        this.orders.forEach(order -> order.setDelivery(this));
    }

    public void setCarLoads(List<CarLoad> carLoads) {
        this.carLoads = Optional.ofNullable(carLoads)
                .orElseGet(Collections::emptyList);
        this.carLoads.forEach(carLoad -> carLoad.setDelivery(this));
    }

    public void removeRoutes() {
        routes.forEach(route -> route.setDelivery(null));
        this.routes.clear();
    }

    public void removeOrders() {
        this.orders.forEach(order -> order.setDelivery(null));
        this.orders.clear();
    }

    public void removeCarLoads() {
        this.carLoads.forEach(carLoad -> carLoad.setDelivery(null));
        this.carLoads.clear();
        this.orders.forEach(orderExternal -> orderExternal.setCarLoad(null));
    }
}
