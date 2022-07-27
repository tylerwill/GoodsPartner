package com.goods.partner.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.goods.partner.entity.RouteStatus;
import lombok.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RouteDto {

    private int routeId;
    private RouteStatus status;
    private double totalWeight;
    private int totalPoints;
    private int totalOrders;
    private double distance;
    private Duration estimatedTime;
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalDateTime startTime;
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalDateTime finishTime;
    private Duration spentTime;
    private String storeName;
    private String storeAddress;
    private List<RoutePointDto> routePoints;
}