package com.goodspartner.service.document.impl;

import com.goodspartner.dto.InvoiceDto;
import com.goodspartner.dto.InvoiceProduct;
import com.goodspartner.service.document.DocumentContentGenerator;
import com.goodspartner.service.document.HtmlAggregator;
import com.goodspartner.service.document.impl.DefaultHtmlAggregator;
import com.goodspartner.service.dto.DocumentContent;
import com.goodspartner.service.dto.DocumentPdfType;
import com.goodspartner.service.dto.PdfDocumentContent;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;
import java.util.Map;
import java.util.Objects;
import java.util.List;
import java.util.Arrays;
import java.util.HashSet;
import java.util.stream.Collectors;

@Slf4j
public class PdfDocumentContentGenerator implements DocumentContentGenerator {

    private static final int PDF = 0;
    private static final int JPG = 0;
    private static final int JPEG = 1;
    private static final int PNG = 2;
    private static final int BMP = 3;

    private static final String HTML_TEMPLATE_BILL = "bill_template_new.html";
    private static final String HTML_TEMPLATE_INVOICE = "invoice_template_new.html";
    private final Map<DocumentPdfType, Set<String>> documents = setUpHashMap();
    private final HtmlAggregator defaultHtmlAggregator;

    public PdfDocumentContentGenerator() {
        this(new DefaultHtmlAggregator());
    }

    public PdfDocumentContentGenerator(HtmlAggregator defaultHtmlAggregator) {
        this.defaultHtmlAggregator = defaultHtmlAggregator;
    }

    public DocumentContent getDocumentContent(InvoiceDto invoiceDto) {
        requiredNotNull(invoiceDto);

        getHtmlBill(invoiceDto);
        getHtmlInvoice(invoiceDto);
        getQualityDocumentLinks(invoiceDto);

        return PdfDocumentContent
                .builder()
                .documents(documents)
                .build();
    }

    private void requiredNotNull(InvoiceDto invoiceDto) {
        if (Objects.isNull(invoiceDto)) {
            throw new RuntimeException("The InvoiceDto cannot be null");
        }
    }

    private void getHtmlBill(InvoiceDto invoiceDto) {
        String bill = defaultHtmlAggregator.getEnrichedHtml(invoiceDto, HTML_TEMPLATE_BILL);
        documents.get(DocumentPdfType.BILL).add(bill);
    }

    private void getHtmlInvoice(InvoiceDto invoiceDto) {
        String invoice = defaultHtmlAggregator.getEnrichedHtml(invoiceDto, HTML_TEMPLATE_INVOICE);
        documents.get(DocumentPdfType.INVOICE).add(invoice);
    }

    private void getQualityDocumentLinks(InvoiceDto invoiceDto) {
        List<InvoiceProduct> products = invoiceDto.getProducts();

        products.forEach(product -> {

            if (!urlNullOrEmpty(product)) {
                setQualityDocumentLink(product);
            } else {
                loggUrlNotProvided(product, invoiceDto);
            }
        });
    }

    private boolean urlNullOrEmpty(InvoiceProduct product) {
        String url = getUrl(product);
        return Objects.isNull(url) || url.isEmpty();
    }

    private void setQualityDocumentLink(InvoiceProduct product) {
        String url = getUrl(product);
        if (isPdf(url)) {
            documents.get(DocumentPdfType.PDF).add(url);
        }
        if (isImage(url)) {
            documents.get(DocumentPdfType.IMAGES).add(url);
        }
    }

    private String getUrl(InvoiceProduct product) {
        return product.getQualityUrl();
    }

    private boolean isPdf(String url) {
        String[] extensions = DocumentPdfType.PDF.getExtensions();
        return url.endsWith(extensions[PDF]);
    }

    private boolean isImage(String url) {
        String[] extensions = DocumentPdfType.IMAGES.getExtensions();
        return url.endsWith(extensions[JPG]) || url.endsWith(extensions[JPEG]) || url.endsWith(extensions[PNG]) || url.endsWith(extensions[BMP]);
    }

    private void loggUrlNotProvided(InvoiceProduct product, InvoiceDto invoiceDto) {
        log.warn("Quality document of the product: {} within the Order #: {} was not provided",
                product.getProductName(), invoiceDto.getOrderNumber());
    }

    private Map<DocumentPdfType, Set<String>> setUpHashMap() {
        return Arrays.stream(DocumentPdfType.values()).collect(Collectors.toMap(value -> value, value -> new HashSet<>()));
    }
}
