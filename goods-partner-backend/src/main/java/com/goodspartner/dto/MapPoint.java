package com.goodspartner.dto;

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

    @AllArgsConstructor
    public enum AddressStatus {
        KNOWN("KNOWN"),
        UNKNOWN("UNKNOWN"),
        AUTOVALIDATED("AUTOVALIDATED");

        @Getter
        private final String status;
    }
}
