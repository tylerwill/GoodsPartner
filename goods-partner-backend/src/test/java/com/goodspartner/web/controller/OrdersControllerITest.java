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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.AdditionalMatchers;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import({TestSecurityDisableConfig.class})
@AutoConfigureMockMvc(addFilters = false)
@DBRider
class OrdersControllerITest extends AbstractWebITest {

    private static final double LATITUDE = 50.51;
    private static final double LONGITUDE = 30.79;
    private static final String FORMATTED_ADDRESS = "Marii Lahunovoi St, 11, Brovary, Kyivs'ka oblast, Ukraine, 07400";

    @MockBean
    private IntegrationService integrationService;

    @MockBean
    private GoogleClient googleClient;

    @Test
    @DisplayName("given OrderDto when Calculate Orders then Json Returned")
    void givenOrderDto_whenCalculateOrders_JsonReturned() throws Exception {

        Product productFirst = Product.builder()
                .amount(1)
                .storeName("Склад №1")
                .unitWeight(12.0)
                .productName("Наповнювач фруктово-ягідний (декоргель) (12 кг)")
                .totalProductWeight(12.0)
                .build();

        OrderDto orderDtoFirst = OrderDto.builder()
                .orderNumber("45678")
                .refKey("01grande-0000-0000-0000-000000000000")
                .createdDate(LocalDate.parse("2022-02-17"))
                .clientName("Домашня випічка")
                .address("Бровари, Марії Лагунової, 11")
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

        OrderDto orderDtoSecond = OrderDto.builder()
                .orderNumber("43532")
                .refKey("02grande-0000-0000-0000-000000000000")
                .createdDate(LocalDate.parse("2022-02-17"))
                .clientName("ТОВ Пекарня")
                .address("вул. Невідома 8667")
                .comment("бн")
                .managerFullName("Шульженко Олег")
                .products(List.of(productSecond))
                .orderWeight(20.00)
                .build();

        when(integrationService.findAllByShippingDate(LocalDate.parse("2022-07-10")))
                .thenReturn(List.of(orderDtoFirst, orderDtoSecond));

        mockGoogleGeocodeService(orderDtoFirst.getAddress());

        when(integrationService.calculateTotalOrdersWeight(List.of(orderDtoFirst, orderDtoSecond)))
                .thenReturn(32.00);

        mockMvc.perform(get("/api/v1/orders")
                .param("date", "2022-07-10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content()
                        .json(getResponseAsString("response/order-controller.json")));
    }

    @Test
    @DataSet(value = "common/orders_external/dataset_address_external.yml",
            cleanBefore = true,
            skipCleaningFor = {"flyway_schema_history"})
    @DisplayName("given Order Dto and saved Address when Calculate Orders then Json with Different Address Status Returned")
    void givenOrderDto_and_savedAddress_whenCalculateOrders_then_jsonWithDifferentAddressStatus_Returned() throws Exception {

        Product productFirst = Product.builder()
                .amount(1)
                .storeName("Склад №1")
                .unitWeight(12.0)
                .productName("Наповнювач фруктово-ягідний (декоргель) (12 кг)")
                .totalProductWeight(12.0)
                .build();

        OrderDto orderDtoFirst = OrderDto.builder()
                .orderNumber("45678")
                .refKey("01grande-0000-0000-0000-000000000000")
                .createdDate(LocalDate.parse("2022-02-17"))
                .clientName("Домашня випічка")
                .address("Бровари, Марії Лагунової, 11")
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

        OrderDto orderDtoSecond = OrderDto.builder()
                .orderNumber("43532")
                .refKey("02grande-0000-0000-0000-000000000000")
                .createdDate(LocalDate.parse("2022-02-17"))
                .clientName("ТОВ Пекарня")
                .address("вул. Невідома 8667")
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

        OrderDto orderDtoThird = OrderDto.builder()
                .orderNumber("45679")
                .refKey("03grande-0000-0000-0000-000000000000")
                .createdDate(LocalDate.parse("2022-02-17"))
                .clientName("Фоззі-Фуд")
                .address("м.Львів, вулиця Незалежності 105")
                .comment("бн")
                .managerFullName("Кравченко Сергій")
                .products(List.of(productThird))
                .orderWeight(10.00)
                .build();

        when(integrationService.findAllByShippingDate(LocalDate.parse("2022-07-10")))
                .thenReturn(List.of(orderDtoFirst, orderDtoSecond, orderDtoThird));

        mockGoogleGeocodeService(orderDtoFirst.getAddress());

        when(integrationService.calculateTotalOrdersWeight(List.of(orderDtoFirst, orderDtoSecond, orderDtoThird)))
                .thenReturn(42.00);

        mockMvc.perform(get("/api/v1/orders")
                .param("date", "2022-07-10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content()
                        .json(getResponseAsString("response/order-controller-with-known-address.json")));
    }

    @Test
    @DisplayName("given No Orders For Specified Date when Calculate Orders then Json With Empty Orders Field Returned")
    void givenNoOrdersForSpecifiedDate_whenCalculateOrders_thenJsonWithEmptyOrdersFieldReturned() throws Exception {
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

    private void mockGoogleGeocodeService(String parsableAddress) {
        LatLng latLngMock = new LatLng(LATITUDE, LONGITUDE);

        Geometry geometryMock = new Geometry();
        geometryMock.location = latLngMock;

        GeocodingResult geocodingResult = new GeocodingResult();
        geocodingResult.formattedAddress = FORMATTED_ADDRESS;
        geocodingResult.geometry = geometryMock;

        GeocodingResult[] mockedGeocodeResults = new GeocodingResult[]{geocodingResult};

        when(googleClient.getGeocodingResults(parsableAddress))
                .thenReturn(mockedGeocodeResults);

        when(googleClient.getGeocodingResults(AdditionalMatchers.not(eq(parsableAddress))))
                .thenReturn(new GeocodingResult[0]);
    }
}