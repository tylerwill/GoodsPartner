package com.goods.partner.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@AllArgsConstructor
public enum CarStatus {
    ENABLE("ENABLE"),
    DISABLE("DISABLE");

    @Getter
    private final String status;

    public static CarStatus getCarStatus(String name) {

        return Arrays.stream(CarStatus.values())
                .filter(carStatus -> carStatus.status.equalsIgnoreCase(name))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("CarStatus name " + name + " is not correct!"));
    }
}
