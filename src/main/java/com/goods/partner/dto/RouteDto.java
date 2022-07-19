package com.goods.partner.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RouteDto {

    private int routeId;
    private String status;
    private double totalWeight;
    private int totalPoints;
    private int totalOrders;
    private double distance;
    private LocalTime estimatedTime;
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalDateTime startTime;
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalDateTime finishTime;
    private LocalTime spentTime;
    private String storeName;
    private String storeAddress;
    private List<RoutePointDto> routePoints;
}