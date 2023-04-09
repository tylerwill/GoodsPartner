package com.goodspartner.service.document;

import com.goodspartner.configuration.properties.ClientProperties;
import org.springframework.web.reactive.function.client.WebClient;

public abstract class AbstractDocumentFactory {
    public abstract DocumentContentGenerator createDocumentContentGenerator(HtmlAggregator aggregator);
    public abstract ItineraryContentGenerator createItineraryContentGenerator(HtmlAggregator aggregator);
    public abstract FileCompiler createFileCompiler(FileFetcher connector);
    public abstract HtmlAggregator createHtmlAggregator();
    public abstract DataExtractor createDataExtractor();
    public abstract FileFetcher createFileFetcher(ClientProperties clientProperties, WebClient webClient);
}