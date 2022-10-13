package com.goodspartner.util;

import com.goodspartner.dto.Product;
import com.goodspartner.entity.Store;
import com.goodspartner.service.StoreService;
import com.goodspartner.service.dto.external.grandedolce.ODataProductDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ExternalOrderDataEnricherTest {

    private final StoreService mockedStoreService = mock(StoreService.class);
    private final ExternalOrderDataEnricher dtoHelper = new ExternalOrderDataEnricher(mockedStoreService);

    private ODataProductDto oDataProductInKg;
    private ODataProductDto oDataProductInL;
    private ODataProductDto oDataProductInPack;
    private ODataProductDto oDataProductInPiece;
    private Product product;

    @BeforeEach
    public void setUp() {
        oDataProductInKg = ODataProductDto.builder().measure("кг").amount(4).coefficient(1.0).totalProductWeight(26.4).build();
        oDataProductInPiece = ODataProductDto.builder().measure("шт").amount(11).totalProductWeight(0.3).build();
        oDataProductInPack = ODataProductDto.builder().measure("паков").totalProductWeight(100.0).coefficient(0.5).build();
        oDataProductInL = ODataProductDto.builder().measure("л").totalProductWeight(11.3).coefficient(1.0).build();

        product = Product.builder().amount(4).unitWeight(0.5).build();

        Store mockStore = mock(Store.class);
        when(mockedStoreService.getMainStore()).thenReturn(mockStore);
        when(mockStore.getName()).thenReturn("Склад №1");
    }


    @Test
    @DisplayName("Test odata product unit weight, depends on total product weight, amount, coefficient")
    void testCalculateProductUnitWeight() {
        var unitWeight = dtoHelper.calculateProductUnitWeight(oDataProductInKg);

        assertEquals(6.6, unitWeight);
    }

    @Test
    @DisplayName("Test get total product unit weight")
    void testGetTotalProductWeight_whenMeasure() {
        var totalWeightKg = dtoHelper.getTotalProductWeight(oDataProductInKg);
        assertEquals(26.4, totalWeightKg);
        var totalWeighL = dtoHelper.getTotalProductWeight(oDataProductInL);
        assertEquals(11.3, totalWeighL);
        var totalWeighUnit = dtoHelper.getTotalProductWeight(oDataProductInPiece);
        assertEquals(10.0, totalWeighUnit);
        var totalWeighPak = dtoHelper.getTotalProductWeight(oDataProductInPack);
        assertEquals(50.0, totalWeighPak);

    }

    @Test
    @DisplayName("Test get total product weight of pack and box")
    void testGetTotalProductWeight_whenMeasureIsPACK_BOX() {
        ODataProductDto boxWithPCS = ODataProductDto.builder().
                productName("715050 РА Форми тюльпани 150/50 Помаранчові (3200 шт)")
                .measure("ящ")
                .amount(3)
                .coefficient(3200)
                .totalProductWeight(3)
                .build();
        assertEquals(3.0, dtoHelper.getTotalProductWeight(boxWithPCS));

        ODataProductDto box = ODataProductDto.builder().
                productName("715050 РА Форми тюльпани 150/50 Помаранчові")
                .measure("паков")
                .amount(1)
                .coefficient(3.5)
                .totalProductWeight(3)
                .build();
        assertEquals(10.5, dtoHelper.getTotalProductWeight(box));
    }

    @Test
    @DisplayName("Test get total product weight of shtuck")
    void testGetTotalProductWeight_whenMeasureIsPCS() {
        ODataProductDto amountMoreThen10 = ODataProductDto.builder()
                .measure("шт")
                .amount(11)
                .coefficient(1)
                .totalProductWeight(3)
                .build();
        assertEquals(10.0, dtoHelper.getTotalProductWeight(amountMoreThen10));

        ODataProductDto amountFewerThen10 = ODataProductDto.builder()
                .measure("шт")
                .amount(5)
                .coefficient(1)
                .totalProductWeight(3)
                .build();
        assertEquals(5.0, dtoHelper.getTotalProductWeight(amountFewerThen10));
    }

    @Test
    @DisplayName("Test get total product weight of bucket and bank")
    void testGetTotalProductWeight_whenMeasureBUCKET_BANK() {
        ODataProductDto bucket = ODataProductDto.builder()
                .measure("відро")
                .amount(2)
                .coefficient(2.5)
                .totalProductWeight(3)
                .build();
        assertEquals(7.5, dtoHelper.getTotalProductWeight(bucket));

        ODataProductDto bank = ODataProductDto.builder()
                .measure("банк")
                .amount(2)
                .coefficient(2.5)
                .totalProductWeight(2)
                .build();
        assertEquals(5.0, dtoHelper.getTotalProductWeight(bank));
    }

    @Test
    @DisplayName("Test get total product weight of unknown measure")
    void testGetTotalProductWeight_whenMeasureIsUnknown() {
        ODataProductDto bucket = ODataProductDto.builder()
                .measure("бут")
                .amount(10)
                .coefficient(2.5)
                .totalProductWeight(100)
                .build();
        assertEquals(1.0, dtoHelper.getTotalProductWeight(bucket));
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