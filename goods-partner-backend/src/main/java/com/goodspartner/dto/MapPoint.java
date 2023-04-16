package com.goodspartner.dto;

import com.goodspartner.entity.AddressStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class MapPoint {

    private String address; // Geocoded address
    private double latitude;
    private double longitude;
    private AddressStatus status;
    private int serviceTimeMinutes;
}
