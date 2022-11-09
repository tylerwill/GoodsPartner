package com.goodspartner.entity;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Entity
@Table(name = "route_points")
public class RoutePoint {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "route_points_sequence")
    @SequenceGenerator(name = "route_points_sequence", sequenceName = "route_points_sequence")
    private long id;
    private RoutePointStatus status;
    @Column(name = "client_name")
    private String clientName;
    private String address;
    private double addressTotalWeight; //TODO check if necessary here
    @Column(name = "route_point_distant_time")
    private long routePointDistantTime;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;
    @Column(name = "expected_arrival")
    private LocalTime expectedArrival;
    @Column(name = "expected_completion")
    private LocalTime expectedCompletion;
    @Column(name = "delivery_start")
    private LocalTime deliveryStart;
    @Column(name = "delivery_end")
    private LocalTime deliveryEnd;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_id", referencedColumnName = "id")
    private Route route;

    @OneToMany(mappedBy = "routePoint", cascade = CascadeType.ALL)
    private List<OrderExternal> orders = new ArrayList<>();

    public void setOrders(List<OrderExternal> orders) {
        List<OrderExternal> requiredOrders = Optional.ofNullable(orders)
                .orElse(Collections.emptyList());
        this.orders.clear();
        this.orders.addAll(requiredOrders);
        this.orders.forEach(order -> order.setRoutePoint(this));
    }
}
