package com.goodspartner.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
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


// TODO : fix formats once we migrate RoutePoint from json to table. Formats should be applied to DTO not for entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class RoutePoint {
    private UUID id;
    private RoutePointStatus status;
    private long clientId;
    private String clientName;
    private String address;
    private double addressTotalWeight;
    private long routePointDistantTime;

    //    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private LocalDateTime completedAt;
    //    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private LocalTime expectedArrival;
    //    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private LocalTime expectedCompletion;
    //    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private LocalTime deliveryStart;
    //    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
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
