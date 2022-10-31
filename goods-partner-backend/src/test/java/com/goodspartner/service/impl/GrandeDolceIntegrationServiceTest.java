package com.goodspartner.service.impl;

import com.goodspartner.configuration.properties.GrandeDolce1CProperties;
import com.goodspartner.mapper.ProductMapper;
import com.goodspartner.mapper.ProductMapperImpl;
import com.goodspartner.service.dto.external.grandedolce.ODataOrderDto;
import com.goodspartner.service.dto.external.grandedolce.ODataProductDto;
import com.goodspartner.service.dto.external.grandedolce.ODataWrapperDto;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

class GrandeDolceIntegrationServiceTest {

    private final GrandeDolce1CProperties properties = new GrandeDolce1CProperties();
    private final ProductMapper productMapper = new ProductMapperImpl();
    private final GrandeDolceIntegrationService orderService = new GrandeDolceIntegrationService(
            properties, null, null, productMapper, null);

    private final List<ODataOrderDto> orderList = List.of(
            ODataOrderDto.builder().refKey("ecdc9069-84f4-11ec-b3ce-00155dd72305").build(),
            ODataOrderDto.builder().refKey("5c7c3687-84f2-11ec-b3ce-00155dd72305").build()
    );

    private final List<ODataProductDto> products_oData_order_ecdc9069_84f4 = List.of(
            ODataProductDto.builder().measure("кг").totalProductWeight(4.1).build(),
            ODataProductDto.builder().measure("шт").totalProductWeight(1.0).build(),
            ODataProductDto.builder().measure("пак").totalProductWeight(0.0).build(),
            ODataProductDto.builder().measure("л").totalProductWeight(11.3).build()
    );
    private final List<ODataProductDto> products_Odata_order_5c7c3687_84f2 = List.of(
            ODataProductDto.builder().measure("кг").amount(4).coefficient(0.4).totalProductWeight(11.1).build()
    );

    private final Map<String, List<ODataProductDto>> productMap = Map.of(
            "ecdc9069-84f4-11ec-b3ce-00155dd72305", products_oData_order_ecdc9069_84f4,
            "5c7c3687-84f2-11ec-b3ce-00155dd72305", products_Odata_order_5c7c3687_84f2
    );


    @Test
    void testEnrichOrders() {
        orderService.enrichOrders(orderList, productMap, LocalDate.of(2000,2,2));

        ODataOrderDto order_5c7c3687_84f2 = orderList.stream().
                filter(orderDto -> "5c7c3687-84f2-11ec-b3ce-00155dd72305".equals(orderDto.getRefKey())).findFirst().get();
        ODataOrderDto order_ecdc9069_84f4 = orderList.stream().
                filter(orderDto -> "ecdc9069-84f4-11ec-b3ce-00155dd72305".equals(orderDto.getRefKey())).findFirst().get();

        assertNotNull(order_ecdc9069_84f4.getProducts());
        assertEquals(16.4, order_ecdc9069_84f4.getOrderWeight());

        assertNotNull(order_5c7c3687_84f2.getProducts());
        assertEquals(11.1, order_5c7c3687_84f2.getOrderWeight());

    }

    @Test
    void testMockGetOrders() {
        ODataWrapperDto<ODataOrderDto> orders = new ODataWrapperDto<>();
        orders.setValue(new ArrayList<>());

        ODataWrapperDto<ODataOrderDto> wrappedOrders = getWebClientMock(orders).get()
                .uri("getOrders")
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ODataWrapperDto<ODataOrderDto>>() {
                })
                .block();

        assertNotNull(wrappedOrders);
    }

    @Test
    void testMockGetProducts() {
        ODataWrapperDto<ODataProductDto> products = new ODataWrapperDto<>();
        products.setValue(new ArrayList<>());

        ODataWrapperDto<ODataProductDto> wrappedProducts = getWebClientMock(products).get()
                .uri("getProducts")
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ODataWrapperDto<ODataProductDto>>() {
                })
                .block();

        assertNotNull(wrappedProducts);
    }

    @Test
    void testGetTotalOrderWeight() {
        var totalOrderWeight = orderService.getTotalOrderWeight(products_oData_order_ecdc9069_84f4);

        assertEquals(16.4, totalOrderWeight);
    }

    @Test
    void testCreateProductsFilter() {
        var keyList = List.of(
                "ecdc9069-84f4-11ec-b3ce-00155dd72305",
                "5c7c3687-84f2-11ec-b3ce-00155dd72305",
                "e1759d35-84cd-11ec-b3ce-00155dd72305");
        var productsFilter = orderService.createProductsFilter(keyList);
        var expectedProductFilter =
                "Ref_Key eq guid'ecdc9069-84f4-11ec-b3ce-00155dd72305' or " +
                "Ref_Key eq guid'5c7c3687-84f2-11ec-b3ce-00155dd72305' or " +
                "Ref_Key eq guid'e1759d35-84cd-11ec-b3ce-00155dd72305'";

        assertEquals(expectedProductFilter, productsFilter);
    }

    @Test
    void testCreateRefKeyFilterRequest() {
        var filter = orderService.createRefKeyFilterRequest("ecdc9069-84f4-11ec-b3ce-00155dd72305");
        var expectedFilter = "Ref_Key eq guid'ecdc9069-84f4-11ec-b3ce-00155dd72305'";

        assertEquals(expectedFilter, filter);
    }

    @Disabled
    @Test
    void testCreateOrderByDateFilter() {
        var filter = orderService.createOrderByDateFilter("2022-02-04T00:00:00");
        var expectedFilter = "ДатаОтгрузки eq datetime'2022-02-04T00:00:00'";

        assertEquals(expectedFilter, filter);
    }

    private <T> WebClient getWebClientMock(T response) {
        var webClientMock = Mockito.mock(WebClient.class);
        var uriSpecMock = Mockito.mock(WebClient.RequestHeadersUriSpec.class);
        var headersSpecMock = Mockito.mock(WebClient.RequestHeadersSpec.class);
        var responseSpecMock = Mockito.mock(WebClient.ResponseSpec.class);
        var mono = Mockito.mock(Mono.class);

        when(webClientMock.get()).thenReturn(uriSpecMock);
        when(uriSpecMock.uri(ArgumentMatchers.<String>notNull())).thenReturn(headersSpecMock);
        when(headersSpecMock.retrieve()).thenReturn(responseSpecMock);
        when(responseSpecMock.bodyToMono(ArgumentMatchers.<ParameterizedTypeReference<?>>notNull())).thenReturn(mono);
        when(mono.block()).thenReturn(response);

        return webClientMock;
    }
}