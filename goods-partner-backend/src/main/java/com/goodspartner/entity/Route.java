package com.goodspartner.entity;

import com.vladmihalcea.hibernate.type.json.JsonType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "routes")
@TypeDef(name = "json", typeClass = JsonType.class)
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

    @Type(type = "json")
    @Column(columnDefinition = "jsonb")
    private List<RoutePoint> routePoints;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "delivery_id", referencedColumnName = "id")
    private Delivery delivery;

    @OneToOne
    private Car car;

    @OneToOne
    private Store store;
}
