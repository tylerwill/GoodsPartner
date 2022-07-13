package com.goods.partner.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "routes")
public class Route {

    @Id
    private int id;

    private String status;
    private double totalWeight;
    private int totalPoints;
    private int totalOrders;
    private double distance;
    private LocalDateTime estimatedTime;
    private LocalDateTime startTime;
    private LocalDateTime finishTime;
    private LocalDateTime spentTime;
    private String routeLink;
    private String storeName;
    private String storeAddress;
}
