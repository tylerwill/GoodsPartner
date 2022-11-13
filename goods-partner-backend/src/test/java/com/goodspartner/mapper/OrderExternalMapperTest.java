package com.goodspartner.mapper;

import com.goodspartner.dto.MapPoint;
import com.goodspartner.dto.OrderDto;
import com.goodspartner.dto.Product;
import com.goodspartner.entity.AddressExternal;
import com.goodspartner.entity.OrderExternal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OrderExternalMapperTest {

    private final OrderExternalMapper orderExternalMapper = Mappers.getMapper(OrderExternalMapper.class);

    @Test
    @DisplayName("test given OrderDto when Map OrderDto then Return OrderExternal")
    void test_givenOrderDto_whenMapOrderDto_thenReturnOrderExternal() {

        MapPoint mapPoint = MapPoint.builder()
                .address("м.Київ")
                .latitude(30.0)
                .longitude(50.0)
                .status(MapPoint.AddressStatus.AUTOVALIDATED)
                .build();

        Product product = Product.builder()
                .amount(1)
                .storeName("Склад №1")
                .unitWeight(12.00)
                .productName("Наповнювач фруктово-ягідний (декоргель) (12 кг)")
                .totalProductWeight(12.00)
                .build();

        OrderDto orderDto = OrderDto.builder()
                .id(1)
                .refKey("refKey")
                .orderNumber("45678")
                .shippingDate(LocalDate.of(2022, 2, 17))
                .comment("comment")
                .managerFullName("Балашова Лариса")
                .isFrozen(true)
                .deliveryStart(LocalTime.of(9, 0))
                .deliveryFinish(LocalTime.of(18, 30))
                .clientName("Домашня випічка")
                .address("Бровари, Марії Лагунової, 11")
                .mapPoint(mapPoint)
                .products(List.of(product))
                .orderWeight(12.00)
                .deliveryId(UUID.fromString("237e9877-e79b-12d4-a765-321741963000"))
                .build();

        OrderExternal orderExternal = orderExternalMapper.mapOrderDtoToOrderExternal(orderDto);
        AddressExternal addressExternal = orderExternal.getAddressExternal();
        AddressExternal.OrderAddressId orderAddressId = addressExternal.getOrderAddressId();

        assertEquals(1, orderExternal.getId());
        assertEquals("refKey", orderExternal.getRefKey());
        assertEquals("45678", orderExternal.getOrderNumber());
        assertEquals(LocalDate.of(2022, 2, 17), orderExternal.getShippingDate());
        assertEquals("comment", orderExternal.getComment());
        assertEquals("Балашова Лариса", orderExternal.getManagerFullName());
        assertTrue(orderExternal.isFrozen());
        assertEquals(LocalTime.of(9, 0), orderExternal.getDeliveryStart());
        assertEquals(LocalTime.of(18, 30), orderExternal.getDeliveryFinish());
        assertEquals("Бровари, Марії Лагунової, 11", orderAddressId.getOrderAddress());
        assertEquals("Домашня випічка", orderAddressId.getClientName());
        assertEquals("м.Київ", addressExternal.getValidAddress());
        assertEquals(30.0, addressExternal.getLatitude());
        assertEquals(50.0, addressExternal.getLongitude());
        assertEquals(List.of(product), orderExternal.getProducts());
        assertEquals(12.00, orderExternal.getOrderWeight());
        assertEquals(UUID.fromString("237e9877-e79b-12d4-a765-321741963000"), orderExternal.getDelivery().getId());
    }
}