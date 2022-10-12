package com.goodspartner.web.controller;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.spring.api.DBRider;
import com.goodspartner.AbstractWebITest;
import com.goodspartner.config.TestSecurityDisableConfig;
import com.goodspartner.dto.OrderDto;
import com.goodspartner.dto.Product;
import com.goodspartner.service.client.GoogleClient;
import com.goodspartner.service.IntegrationService;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.Geometry;
import com.google.maps.model.LatLng;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import({TestSecurityDisableConfig.class})
@AutoConfigureMockMvc(addFilters = false)
@DBRider
@TestInstance(PER_CLASS)
class OrdersControllerITest extends AbstractWebITest {

    private static final double AUTOVALIDATED_ADDRESS_LATITUDE = 50.51;
    private static final double AUTOVALIDATED_ADDRESS_LONGITUDE = 30.79;
    private static final double OUT_OF_REGION_ADDRESS_LATITUDE = 50.25;
    private static final double OUT_OF_REGION_ADDRESS_LONGITUDE = 28.69;
    private static final double NOVA_POSHTA_ADDRESS_LATITUDE = 50.0823797;
    private static final double NOVA_POSHTA_ADDRESS_LONGITUDE = 29.9285037;
    private static final String FORMATTED_ADDRESS = "вулиця Марії Лагунової, 11, Бровари, Київська обл., Україна, 07400";
    private static final String AUTOVALIDATED_ADDRESS = "Бровари, Марії Лагунової, 11";
    private static final String UNKNOWN_ADDRESS = "вул. Невідома 8667";
    private static final String KNOWN_ADDRESS = "м.Львів, вулиця Незалежності 105";
    private static final String OUT_OF_REGION_ADDRESS = "м. Житомир, вул Корольова, 1";
    private static final String NOVA_POSHTA_ADDRESS = "вулиця Київська, 34, Фастів, Київська обл., Україна, 08500";

    @MockBean
    private IntegrationService integrationService;

    @MockBean
    private GoogleClient googleClient;

    private OrderDto orderWithAutovalidatedAddress;
    private OrderDto orderWithUnknownAddress;
    private OrderDto orderWithKnownAddress;
    private OrderDto orderWithOutOfRegionAddress;
    private OrderDto orderWithSpecialComment;

    @BeforeAll
    void setUp() {

        Product productFirst = Product.builder()
                .amount(1)
                .storeName("Склад №1")
                .unitWeight(12.0)
                .productName("Наповнювач фруктово-ягідний (декоргель) (12 кг)")
                .totalProductWeight(12.0)
                .build();

        orderWithAutovalidatedAddress = OrderDto.builder()
                .orderNumber("45678")
                .refKey("01grande-0000-0000-0000-000000000000")
                .createdDate(LocalDate.parse("2022-02-17"))
                .clientName("Домашня випічка")
                .address(AUTOVALIDATED_ADDRESS)
                .comment("бн")
                .managerFullName("Балашова Лариса")
                .products(List.of(productFirst))
                .orderWeight(12.00)
                .build();

        Product productSecond = Product.builder()
                .amount(1)
                .storeName("Склад №2")
                .unitWeight(20.0)
                .productName("66784 Арахісова паста")
                .totalProductWeight(20.0)
                .build();

        orderWithUnknownAddress = OrderDto.builder()
                .orderNumber("43532")
                .refKey("02grande-0000-0000-0000-000000000000")
                .createdDate(LocalDate.parse("2022-02-17"))
                .clientName("ТОВ Пекарня")
                .address(UNKNOWN_ADDRESS)
                .comment("бн")
                .managerFullName("Шульженко Олег")
                .products(List.of(productSecond))
                .orderWeight(20.00)
                .build();

        Product productThird = Product.builder()
                .amount(1)
                .storeName("Склад №1")
                .unitWeight(10.0)
                .productName("Паста шоколадна(10 кг)")
                .totalProductWeight(10.0)
                .build();

        orderWithKnownAddress = OrderDto.builder()
                .orderNumber("45679")
                .refKey("03grande-0000-0000-0000-000000000000")
                .createdDate(LocalDate.parse("2022-02-17"))
                .clientName("Фоззі-Фуд")
                .address(KNOWN_ADDRESS)
                .comment("бн")
                .managerFullName("Кравченко Сергій")
                .products(List.of(productThird))
                .orderWeight(10.00)
                .build();

        Product productFourth = Product.builder()
                .amount(1)
                .storeName("Склад №1")
                .unitWeight(1.0)
                .productName("Гурме Голд  (AV23AB) 12л")
                .totalProductWeight(1.0)
                .build();

        orderWithOutOfRegionAddress = OrderDto.builder()
                .orderNumber("45789")
                .refKey("04grande-0000-0000-0000-000000000000")
                .createdDate(LocalDate.parse("2022-02-17"))
                .clientName("Тераса ТОВ (Іль Моліно)")
                .address(OUT_OF_REGION_ADDRESS)
                .comment("бн")
                .managerFullName("Кравченко Сергій")
                .products(List.of(productFourth))
                .orderWeight(1.00)
                .build();

        orderWithSpecialComment = OrderDto.builder()
                .orderNumber("00000002130")
                .refKey("05grande-0000-0000-0000-000000000000")
                .createdDate(LocalDate.parse("2022-02-17"))
                .clientName("Домашня випічка")
                .address(AUTOVALIDATED_ADDRESS)
                .comment("доставка: Нова пошта, заморозка: доставка в холодильнику")
                .managerFullName("Балашова Лариса")
                .isFrozen(false)
                .products(List.of(productFirst))
                .orderWeight(12.00)
                .build();
    }

