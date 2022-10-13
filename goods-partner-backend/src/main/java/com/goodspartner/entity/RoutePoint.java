package com.goodspartner.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.goodspartner.dto.MapPoint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class RoutePoint {
    private UUID id;
    private RoutePointStatus status;
    private LocalDateTime completedAt;
    private long clientId;
    private String clientName;
    private String address;
    private double addressTotalWeight;
    private long routePointDistantTime;
    private LocalTime expectedArrival;
    private LocalTime expectedCompletion;
    private LocalTime deliveryStart;
    private LocalTime deliveryEnd;

    private MapPoint mapPoint;

    @JsonIgnoreProperties(value = {"orderTotalWeight"})
    private List<OrderReference> orders;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class OrderReference {
        private int id;
        private String orderNumber;
        private String comment;
        private double orderTotalWeight;
    }
}
