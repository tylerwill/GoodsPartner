package com.goodspartner.service.impl;

import com.goodspartner.dto.MapPoint;
import com.goodspartner.dto.StoreDto;
import com.goodspartner.service.StoreService;
import org.springframework.stereotype.Service;

import static com.goodspartner.dto.MapPoint.AddressStatus.KNOWN;

@Service
public class MockedStoreService implements StoreService {
    private static final String STORE_NAME = "Склад №1";
    private static final String STORE_ADDRESS = "Фастів, вул. Калинова, 15";

    private static final StoreDto MAIN_STORE = StoreDto.builder()
            .address(STORE_ADDRESS)
            .name(STORE_NAME)
            .mapPoint(MapPoint.builder()
                    .address("15, Калинова вулиця, Фастів, Фастівська міська громада, Фастівський район, Київська область, 08500, Україна")
                    .latitude(50.08340335)
                    .longitude(29.885050630832627)
                    .status(KNOWN)
                    .build())
            .build();

    @Override
    public StoreDto getMainStore() {
        return MAIN_STORE;
    }

}
