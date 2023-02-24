package com.goodspartner.exception;

import com.goodspartner.dto.MapPoint;

public class AddressOutOfRegionException extends RuntimeException {

    private static final String ADDRESS_OUT_OF_REGION_MESSAGE =
            "Requested address coordinates out of valid region:\n %s.\n Change delivery type if needed";
    private static final String ADDRESS_OUT_OF_REGION_MESSAGE_UKR =
            "Дана адреса знаходиться поза межами доступного регіону:\n %s.\n При необхідності змініть тип доставки";

    public AddressOutOfRegionException(MapPoint mapPoint) {
        super(String.format(ADDRESS_OUT_OF_REGION_MESSAGE_UKR, mapPoint.getAddress()));
    }
}
