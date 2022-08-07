package com.goodspartner.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.time.Duration;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoutePointDto {
    private long clientId;
    private String clientName;
    private String address;
    private double addressTotalWeight;
    private Duration routePointDistantTime;

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
