package com.goodspartner.dto;

import com.goodspartner.entity.AddressStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MapPoint {

    private String address; // Geocoded address
    private double latitude;
    private double longitude;
    private AddressStatus status;
}
