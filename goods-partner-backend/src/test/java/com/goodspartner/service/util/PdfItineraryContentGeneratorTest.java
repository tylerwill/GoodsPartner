package com.goodspartner.service.util;

import com.goodspartner.service.document.ItineraryContentGenerator;
import com.goodspartner.service.document.dto.RouteSheet;
import com.goodspartner.service.document.impl.DefaultHtmlAggregator;
import com.goodspartner.service.document.impl.PdfItineraryContentGenerator;
import com.goodspartner.service.dto.DocumentContent;
import com.goodspartner.service.dto.DocumentDto;
import com.goodspartner.service.dto.DocumentPdfType;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PdfItineraryContentGeneratorTest {
    @Test
    void name() {
        ItineraryContentGenerator generator = new PdfItineraryContentGenerator(new DefaultHtmlAggregator());

        DocumentContent content = generator.getItineraryContent(createDocumentDto());
        String resultString = content.getDocument(DocumentPdfType.ITINERARY).iterator().next();

        assertTrue(resultString.contains("15.06.2021"));
        assertTrue(resultString.contains("client1&amp;"));
        assertTrue(resultString.contains("Mercedes"));
        assertTrue(resultString.contains("AE2525CM"));
        assertTrue(resultString.contains("Sidorov"));
        assertFalse(resultString.contains("8585"));
        assertTrue(resultString.contains("address1"));
        assertTrue(resultString.contains("comment1"));
        assertTrue(resultString.contains("1077"));
        assertTrue(resultString.contains("333"));
        assertTrue(resultString.contains("client2&amp;"));
        assertTrue(resultString.contains("address2"));
        assertTrue(resultString.contains("comment2"));
        assertTrue(resultString.contains("555"));
        assertTrue(resultString.contains("777"));
    }

    private DocumentDto createDocumentDto() {
        DocumentDto documentDto = new DocumentDto();
        documentDto.setCarName("Mercedes");
        documentDto.setCarLicencePlate("AE2525CM");
        documentDto.setDriverName("Sidorov");
        documentDto.setOrderNumber("8585");
        documentDto.setDeliveryDate("2021-06-15");
        documentDto.setRouteSheets(List.of(new RouteSheet("client1&", "address1", LocalTime.now(), LocalTime.now(),
                        "comment1", "1077", "333"),
                new RouteSheet("client2&", "address2", LocalTime.now(), LocalTime.now(),
                        "comment2", "555", "777")));

        return documentDto;
    }
}