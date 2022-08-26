package com.goodspartner.service;

import com.goodspartner.AbstractBaseITest;
import com.goodspartner.service.dto.external.grandedolce.ODataOrderDto;
import com.goodspartner.service.dto.external.grandedolce.ODataProductDto;
import com.goodspartner.service.dto.external.grandedolce.ODataWrapperDto;
import com.goodspartner.util.ODataUrlBuilder;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Disabled
public class WebClientTest extends AbstractBaseITest {
    // Orders
    private static final String ORDER_ENTRY_SET_NAME = "Document_ЗаказПокупателя";
    private static final String ORDER_SELECT_FIELDS = "Ref_Key,Number,Date,АдресДоставки,Контрагент/Description,Ответственный/Code";
    private static final String ORDER_EXPAND_FIELDS = "Ответственный/ФизЛицо,Контрагент";
    // Product
    private static final String PRODUCT_ENTRY_SET_NAME = "Document_ЗаказПокупателя_Товары";
    private static final String PRODUCT_SELECT_FIELDS = "Ref_Key,Количество,КоличествоМест,Коэффициент,ЕдиницаИзмерения/Description,Номенклатура/Description";
    private static final String PRODUCT_EXPAND_FIELDS = "Номенклатура,ЕдиницаИзмерения";

    public static final String SERVER_ODATA_URL = "http://89.76.239.245:8080/test/odata/standard.odata/";

    private static final String DATE_FILTER = "ДатаОтгрузки eq datetime'2022-02-07T00:00:00'";
    private static final String PRODUCT_KEY_FILTER = "Ref_Key eq guid'b94c2d43-8291-11ec-b3ce-00155dd72305' " +
            "or Ref_Key eq guid'abaa9b64-84ca-11ec-b3ce-00155dd72305'";
    private static final String FORMAT = "json";
    private static final int ORDER_FETCH_LIMIT = 3;

    @Autowired
    private WebClient webClient;

    @Test
    public void testGetOrders() {
        URI uri = new ODataUrlBuilder()
                .baseUrl(SERVER_ODATA_URL)
                .appendEntitySetSegment(ORDER_ENTRY_SET_NAME)
                .filter(DATE_FILTER)
                .expand(ORDER_EXPAND_FIELDS)
                .select(ORDER_SELECT_FIELDS)
                .format(FORMAT)
                .top(ORDER_FETCH_LIMIT)
                .build();

        ODataWrapperDto<ODataOrderDto> wrappedOrders = webClient.get()
                .uri(uri)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ODataWrapperDto<ODataOrderDto>>() {
                })
                .block();

        assertEquals(3, wrappedOrders.getValue().size());
    }

    @Test
    public void testGetProducts() {
        URI uri = new ODataUrlBuilder()
                .baseUrl(SERVER_ODATA_URL)
                .appendEntitySetSegment(PRODUCT_ENTRY_SET_NAME)
                .filter(PRODUCT_KEY_FILTER)
                .expand(PRODUCT_EXPAND_FIELDS)
                .select(PRODUCT_SELECT_FIELDS)
                .format(FORMAT)
                .build();

        ODataWrapperDto<ODataProductDto> wrappedProducts = webClient.get()
                .uri(uri)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ODataWrapperDto<ODataProductDto>>() {
                })
                .block();

        assertEquals(5, wrappedProducts.getValue().size());
    }
}
