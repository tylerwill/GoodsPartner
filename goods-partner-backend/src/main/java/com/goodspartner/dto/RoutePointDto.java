package com.goodspartner.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.goodspartner.entity.RoutePointStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoutePointDto {

    private long id;
    private RoutePointStatus status;
    private String clientName;
    private String address;
    private double addressTotalWeight;
    private long routePointDistantTime;
    private boolean matchingExpectedDeliveryTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private LocalDateTime completedAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private LocalTime expectedArrival;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private LocalTime expectedCompletion;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private LocalTime deliveryStart;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private LocalTime deliveryEnd;

    private MapPoint mapPoint;

}
