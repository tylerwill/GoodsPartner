package com.goods.partner.mapper;

import com.goods.partner.dto.StoreDto;
import com.goods.partner.dto.StoreOrderDto;
import com.goods.partner.entity.projection.StoreProjection;
import com.google.common.annotations.VisibleForTesting;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class StoreMapper {

    public List<StoreDto> mapStoreGroup(List<StoreProjection> storeProjections) {
        Map<Integer, StoreDto> storeMap = new HashMap<>();

        storeProjections.forEach(storeProjection -> {

            StoreDto storeDto = storeMap.computeIfAbsent(storeProjection.getStoreId(), k -> getStoreDto(storeProjection));

            StoreOrderDto storeOrderDto = new StoreOrderDto();
            storeOrderDto.setOrderId(storeProjection.getOrderId());
            storeOrderDto.setOrderNumber(storeProjection.getOrderNumber());
            storeOrderDto.setTotalOrderWeight(storeProjection.getTotalOrderWeight());
            storeDto.addStoreOrderDto(storeOrderDto);
        });

        return new ArrayList<>(storeMap.values());
    }

    @VisibleForTesting
    StoreDto getStoreDto(StoreProjection storeProjection) {
        StoreDto storeDto = new StoreDto();
        storeDto.setStoreId(storeProjection.getStoreId());
        storeDto.setStoreName(storeProjection.getStoreName());
        return storeDto;
    }

}

