package com.goods.partner.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.time.LocalTime;
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
    private LocalTime routPointDistantTime;

    @JsonIgnoreProperties(value = {"orderTotalWeight"})
    private List<AddressOrderDto> orders;
}
