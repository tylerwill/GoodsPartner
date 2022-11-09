package com.goodspartner.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "routes")
public class Route {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "routes_sequence")
    @SequenceGenerator(name = "routes_sequence", sequenceName = "routes_sequence")
    private int id;

    @Enumerated(value = EnumType.STRING)
    private RouteStatus status;
    private double totalWeight;
    private int totalPoints;
    private int totalOrders;
    private double distance;
    private long estimatedTime;
    private LocalDateTime startTime;
    private LocalDateTime finishTime;
    private long spentTime; // TODO Minutes? Only for completed so far
    private boolean optimization;

    @OneToMany(mappedBy = "route", cascade = CascadeType.ALL)
    private List<RoutePoint> routePoints = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "delivery_id", referencedColumnName = "id")
    private Delivery delivery;

    @OneToOne
    private Car car;

    @OneToOne
    private Store store;

    public void setRoutePoints(List<RoutePoint> routePoints) {
        List<RoutePoint> requiredRoutePoints = Optional.ofNullable(routePoints)
                .orElse(Collections.emptyList());
        this.routePoints.clear();
        this.routePoints.addAll(requiredRoutePoints);
        this.routePoints.forEach(routePoint -> routePoint.setRoute(this));
    }
}
