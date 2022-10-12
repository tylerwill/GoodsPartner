package com.goodspartner.dto;

import com.goodspartner.entity.RoutePoint;
import com.goodspartner.entity.RouteStatus;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class RouteDto {
    private int id;
    private RouteStatus status;
    private double totalWeight;
    private int totalPoints;
    private int totalOrders;
    private double distance;
    private long estimatedTime;
    private LocalDateTime startTime;
    private LocalDateTime finishTime;
    private long spentTime;
    private boolean optimization;
    private List<RoutePoint> routePoints;
    private CarDto car;
    private StoreDto store;
}