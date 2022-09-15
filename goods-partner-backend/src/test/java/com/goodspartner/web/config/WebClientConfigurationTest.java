package com.goodspartner.web.config;

import com.goodspartner.AbstractBaseITest;
import com.goodspartner.config.TestSecurityDisableConfig;
import com.goodspartner.service.dto.external.grandedolce.ODataOrderDto;
import com.goodspartner.service.dto.external.grandedolce.ODataProductDto;
import com.goodspartner.service.dto.external.grandedolce.ODataWrapperDto;
import com.goodspartner.util.ODataUrlBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestInstance(PER_CLASS)
@Import({TestSecurityDisableConfig.class})
@AutoConfigureMockMvc(addFilters = false)
class WebClientConfigurationTest extends AbstractBaseITest {
    @LocalServerPort
    private int port;
    @Autowired
    private WebClient webClient;
    private static final String ORDER_ENTRY_SET_NAME = "Document_ЗаказПокупателя";
    private static final String ORDER_SELECT_FIELDS = "Ref_Key,Number,Date,АдресДоставки,Контрагент/Description,Ответственный/Code";
    private static final String ORDER_EXPAND_FIELDS = "Ответственный/ФизЛицо,Контрагент";
    // Product
    private static final String PRODUCT_ENTRY_SET_NAME = "Document_ЗаказПокупателя_Товары";
    private static final String PRODUCT_SELECT_FIELDS = "Ref_Key,Количество,КоличествоМест,Коэффициент,ЕдиницаИзмерения/Description,Номенклатура/Description";
    private static final String PRODUCT_EXPAND_FIELDS = "Номенклатура,ЕдиницаИзмерения";
    public static final String NONEXISTENT_SERVER_ODATA_URL = "http://SOME_1C_SERVICE:8080/test/odata/standard.odata/";
    private static final String DATE_FILTER = "ДатаОтгрузки eq datetime'2022-02-07T00:00:00'";
    private static final String PRODUCT_KEY_FILTER = "Ref_Key eq guid'b94c2d43-8291-11ec-b3ce-00155dd72305' " +
            "or Ref_Key eq guid'abaa9b64-84ca-11ec-b3ce-00155dd72305' " +
            "or Ref_Key eq guid'd5a56078-84cb-11ec-b3ce-00155dd72305' " +
            "or Ref_Key eq guid'214453ef-84ce-11ec-b3ce-00155dd72305' " +
            "or Ref_Key eq guid'f4c6de85-84f3-11ec-b3ce-00155dd72305' " +
            "or Ref_Key eq guid'fec71b02-84ea-11ec-b3ce-00155dd72305' " +
            "or Ref_Key eq guid'20a6101f-858a-11ec-b3ce-00155dd72305' " +
            "or Ref_Key eq guid'c78190c8-858a-11ec-b3ce-00155dd72305' " +
            "or Ref_Key eq guid'eaff2de1-84ce-11ec-b3ce-00155dd72305'";
    private static final String FORMAT = "json";
    private static final int ORDER_FETCH_LIMIT = 9;

    @BeforeAll
    public void setup() {
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        mockRequest.setLocalPort(port);
        RequestAttributes request = new ServletWebRequest(mockRequest);
        RequestContextHolder.setRequestAttributes(request);
    }

    @Test
    void testGetOrders() {
        URI uri = new ODataUrlBuilder()
                .baseUrl(NONEXISTENT_SERVER_ODATA_URL)
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

        assertEquals(9, wrappedOrders.getValue().size());
    }

    @Test
    void testGetProducts() {
        URI uri = new ODataUrlBuilder()
                .baseUrl(NONEXISTENT_SERVER_ODATA_URL)
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

        assertEquals(18, wrappedProducts.getValue().size());
    }
    @Test
    void test() {
        URI uri = new ODataUrlBuilder()
                .baseUrl("http://127.0.0.1:7777/test/odata/standard.odata/")
                .appendEntitySetSegment(ORDER_ENTRY_SET_NAME)
                .filter("2022-02-05")
                .expand(ORDER_EXPAND_FIELDS)
                .select(ORDER_SELECT_FIELDS)
                .format(FORMAT)
                .top(ORDER_FETCH_LIMIT)
                .build();

        assertThrows(WebClientRequestException.class, () -> webClient.get()
                .uri(uri)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ODataWrapperDto<ODataOrderDto>>() {
                })
                .block());
    }
}
