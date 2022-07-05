package com.goods.partner.mapper;

import com.goods.partner.dto.StoreDto;
import com.goods.partner.dto.StoreOrderDto;
import com.goods.partner.entity.projection.StoreProjection;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;


class StoreMapperTest {

    private final StoreMapper storeMapper = new StoreMapper();
    private static StoreProjection storeProjection;

    @BeforeAll
    static void setup() {
        storeProjection = new StoreProjection(1, "Склад №1", 1, 5, 50.0);
    }


    @Test
    @DisplayName("Get StoreDto from StoreProjection")
    void test_givenStoreProjection_whenGetStoreDto_thenReturnStoreDto() {

        StoreDto storeDto = storeMapper.getStoreDto(storeProjection);

        assertEquals(storeDto.getStoreId(), storeProjection.getStoreId());
        assertEquals(storeDto.getStoreName(), storeProjection.getStoreName());

    }

    @Test
    @DisplayName("Get StoreDtoList from StoreProjection List")
    void test_givenStoreProjectionList_whenGetStoreDto_thenReturnStoreDtoList() {
        List<StoreDto> storeDtoList = storeMapper.mapStoreGroup(List.of(storeProjection));

        assertEquals(storeDtoList.size(), 1);

        StoreDto storeDto = storeDtoList.get(0);

        assertEquals(storeDto.getStoreId(), storeProjection.getStoreId());
        assertEquals(storeDto.getStoreName(), storeProjection.getStoreName());


        List<StoreOrderDto> orders = storeDto.getOrders();
        assertEquals(orders.size(), 1);

        StoreOrderDto storeOrderDto = orders.get(0);

        assertEquals(storeOrderDto.getOrderId(), storeProjection.getOrderId());
        assertEquals(storeOrderDto.getOrderNumber(), storeProjection.getOrderNumber());
        assertEquals(storeOrderDto.getTotalOrderWeight(), storeProjection.getTotalOrderWeight());

    }

    @Test
    @DisplayName("Get StoreDtoList from StoreProjection List checking method calls")
    void test_givenStoreProjectionList_whenGetStoreDto_thenReturnStoreDtoListVerify() {

        StoreMapper spyStoreMapper = spy(storeMapper);

        List<StoreDto> storeDtoList = spyStoreMapper.mapStoreGroup(List.of(storeProjection));

        assertEquals(storeDtoList.size(), 1);

        verify(spyStoreMapper, times(1)).getStoreDto(storeProjection);
    }


}