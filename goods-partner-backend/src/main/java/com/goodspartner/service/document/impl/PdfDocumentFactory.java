package com.goodspartner.service.document.impl;

import com.goodspartner.configuration.properties.ClientProperties;
import com.goodspartner.service.document.*;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class PdfDocumentFactory extends AbstractDocumentFactory {
    @Override
    public DocumentContentGenerator createDocumentContentGenerator(HtmlAggregator aggregator) {
        return new PdfDocumentContentGenerator(aggregator);
    }

    @Override
    public ItineraryContentGenerator createItineraryContentGenerator(HtmlAggregator aggregator) {
        return new PdfItineraryContentGenerator(aggregator);
    }

    @Override
    public HtmlAggregator createHtmlAggregator() {
        return new DefaultHtmlAggregator();
    }

    @Override
    public FileCompiler createFileCompiler(FileFetcher fileFetcher) {
        return new PdfFileCompiler(fileFetcher);
    }

    @Override
    public DataExtractor createDataExtractor() {
        return new DefaultDataExtractor();
    }

    @Override
    public FileFetcher createFileFetcher(ClientProperties clientProperties, WebClient webClient) {
        return new DefaultFileFetcher(clientProperties, webClient);
    }
}