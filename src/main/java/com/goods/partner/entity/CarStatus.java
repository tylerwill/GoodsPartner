package com.goods.partner.entity;

import lombok.NoArgsConstructor;

import java.util.Arrays;

@NoArgsConstructor
public enum CarStatus {
    ENABLE,
    DISABLE;

    public static CarStatus getCarStatus(String name) {

        return Arrays.stream(CarStatus.values())
                .filter(carStatus -> carStatus.name().equalsIgnoreCase(name))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("CarStatus name " + name + " is not correct!"));
    }
}
