package com.goods.partner.entity;

import lombok.NoArgsConstructor;

import java.util.Arrays;

@NoArgsConstructor
public enum CarStatus {
    ENABLE,
    DISABLE;
    private String carStatusName;

    CarStatus(String carStatusName) {
        this.carStatusName = carStatusName;
    }

    public String getCarStatusName() {
        return carStatusName;
    }

    public static CarStatus getCarStatus(String carStatusName) {

        return Arrays.stream(CarStatus.values())
                .filter(carStatus -> carStatus.carStatusName.equalsIgnoreCase(carStatusName))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("CarStatus name " + carStatusName + " is not correct!"));
    }
}
