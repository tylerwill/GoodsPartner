package com.goodspartner.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum DeliveryType {

    REGULAR("REGULAR"),
    POSTAL("POSTAL"),
    SELF_SERVICE("SELF_SERVICE"),
    PRE_PACKING("PRE_PACKING"),
    ;

    @Getter
    private final String name;
}
