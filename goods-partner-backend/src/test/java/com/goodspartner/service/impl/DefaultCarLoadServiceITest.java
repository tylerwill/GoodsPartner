package com.goodspartner.service.impl;


import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.spring.api.DBRider;
import com.goodspartner.AbstractBaseITest;
import com.goodspartner.dto.Product;
import com.goodspartner.entity.AddressExternal;
import com.goodspartner.entity.Car;
import com.goodspartner.entity.CarLoad;
import com.goodspartner.entity.OrderExternal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DBRider
class DefaultCarLoadServiceITest extends AbstractBaseITest {
    @Autowired
    private DefaultCarLoadService carLoadService;

    @Test
    @DataSet(value = "datasets/carload/delivery-carload-controller-test.yml",
            cleanAfter = true, cleanBefore = true, skipCleaningFor = "flyway_schema_history")
    @DisplayName("when Find By Delivery Id then Expected List Of CarLoadDto Returned")
    public void whenFindByDeliveryId_thenCarLoadDtoListReturned() {

        // Given
        UUID uuid = UUID.fromString("00000000-0000-0000-0000-000000000111");

        // When
        List<CarLoad> actualCarLoadList = carLoadService.findByDeliveryId(uuid);
        CarLoad actualCarLoad = actualCarLoadList.get(0);
        OrderExternal actualOrder = actualCarLoad.getOrders().get(0);
        List<Product> productList = actualOrder.getProducts();
        Product product = productList.get(0);
        Car actualCarDto = actualCarLoad.getCar();

        // Then
        assertEquals(1, actualCarDto.getId());
        assertEquals("Mercedes Sprinter", actualCarDto.getName());
        assertEquals("AA 1111 CT", actualCarDto.getLicencePlate());
        assertEquals("Test Driver", actualCarDto.getDriver().getUserName());
        assertEquals(3000, actualCarDto.getWeightCapacity());
        assertTrue(actualCarDto.isAvailable());
        assertEquals(10, actualCarDto.getTravelCost());
        assertFalse(actualCarDto.isCooler());

        assertEquals(1, actualOrder.getId());

        assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000111"), actualOrder.getDelivery().getId());
        assertEquals(12, actualOrder.getOrderWeight());

        AddressExternal addressExternal = actualOrder.getAddressExternal();
        AddressExternal.OrderAddressId orderAddressId = addressExternal.getOrderAddressId();

        assertEquals("вул.Єлізавети Чавдар, буд.36", orderAddressId.getOrderAddress());
        assertEquals("Кух Плюс ТОВ (Кухмайстер) бн", orderAddressId.getClientName());
        assertEquals("f6f73d76-8005-11ec-b3ce-00155dd72305", actualOrder.getRefKey());
        assertEquals("Наталія Рябченко", actualOrder.getManagerFullName());
        assertEquals("00000002055", actualOrder.getOrderNumber());
        assertEquals("реп", actualOrder.getComment());
        assertEquals("REGULAR", actualOrder.getDeliveryType().toString());
        assertFalse(actualOrder.isFrozen());
        assertFalse(actualOrder.isExcluded());
        assertFalse(actualOrder.isDropped());

        assertEquals(1, productList.size());
        assertEquals("Наповнювач фруктово-ягідний (декоргель) (12 кг)", product.getProductName());
        assertEquals("Склад №1", product.getStoreName());
        assertEquals("кг", product.getMeasure());
        assertEquals(1.0, product.getCoefficient());
        assertEquals(12.0, product.getTotalProductWeight());
        assertEquals(12.0, product.getUnitWeight());

    }

}