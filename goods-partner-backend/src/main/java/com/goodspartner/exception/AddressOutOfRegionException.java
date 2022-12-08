package com.goodspartner.exception;

import com.goodspartner.dto.MapPoint;

public class AddressOutOfRegionException extends RuntimeException {

    private static final String ADDRESS_OUT_OF_REGION_MESSAGE =
            "Requested address coordinates out of valid region:\n %s.\n Change delivery type if needed";

    public AddressOutOfRegionException(MapPoint mapPoint) {
        super(String.format(ADDRESS_OUT_OF_REGION_MESSAGE, mapPoint.getAddress()));
    }
}
