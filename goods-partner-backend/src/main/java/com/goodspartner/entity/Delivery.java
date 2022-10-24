package com.goodspartner.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "deliveries")
@SQLDelete(sql = "UPDATE deliveries SET deleted = true WHERE id=?")
@Where(clause = "deleted=false")
public class Delivery {

    @Id
    @Column(name = "id", updatable = false)
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    @Column(name = "delivery_date")
    private LocalDate deliveryDate;

    @OneToMany(mappedBy = "delivery", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Route> routes = new ArrayList<>();

    @OneToMany(mappedBy = "delivery", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderExternal> orders = new ArrayList<>();

    @OneToMany(mappedBy = "delivery", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CarLoad> carLoads = new ArrayList<>();

    @OneToMany(mappedBy = "delivery", cascade = CascadeType.ALL)
    private List<DeliveryHistory> deliveryHistories = new ArrayList<>();

    @Enumerated(value = EnumType.STRING)
    @Column(length = 9)
    private DeliveryStatus status;

    @Enumerated(value = EnumType.STRING)
    @Column(length = 17)
    private DeliveryFormationStatus formationStatus;

    @Column(name = "deleted")
    private boolean deleted = false;

    public void setRoutes(List<Route> routes) {
        List<Route> requiredRoutes = Optional.ofNullable(routes)
                .orElse(Collections.emptyList());
        this.routes.clear();
        this.routes.addAll(requiredRoutes);
        this.routes.forEach(route -> route.setDelivery(this));
    }

    public void setOrders(List<OrderExternal> ordersExternal) {
        List<OrderExternal> requiredOrderExternals = Optional.ofNullable(ordersExternal)
                .orElse(Collections.emptyList());
        this.orders.clear();
        this.orders.addAll(requiredOrderExternals);
        this.orders.forEach(order -> order.setDelivery(this));
    }

    public void setCarLoads(List<CarLoad> carLoads) {
        List<CarLoad> requiredCarLoads = Optional.ofNullable(carLoads)
                .orElse(Collections.emptyList());
        this.carLoads.clear();
        this.carLoads.addAll(requiredCarLoads);
        this.carLoads.forEach(carLoad -> carLoad.setDelivery(this));
    }
}
