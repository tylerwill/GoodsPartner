package com.goodspartner.service.impl;

import com.goodspartner.dto.OrderDto;
import com.goodspartner.dto.OsmGeocodingDto;
import com.goodspartner.dto.ProductDto;
import com.goodspartner.service.dto.OrderValidationDto;
import org.junit.jupiter.api.*;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

@TestInstance(PER_CLASS)
public class OsmOrderValidationServiceITest {

    private final String BASE_URL = "https://nominatim.openstreetmap.org/search?q=";
    private final String RESPONSE_FORMAT = "&format=geojson";
    private OsmOrderValidationService osmOrderValidationService;

    @BeforeAll
    public void setUp() {
        String address = "";
        WebClient webClient = WebClient.create(BASE_URL + address + RESPONSE_FORMAT);
        osmOrderValidationService = new OsmOrderValidationService(webClient);
    }

    @Test
    @DisplayName("given Valid Address when Validate Address then True Return")
    public void givenValidAddress_whenValidateAddress_thenTrueReturn() {

        Assertions.assertTrue(osmOrderValidationService.validateAddress("Київ, вул. Генерала Aлмазова 11"));
        Assertions.assertTrue(osmOrderValidationService.validateAddress("Київ Генерала Aлмазова 11"));
        Assertions.assertTrue(osmOrderValidationService.validateAddress("київ генерала алмазова 11"));
        Assertions.assertTrue(osmOrderValidationService.validateAddress("київ алмазова 11"));
    }

    @Test
    @DisplayName("given Invalid Address when Validate Address then False Return")
    public void givenInvalidAddress_whenValidateAddress_thenFalseReturn() {

        Assertions.assertFalse(osmOrderValidationService.validateAddress("Киїїв, вул. Генерала Aлмазова 11"));
        Assertions.assertFalse(osmOrderValidationService.validateAddress("Київ вул. Некоректна 11"));
        Assertions.assertFalse(osmOrderValidationService.validateAddress("Київ, Генерала Aлмазова 157861"));
        Assertions.assertFalse(osmOrderValidationService.validateAddress("Київ, вул. Генерала Aлмазова 11, тел. 0974899467"));
    }

    @Test
    @DisplayName("given Valid Address when Get OsmGeocodingDto then Not Null Dto Return")
    public void givenValidAddress_whenGetOsmGeocodingDto_thenNotNullDtoReturn() {
        String validAddress = "Київ, вул. Генерала Aлмазова 11";

        OsmGeocodingDto osmGeocodingDto = osmOrderValidationService.getOsmGeocodingDto(validAddress);
        Assertions.assertNotNull(osmGeocodingDto);
        Assertions.assertNotNull(osmGeocodingDto.getFeatures());
    }

    @Test
    @DisplayName("given Invalid Address when GetOsmGeocodingDto then Empty Fields of OsmGeocodingDto Return")
    public void givenInvalidAddress_whenGetOsmGeocodingDto_thenEmptyFields_ofOsmGeocodingDtoReturn() {
        String invalidAddress = "Киїїїв,вул. алмазова 567888 тел.097748904";

        OsmGeocodingDto osmGeocodingDto = osmOrderValidationService.getOsmGeocodingDto(invalidAddress);
        Assertions.assertNull(osmGeocodingDto.getLicence());
        Assertions.assertNull(osmGeocodingDto.getType());
        Assertions.assertEquals(0, osmGeocodingDto.getFeatures().length);
    }

    @Test
    @DisplayName("given OrderDtos when ValidateOrders then OrderValidationDto Return")
    public void givenOrderDtos_whenValidateOrders_thenOrderValidationDtoReturn() {
        ProductDto firstProductDto = ProductDto.builder()
                .amount(1)
                .storeName("Склад №1")
                .unitWeight(12.0)
                .productName("Наповнювач фруктово-ягідний (декоргель) (12 кг)")
                .totalProductWeight(12.0)
                .build();

        OrderDto firstOrderDto = OrderDto.builder()
                .orderNumber("45678")
                .createdDate(LocalDate.parse("2022-02-17"))
                .clientName("Домашня випічка")
                .address("Київ, вул. Генерала Aлмазова 11")
                .managerFullName("Балашова Лариса")
                .products(List.of(firstProductDto))
                .orderWeight(12.00)
                .validAddress(false)
                .build();
        ProductDto secondProductDto = ProductDto.builder()
                .amount(1)
                .storeName("Склад №2")
                .unitWeight(20.0)
                .productName("66784 Арахісова паста")
                .totalProductWeight(20.0)
                .build();

        OrderDto secondOrderDto = OrderDto.builder()
                .orderNumber("43532")
                .createdDate(LocalDate.parse("2022-02-17"))
                .clientName("ТОВ Пекарня")
                .address("Киїїїв,вул. алмазова 567888 тел.097748904")
                .managerFullName("Шульженко Олег")
                .products(List.of(secondProductDto))
                .orderWeight(20.00)
                .validAddress(false)
                .build();

        OrderValidationDto orderValidationDto = osmOrderValidationService.validateOrders(List.of(firstOrderDto, secondOrderDto));

        assertNotNull(orderValidationDto);
        assertEquals(List.of(secondOrderDto), orderValidationDto.getInvalidOrders());
        assertEquals(List.of(firstOrderDto), orderValidationDto.getValidOrders());
    }
}