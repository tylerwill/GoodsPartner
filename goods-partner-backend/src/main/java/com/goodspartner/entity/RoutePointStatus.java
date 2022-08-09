package com.goodspartner.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum RoutePointStatus {
    PENDING("PENDING"),
    DONE("DONE"),
    SKIPPED("SKIPPED");

    @Getter
    private final String status;
}