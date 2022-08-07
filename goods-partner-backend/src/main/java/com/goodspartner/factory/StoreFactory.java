package com.goodspartner.factory;

import org.springframework.stereotype.Component;

@Component
public class StoreFactory {
    private static final String STORE_NAME = "Склад №1";
    private static final String STORE_ADDRESS = "Фастів, вул. Широка, 15";

    // TODO rewrite to singleton
    public Store getStore() {
        return Store.builder()
                .address(STORE_ADDRESS)
                .name(STORE_NAME)
                .build();
    }

}
