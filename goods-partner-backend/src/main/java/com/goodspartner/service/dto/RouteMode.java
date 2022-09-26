package com.goodspartner.service.dto;

public enum RouteMode {

    COOLER(true),
    REGULAR(false)
    ;

    private final boolean coolerNecessary;

    RouteMode(boolean coolerNecessity) {
        this.coolerNecessary = coolerNecessity;
    }

    public boolean isCoolerNecessary() {
        return coolerNecessary;
    }
}
