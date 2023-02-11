package com.goodspartner.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum SettingsGroup {

    CLIENT("CLIENT"),
    GOOGLE("GOOGLE");

    @Getter
    private final String value;
}
