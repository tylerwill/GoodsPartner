package com.goodspartner.service.impl;


import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.spring.api.DBRider;
import com.goodspartner.AbstractBaseITest;
import com.goodspartner.dto.ProductLoadDto;
import com.goodspartner.dto.ProductShippingDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DBRider
class DefaultShippingServiceITest extends AbstractBaseITest {

    @Autowired
    private DefaultShippingService defaultShippingService;

    @Test
    @DataSet(value = "datasets/delivery/delivery-shipping-controller-test.yml")
    @DisplayName("when Find By Delivery Id then Expected Product Shipping Dto List Returned")
    public void whenFindByDeliveryId_ThenProductShippingDtoListReturned() {

        // Given
        UUID uuid = UUID.fromString("00000000-0000-0000-0000-000000000111");

        // When
        List<ProductShippingDto> productShippingDtosList = defaultShippingService.findByDeliveryId(uuid);
        ProductShippingDto productShippingDto = productShippingDtosList.get(0);
        List<ProductLoadDto> productLoadDtoList = productShippingDto.getProductLoadDtos();
        ProductLoadDto productLoadDto = productLoadDtoList.get(0);

        // Then
        assertEquals("Наповнювач фруктово-ягідний (декоргель) (12 кг)", productShippingDto.getArticle());
        assertEquals(1, productShippingDto.getTotalAmount());
        assertEquals(12.0, productShippingDto.getTotalWeight());

        assertEquals("00000002055", productLoadDto.getOrderNumber());
        assertEquals("Mercedes Sprinter (AA 1111 CT)", productLoadDto.getCar());
        assertEquals(1, productLoadDto.getAmount());
        assertEquals(12.0, productLoadDto.getWeight());
        assertEquals(12.0, productLoadDto.getTotalWeight());

    }
}