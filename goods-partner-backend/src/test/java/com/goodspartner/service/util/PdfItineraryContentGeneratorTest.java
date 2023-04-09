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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PdfItineraryContentGeneratorTest {
    @Test
    void name() {
        int expectedItineraryLength = 7054;

        ItineraryContentGenerator generator = new PdfItineraryContentGenerator(new DefaultHtmlAggregator());

        DocumentContent content = generator.getItineraryContent(createDocumentDto());
        String resultString = content.getDocument(DocumentPdfType.ITINERARY).iterator().next();

        assertTrue(resultString.contains("15.06.2021"));
        assertTrue(resultString.contains("client&amp;"));
        assertEquals(expectedItineraryLength, resultString.length());
    }

    private DocumentDto createDocumentDto() {
        DocumentDto documentDto = new DocumentDto();
        documentDto.setCarName("Mercedes");
        documentDto.setCarLicencePlate("AE2525CM");
        documentDto.setDriverName("Sidorov");
        documentDto.setOrderNumber("8585");
        documentDto.setDeliveryDate("2021-06-15");
        documentDto.setRouteSheets(List.of(new RouteSheet("client&", "address", LocalTime.now(), LocalTime.now(),
                        "comment", "1077", "333"),
                new RouteSheet("client&", "address", LocalTime.now(), LocalTime.now(),
                        "comment", "1077", "333")));

        return documentDto;
    }
}