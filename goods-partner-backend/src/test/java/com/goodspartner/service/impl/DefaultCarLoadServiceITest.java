package com.goodspartner.service.impl;


import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.spring.api.DBRider;
import com.goodspartner.AbstractBaseITest;
import com.goodspartner.dto.CarDto;
import com.goodspartner.dto.CarLoadDto;
import com.goodspartner.dto.OrderDto;
import com.goodspartner.dto.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@DBRider
class DefaultCarLoadServiceITest extends AbstractBaseITest {
    @Autowired
    private DefaultCarLoadService carLoadService;

    @Test
    @DataSet(value = "datasets/delivery/delivery-carload-controller-test.yml")
    @DisplayName("when Find By Delivery Id then Expected List Of CarLoadDto Returned")
    public void whenFindByDeliveryId_thenCarLoadDtoListReturned() {

        // Given
        UUID uuid = UUID.fromString("00000000-0000-0000-0000-000000000111");

        // When
        List<CarLoadDto> actualCarLoadList = carLoadService.findByDeliveryId(uuid);
        CarLoadDto actualCarLoadDto = actualCarLoadList.get(0);
        OrderDto actualOrderDto = actualCarLoadDto.getOrders().get(0);
        List<Product> productList = actualOrderDto.getProducts();
        Product product = productList.get(0);
        CarDto actualCarDto = actualCarLoadDto.getCar();

        // Then
        assertEquals(1, actualCarDto.getId());
        assertEquals("Mercedes Sprinter", actualCarDto.getName());
        assertEquals("AA 1111 CT", actualCarDto.getLicencePlate());
        assertEquals("Вальдемар Кипарисович", actualCarDto.getDriver());
        assertEquals(3000, actualCarDto.getWeightCapacity());
        assertEquals(true, actualCarDto.getAvailable());
        assertEquals(10, actualCarDto.getTravelCost());
        assertFalse(actualCarDto.getCooler());

        assertEquals(1, actualOrderDto.getId());
        assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000111"), actualOrderDto.getDeliveryId());
        assertEquals(12, actualOrderDto.getOrderWeight());
        assertEquals("вул.Єлізавети Чавдар, буд.36", actualOrderDto.getAddress());
        assertEquals("Кух Плюс ТОВ (Кухмайстер) бн", actualOrderDto.getClientName());
        assertEquals("f6f73d76-8005-11ec-b3ce-00155dd72305", actualOrderDto.getRefKey());
        assertEquals("Наталія Рябченко", actualOrderDto.getManagerFullName());
        assertEquals("00000002055", actualOrderDto.getOrderNumber());
        assertEquals("реп", actualOrderDto.getComment());
        assertEquals("REGULAR", actualOrderDto.getDeliveryType().toString());
        assertFalse(actualOrderDto.isFrozen());
        assertFalse(actualOrderDto.isExcluded());
        assertFalse(actualOrderDto.isDropped());

        assertEquals(1, productList.size());
        assertEquals("Наповнювач фруктово-ягідний (декоргель) (12 кг)", product.getProductName());
        assertEquals("Склад №1", product.getStoreName());
        assertEquals("кг", product.getMeasure());
        assertEquals(1.0, product.getCoefficient());
        assertEquals(12.0, product.getTotalProductWeight());
        assertEquals(12.0, product.getUnitWeight());

    }

}