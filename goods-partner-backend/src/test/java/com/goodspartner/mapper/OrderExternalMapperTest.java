package com.goodspartner.mapper;

import com.goodspartner.dto.OrderDto;
import com.goodspartner.dto.ProductDto;
import com.goodspartner.entity.OrderExternal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OrderExternalMapperTest {

    private final OrderExternalMapper orderExternalMapper = Mappers.getMapper(OrderExternalMapper.class);

    @Test
    @DisplayName("test given OrderDto when Map OrderDto then Return OrderExternal")
    void test_givenOrderDto_whenMapOrderDto_thenReturnOrderExternal() {
        ProductDto productDto = ProductDto.builder()
                .amount(1)
                .storeName("Склад №1")
                .unitWeight(12.00)
                .productName("Наповнювач фруктово-ягідний (декоргель) (12 кг)")
                .totalProductWeight(12.00)
                .build();

        OrderDto orderDto = OrderDto.builder()
                .id(1)
                .orderNumber("45678")
                .createdDate(LocalDate.of(2022, 2, 17))
                .clientName("Домашня випічка")
                .address("Бровари, Марії Лагунової, 11")
                .managerFullName("Балашова Лариса")
                .products(List.of(productDto))
                .orderWeight(12.00)
                .build();

        OrderExternal orderExternal = orderExternalMapper.mapOrderDtoToOrderExternal(orderDto);

        assertEquals(1, orderExternal.getId());
        assertEquals("45678", orderExternal.getOrderNumber());
        assertEquals(LocalDate.of(2022, 02, 17), orderExternal.getCreatedDate());
        assertEquals("Домашня випічка", orderExternal.getClientName());
        assertEquals("Бровари, Марії Лагунової, 11", orderExternal.getAddress());
        assertEquals("Балашова Лариса", orderExternal.getManagerFullName());
        assertEquals(List.of(productDto), orderExternal.getProducts());
        assertEquals(12.00, orderExternal.getOrderWeight());
    }
}