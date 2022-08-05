package com.goods.partner.mapper;

import com.goods.partner.dto.StoreDto;
import com.goods.partner.dto.StoreOrderDto;
import com.goods.partner.entity.projection.StoreProjection;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import static org.mockito.Mockito.*;

@TestInstance(PER_CLASS)
class StoreMapperTest {

    private StoreMapper storeMapper = new StoreMapper();
    private StoreProjection storeProjection;

    @BeforeAll
    void setup() {
        storeMapper = new StoreMapper();
        storeProjection = new StoreProjection(1, "Склад №1", "Фастів, вул. Широка, 15",1, 5, 50.5);
    }

    @Test
    @DisplayName("Get StoreDto from StoreProjection")
    void test_givenStoreProjection_whenGetStoreDto_thenReturnStoreDto() {
        StoreDto storeDto = storeMapper.getStoreDto(storeProjection);
        assertEquals(1, storeDto.getStoreId());
        assertEquals("Склад №1", storeDto.getStoreName());
    }

    @Test
    @DisplayName("Get StoreDtoList from StoreProjection List")
    void test_givenStoreProjectionList_whenGetStoreDto_thenReturnStoreDtoList() {
        List<StoreDto> storeDtoList = storeMapper.mapStoreGroup(List.of(storeProjection));
        assertEquals(1, storeDtoList.size());

        StoreDto storeDto = storeDtoList.get(0);
        assertEquals(1, storeProjection.getStoreId());
        assertEquals("Склад №1", storeProjection.getStoreName());

        List<StoreOrderDto> orders = storeDto.getOrders();
        assertEquals(1, orders.size());

        StoreOrderDto storeOrderDto = orders.get(0);
        assertEquals(1, storeOrderDto.getOrderId());
        assertEquals("5", storeOrderDto.getOrderNumber());
        assertEquals(50.5, storeOrderDto.getTotalOrderWeight(), 0.001);
    }

    @Test
    @DisplayName("Get StoreDtoList from StoreProjection List checking method calls")
    void test_givenStoreProjectionList_whenGetStoreDto_thenReturnStoreDtoListVerify() {
        StoreProjection anotherStoreProjection = new StoreProjection(2, "Склад №2", "Фастів, вул. Широка, 15",1, 5, 50.5);
        StoreMapper spyStoreMapper = spy(storeMapper);
        List<StoreDto> storeDtoList = spyStoreMapper
                .mapStoreGroup(List.of(storeProjection, anotherStoreProjection));

        assertEquals(2, storeDtoList.size());
        verify(spyStoreMapper).mapStoreGroup(anyList());
        verify(spyStoreMapper, times(2)).getStoreDto(any(StoreProjection.class));
    }

}
