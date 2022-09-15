package com.goodspartner.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum DeliveryStatus {
    DRAFT("DRAFT"),
    APPROVED("APPROVED"),
    COMPLETED ("COMPLETED");

    @Getter
    private final String status;
}
