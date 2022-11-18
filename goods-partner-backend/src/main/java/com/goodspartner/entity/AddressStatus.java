package com.goodspartner.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum AddressStatus {
    KNOWN("KNOWN"),
    UNKNOWN("UNKNOWN"),
    AUTOVALIDATED("AUTOVALIDATED");

    @Getter
    private final String status;
}
