package com.goodspartner.service.impl;

import com.goodspartner.dto.StoreDto;
import com.goodspartner.service.StoreService;
import org.springframework.stereotype.Service;

@Service
public class MockedStoreService implements StoreService {
    private static final String STORE_NAME = "Склад №1";
    private static final String STORE_ADDRESS = "Фастів, вул. Широка, 15";

    private static final StoreDto MAIN_STORE = StoreDto.builder()
            .address(STORE_ADDRESS)
            .name(STORE_NAME)
            .build();

    @Override
    public StoreDto getMainStore() {
        return MAIN_STORE;
    }

}
