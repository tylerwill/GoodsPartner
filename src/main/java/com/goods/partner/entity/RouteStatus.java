package com.goods.partner.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum RouteStatus {
    DRAFT("DRAFT"),
    APPROVED("APPROVED"),
    INPROGRESS("INPROGRESS"),
    COMPLETED("COMPLETED"),
    INCOMPLETE("INCOMPLETE");

    @Getter
    private final String value;
}
