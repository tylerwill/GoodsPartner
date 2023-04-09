package com.goodspartner.service.document.impl;

import com.goodspartner.service.document.HtmlAggregator;
import com.goodspartner.service.document.ItineraryContentGenerator;
import com.goodspartner.service.dto.DocumentContent;
import com.goodspartner.service.dto.DocumentDto;
import com.goodspartner.service.dto.DocumentPdfType;
import com.goodspartner.service.dto.PdfDocumentContent;

import java.util.*;

public class PdfItineraryContentGenerator implements ItineraryContentGenerator {

    private static final String TEMPLATE_ITINERARY = "itinerary_template_new.html";
    private final HtmlAggregator aggregator;
    private final Map<DocumentPdfType, Set<String>> content = Map.of(DocumentPdfType.ITINERARY, new HashSet<>());

    public PdfItineraryContentGenerator(HtmlAggregator aggregator) {

        this.aggregator = aggregator;
    }

    @Override
    public DocumentContent getItineraryContent(DocumentDto documentDto) {
        requiredNotNull(documentDto);

        getHtmlItinerary(documentDto);

        return PdfDocumentContent.builder()
                .documents(content)
                .build();
    }

    private void getHtmlItinerary(DocumentDto documentDto) {
        String htmlItinerary = aggregator.getEnrichedHtml(documentDto, TEMPLATE_ITINERARY);
        content.get(DocumentPdfType.ITINERARY).add(htmlItinerary);
    }

    private void requiredNotNull(Object invoiceDto) {
        if (Objects.isNull(invoiceDto)) {
            throw new RuntimeException("The InvoiceDto cannot be null");
        }
    }
}
