package com.goodspartner.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum SettingsCategory {

    ACCOUNTING("ACCOUNTING"),
    ROUTING("ROUTING"),
    BUSINESS("BUSINESS"),
    GEOCODE("GEOCODE");

    @Getter
    private final String value;
}
