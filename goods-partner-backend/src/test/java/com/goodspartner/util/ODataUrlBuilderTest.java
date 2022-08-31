package com.goodspartner.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.*;

class ODataUrlBuilderTest {
    private static final String ORDER_ENTRY_SET_NAME = "Document_ЗаказПокупателя";
    private static final String ORDER_SELECT_FIELDS = "Ref_Key,Number,Контрагент/Description";
    private static final String ORDER_EXPAND_FIELDS = "Контрагент";
    private static final String FORMAT = "JSON";
    private static final int ORDER_FETCH_LIMIT = 3;

    private static final String SERVER_ODATA_URL = "http://89.76.239.245:8080/";
    private static final String HOST_PREFIX = "test/odata/standard.odata/";

    private final ODataUrlBuilder ODataUrlBuilder = new ODataUrlBuilder();

    @Test
    @DisplayName("Test build url with all chunks (filter, expand, select, format, top)")
    void testURLBuilderWithAllChunks() {
        URI uri = ODataUrlBuilder
                .baseUrl(SERVER_ODATA_URL)
                .hostPrefix(HOST_PREFIX)
                .appendEntitySetSegment(ORDER_ENTRY_SET_NAME)
                .filter("Ref_Key eq guid'e5a1c733-57d4-11ea-b5ce-94de80dde3f4'")
                .expand(ORDER_EXPAND_FIELDS)
                .select(ORDER_SELECT_FIELDS)
                .format(FORMAT)
                .top(ORDER_FETCH_LIMIT)
                .build();
        String expectedUrl = "http://89.76.239.245:8080/test/odata/standard.odata/Document_ЗаказПокупателя?" +
                "%24filter=Ref_Key%20eq%20guid%27e5a1c733-57d4-11ea-b5ce-94de80dde3f4%27" +
                "&%24expand=%D0%9A%D0%BE%D0%BD%D1%82%D1%80%D0%B0%D0%B3%D0%B5%D0%BD%D1%82" +
                "&%24select=Ref_Key%2CNumber%2C%D0%9A%D0%BE%D0%BD%D1%82%D1%80%D0%B0%D0%B3%D0%B5%D0%BD%D1%82%2FDescription" +
                "&%24format=JSON&%24top=3";

        assertEquals(expectedUrl, uri.toString());
    }
}