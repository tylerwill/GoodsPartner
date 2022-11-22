package com.goodspartner.web.controller;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.spring.api.DBRider;
import com.goodspartner.AbstractWebITest;
import com.goodspartner.config.TestConfigurationToCountAllQueries;
import com.goodspartner.config.TestSecurityEnableConfig;
import com.goodspartner.dto.DeliveryDto;
import com.goodspartner.dto.OrderDto;
import com.goodspartner.dto.Product;
import com.goodspartner.entity.Delivery;
import com.goodspartner.entity.DeliveryHistoryTemplate;
import com.goodspartner.entity.DeliveryStatus;
import com.goodspartner.entity.DeliveryType;
import com.goodspartner.repository.DeliveryRepository;
import com.goodspartner.repository.OrderExternalRepository;
import com.goodspartner.service.EventService;
import com.goodspartner.service.IntegrationService;
import com.goodspartner.service.client.GoogleClient;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.Geometry;
import com.google.maps.model.LatLng;
import com.vladmihalcea.sql.SQLStatementCountValidator;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.shaded.org.awaitility.Durations;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

@Slf4j
@DBRider
@Import({
        TestSecurityEnableConfig.class,
        TestConfigurationToCountAllQueries.class
})
@AutoConfigureMockMvc
public class DeliveryControllerIT extends AbstractWebITest {

    private static final double AUTOVALIDATED_ADDRESS_LATITUDE = 50.51;
    private static final double AUTOVALIDATED_ADDRESS_LONGITUDE = 30.79;
    private static final String AUTOVALIDATED_ADDRESS = "Бровари, Марії Лагунової, 11";

    private static final double OUT_OF_REGION_ADDRESS_LATITUDE = 50.25;
    private static final double OUT_OF_REGION_ADDRESS_LONGITUDE = 28.69;
    private static final String OUT_OF_REGION_ADDRESS = "м. Житомир, вул Корольова, 1";

    private static final double NOVA_POSHTA_ADDRESS_LATITUDE = 50.0823797;
    private static final double NOVA_POSHTA_ADDRESS_LONGITUDE = 29.9285037;
    private static final String NOVA_POSHTA_ADDRESS = "вулиця Київська, 34, Фастів, Київська обл., Україна, 08500";

    private static final String FORMATTED_ADDRESS = "вулиця Марії Лагунової, 11, Бровари, Київська обл., Україна, 07400";
    private static final String UNKNOWN_ADDRESS = "вул. Невідома 8667";
    private static final String KNOWN_ADDRESS = "м.Львів, вулиця Незалежності 105";

    private static OrderDto orderWithAutovalidatedAddress;
    private static OrderDto orderWithUnknownAddress;
    private static OrderDto orderWithKnownAddress;
    private static OrderDto orderWithOutOfRegionAddress;
    private static OrderDto orderWithSpecialComment;

    @MockBean
    private IntegrationService integrationService;
    @MockBean
    private GoogleClient googleClient;
    @MockBean
    private EventService eventService;

    @Autowired
    private DeliveryRepository deliveryRepository;

    @Autowired
    private OrderExternalRepository orderExternalRepository;

    @BeforeAll
    public static void setUp() {

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
                .shippingDate(LocalDate.parse("2022-02-17"))
                .clientName("Домашня випічка")
                .address(AUTOVALIDATED_ADDRESS)
                .comment("бн")
                .managerFullName("Балашова Лариса")
                .products(List.of(productFirst))
                .orderWeight(12.00)
                .deliveryType(DeliveryType.REGULAR)
                .excluded(false)
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
                .shippingDate(LocalDate.parse("2022-02-17"))
                .clientName("ТОВ Пекарня")
                .address(UNKNOWN_ADDRESS)
                .comment("бн")
                .managerFullName("Шульженко Олег")
                .products(List.of(productSecond))
                .orderWeight(20.00)
                .deliveryType(DeliveryType.REGULAR)
                .excluded(false)
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
                .shippingDate(LocalDate.parse("2022-02-17"))
                .clientName("Фоззі-Фуд")
                .address(KNOWN_ADDRESS)
                .comment("бн")
                .managerFullName("Кравченко Сергій")
                .products(List.of(productThird))
                .orderWeight(10.00)
                .deliveryType(DeliveryType.REGULAR)
                .excluded(false)
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
                .shippingDate(LocalDate.parse("2022-02-17"))
                .clientName("Тераса ТОВ (Іль Моліно)")
                .address(OUT_OF_REGION_ADDRESS)
                .comment("бн")
                .managerFullName("Кравченко Сергій")
                .products(List.of(productFourth))
                .orderWeight(1.00)
                .deliveryType(DeliveryType.REGULAR)
                .excluded(false)
                .build();

        orderWithSpecialComment = OrderDto.builder()
                .orderNumber("00000002130")
                .refKey("05grande-0000-0000-0000-000000000000")
                .shippingDate(LocalDate.parse("2022-02-17"))
                .clientName("Домашня випічка")
                .address(AUTOVALIDATED_ADDRESS)
                .comment("доставка: Нова пошта, заморозка: доставка в холодильнику")
                .managerFullName("Балашова Лариса")
                .isFrozen(false)
                .products(List.of(productFirst))
                .orderWeight(12.00)
                .deliveryType(DeliveryType.POSTAL)
                .excluded(false)
                .build();
    }

    // Query count is not applicable in thi test due to Async thread
    @Test
    @DataSet("datasets/delivery/add_delivery_fetch_orders.yml")
    public void addDelivery() throws Exception {

        // Given
        mockIntegrationService();
        mockGoogleGeocodeService();

        DeliveryDto deliveryDto = DeliveryDto.builder()
                .deliveryDate(LocalDate.parse("2022-07-10"))
                .status(DeliveryStatus.DRAFT)
                .build();

        // When
        String addDeliveryResponse = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/deliveries")
                        .session(getLogistSession())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(deliveryDto)))
                .andExpect(status().isAccepted())
                .andReturn().getResponse().getContentAsString();
        Delivery addedDelivery = objectMapper.readValue(addDeliveryResponse, Delivery.class);

        // Then
        await()
                .atLeast(Durations.TWO_HUNDRED_MILLISECONDS)
                .atMost(Durations.TWO_SECONDS)
                .with()
                .pollInterval(Durations.TWO_HUNDRED_MILLISECONDS)
                .until(() -> deliveryRepository.findByIdWithOrders(addedDelivery.getId())
                        .map(Delivery::getOrders)
                        .map(List::size)
                        .map(size -> size == 6) // 5 Orders from 1C + 1 order rescheduled
                        .orElse(false));

        Mockito.verify(eventService, times(1))
                .publishDeliveryEvent(DeliveryHistoryTemplate.DELIVERY_CREATED, addedDelivery.getId());

        Mockito.verify(eventService, times(1))
                .publishOrdersStatus(DeliveryHistoryTemplate.ORDERS_LOADING, addedDelivery.getId());

        Mockito.verify(eventService, times(1))
                .publishOrdersStatus(DeliveryHistoryTemplate.ORDERS_LOADED, addedDelivery.getId());
    }

    private void mockIntegrationService() {
        when(integrationService.findAllByShippingDate(LocalDate.parse("2022-07-10")))
                .thenReturn(
                        List.of(orderWithAutovalidatedAddress,
                                orderWithUnknownAddress,
                                orderWithOutOfRegionAddress,
                                orderWithSpecialComment,
                                orderWithKnownAddress));
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
