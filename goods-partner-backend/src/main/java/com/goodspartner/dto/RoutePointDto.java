package com.goodspartner.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.goodspartner.entity.RoutePointStatus;
import lombok.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoutePointDto {
    private UUID id;
    private RoutePointStatus status;
    private LocalDateTime completedAt;
    private long clientId;
    private String clientName;
    private String address;
    private double addressTotalWeight;
    private long routePointDistantTime;

    @JsonIgnoreProperties(value = {"orderTotalWeight"})
    private List<AddressOrderDto> orders;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AddressOrderDto {
        private int id;
        private String orderNumber;
        private double orderTotalWeight;
    }
}