    @Test
    @DisplayName("when Get Orders then Expected Json Returned")
    void whenGetOrders_then_expectedJsonReturned() throws Exception {

        when(integrationService.findAllByShippingDate(LocalDate.parse("2022-07-10")))
                .thenReturn(
                        List.of(orderWithAutovalidatedAddress,
                                orderWithUnknownAddress,
                                orderWithOutOfRegionAddress));

        mockGoogleGeocodeService();

        when(integrationService.calculateTotalOrdersWeight(
                List.of(orderWithAutovalidatedAddress,
                        orderWithUnknownAddress,
                        orderWithOutOfRegionAddress)))
                .thenReturn(33.00);

        mockMvc.perform(get("/api/v1/orders")
                        .param("date", "2022-07-10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content()
                        .json(getResponseAsString("response/order-controller.json")));
    }

    @Test
    @DisplayName("when Get Orders Containing Special Comment then Expected Json Returned")
    void whenGetOrdersContainingSpecialComment_then_expectedJsonReturned() throws Exception {

        when(integrationService.findAllByShippingDate(LocalDate.parse("2022-07-10")))
                .thenReturn(
                        List.of(orderWithAutovalidatedAddress,
                                orderWithUnknownAddress,
                                orderWithOutOfRegionAddress,
                                orderWithSpecialComment));

        mockGoogleGeocodeService();

        when(integrationService.calculateTotalOrdersWeight(
                List.of(orderWithAutovalidatedAddress,
                        orderWithUnknownAddress,
                        orderWithOutOfRegionAddress,
                        orderWithSpecialComment)))
                .thenReturn(45.00);

        mockMvc.perform(get("/api/v1/orders")
                        .param("date", "2022-07-10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content()
                        .json(getResponseAsString("response/orders-with-special-comments.json")));
    }

    @Test
    @DataSet(value = "common/orders_external/dataset_address_external.yml",
            cleanBefore = true,
            skipCleaningFor = {"flyway_schema_history"})
    @DisplayName("given Known Addresses Table (Cache) when Get Orders then Expected Json Returned")
    void givenKnownAddressesTable_whenGetOrders_then_expectedJsonReturned() throws Exception {

        when(integrationService.findAllByShippingDate(LocalDate.parse("2022-07-10")))
                .thenReturn(
                        List.of(orderWithAutovalidatedAddress,
                                orderWithUnknownAddress,
                                orderWithKnownAddress,
                                orderWithOutOfRegionAddress));

        mockGoogleGeocodeService();

        when(integrationService
                .calculateTotalOrdersWeight(
                        List.of(orderWithAutovalidatedAddress,
                                orderWithUnknownAddress,
                                orderWithKnownAddress,
                                orderWithOutOfRegionAddress)))
                .thenReturn(43.00);

        mockMvc.perform(get("/api/v1/orders")
                        .param("date", "2022-07-10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content()
                        .json(getResponseAsString("response/order-controller-with-known-address.json")));
    }

    @Test
    @DisplayName("given No Orders For Specified Date when Get Orders then Json With Empty Orders Field Returned")
    void givenNoOrdersForSpecifiedDate_whenGetOrders_thenJsonWithEmptyOrdersFieldReturned() throws Exception {
        mockMvc.perform(get("/api/v1/orders")
                        .param("date", "2000-01-01")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))

                .andExpect(status().isOk())
                .andExpect(content()
                        .json("""
                                {
                                   "date":"2000-01-01",
                                   "orders":[],
                                   "totalOrdersWeight":0.00
                                }
                                   """));
    }

    private void mockGoogleGeocodeService() {

        //configure mock for autovalidated address
        LatLng latLngMockForAutovalidatedAddress =
                new LatLng(AUTOVALIDATED_ADDRESS_LATITUDE, AUTOVALIDATED_ADDRESS_LONGITUDE);
        Geometry geometryMockForAutovalidatedAddress = new Geometry();
        geometryMockForAutovalidatedAddress.location = latLngMockForAutovalidatedAddress;
        GeocodingResult geocodingResultForAutovalidatedAddress = new GeocodingResult();
        geocodingResultForAutovalidatedAddress.formattedAddress = FORMATTED_ADDRESS;
        geocodingResultForAutovalidatedAddress.geometry = geometryMockForAutovalidatedAddress;

        GeocodingResult[] mockedGeocodeResultsForAutovalidatedAddress =
                new GeocodingResult[]{geocodingResultForAutovalidatedAddress};

        when(googleClient.getGeocodingResults(AUTOVALIDATED_ADDRESS))
                .thenReturn(mockedGeocodeResultsForAutovalidatedAddress);


        //configure mock for outside region address
        LatLng latLngMockForForOutsideRegionAddress =
                new LatLng(OUT_OF_REGION_ADDRESS_LATITUDE, OUT_OF_REGION_ADDRESS_LONGITUDE);
        Geometry geometryMockForForOutsideRegionAddress = new Geometry();
        geometryMockForForOutsideRegionAddress.location = latLngMockForForOutsideRegionAddress;
        GeocodingResult geocodingResultForOutsideRegionAddress = new GeocodingResult();
        geocodingResultForOutsideRegionAddress.geometry = geometryMockForForOutsideRegionAddress;

        GeocodingResult[] mockedGeocodeResultsForOutsideRegionAddress =
                new GeocodingResult[]{geocodingResultForOutsideRegionAddress};

        when(googleClient.getGeocodingResults(OUT_OF_REGION_ADDRESS))
                .thenReturn(mockedGeocodeResultsForOutsideRegionAddress);

        //configure mock for unknown region address
        when(googleClient.getGeocodingResults(UNKNOWN_ADDRESS))
                .thenReturn(new GeocodingResult[0]);

        //configure mock for Nova Poshta address
        LatLng latLngMockForNovaPoshtaAddress =
                new LatLng(NOVA_POSHTA_ADDRESS_LATITUDE, NOVA_POSHTA_ADDRESS_LONGITUDE);
        Geometry geometryMockForNovaPoshtaAddress = new Geometry();
        geometryMockForNovaPoshtaAddress.location = latLngMockForNovaPoshtaAddress;
        GeocodingResult geocodingResultForNovaPoshtaAddress = new GeocodingResult();
        geocodingResultForNovaPoshtaAddress.formattedAddress = NOVA_POSHTA_ADDRESS;
        geocodingResultForNovaPoshtaAddress.geometry = geometryMockForNovaPoshtaAddress;

        GeocodingResult[] mockedGeocodeResultsForNovaPoshtaAddress =
                new GeocodingResult[]{geocodingResultForNovaPoshtaAddress};

        when(googleClient.getGeocodingResults(NOVA_POSHTA_ADDRESS))
                .thenReturn(mockedGeocodeResultsForNovaPoshtaAddress);
    }
}