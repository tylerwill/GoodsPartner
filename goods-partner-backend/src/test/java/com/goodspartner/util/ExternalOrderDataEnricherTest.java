package com.goodspartner.util;

import com.goodspartner.dto.Product;
import com.goodspartner.service.dto.external.grandedolce.ODataProductDto;
import com.goodspartner.service.impl.MockedStoreService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ExternalOrderDataEnricherTest {

    private final MockedStoreService mockedStoreService = new MockedStoreService();
    private final ExternalOrderDataEnricher dtoHelper = new ExternalOrderDataEnricher(mockedStoreService);

    private ODataProductDto oDataProductInKg;
    private ODataProductDto oDataProductInL;
    private ODataProductDto oDataProductInPack;
    private ODataProductDto oDataProductInPiece;
    private Product product;

    @BeforeEach
    public void setUp() {
        oDataProductInKg = ODataProductDto.builder().measure("кг").amount(4).coefficient(1.0).totalProductWeight(26.4).build();
        oDataProductInPiece = ODataProductDto.builder().measure("шт").totalProductWeight(0.3).build();
        oDataProductInPack = ODataProductDto.builder().measure("пак").totalProductWeight(100.0).build();
        oDataProductInL = ODataProductDto.builder().measure("л").totalProductWeight(11.3).build();

        product = Product.builder().amount(4).unitWeight(0.5).build();
    }


    @Test
    @DisplayName("Test odata product unit weight, depends on total product weight, amount, coefficient")
    void testCalculateProductUnitWeight() {
        var unitWeight = dtoHelper.calculateProductUnitWeight(oDataProductInKg);

        assertEquals(6.6, unitWeight);
    }

    @Test
    @DisplayName("Test get total product unit weight depends on allowed measure (кг and л as (1:1), шт (many : 1), not allowed 0")
    void testGetTotalProductWeight_whenMeasure() {
        var totalWeightKg = dtoHelper.getTotalProductWeight(oDataProductInKg);
        assertEquals(26.4, totalWeightKg);
        var totalWeighL = dtoHelper.getTotalProductWeight(oDataProductInL);
        assertEquals(11.3, totalWeighL);
        var totalWeighUnit = dtoHelper.getTotalProductWeight(oDataProductInPiece);
        assertEquals(1.0, totalWeighUnit);
        var totalWeighPak = dtoHelper.getTotalProductWeight(oDataProductInPack);
        assertEquals(0.0, totalWeighPak);
    }

    @Test
    @DisplayName("Test get total product weight as multiple of amount and unit weight")
    void testGetTotalProductWeight() {
        double totalProductWeight = dtoHelper.getTotalProductWeight(product);

        assertEquals(2.0, totalProductWeight);
    }

    @Test
    @DisplayName("Test set to product store name and calculated total weight")
    void testEnrichProduct() {
        dtoHelper.enrichProduct(product);

        assertEquals(2.0, product.getTotalProductWeight());
        assertEquals("Склад №1", product.getStoreName());
    }

    @Test
    @DisplayName("Test set to oData product store name, calculated total and unit weight")
    void testEnrichODataProduct() {
        dtoHelper.enrichODataProduct(oDataProductInKg);

        assertEquals(6.6, oDataProductInKg.getUnitWeight());
        assertEquals(26.4, oDataProductInKg.getTotalProductWeight());
        assertEquals("Склад №1", oDataProductInKg.getStoreName());
    }
}