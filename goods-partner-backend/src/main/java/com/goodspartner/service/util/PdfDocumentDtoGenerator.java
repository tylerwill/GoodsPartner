package com.goodspartner.service.util;

import com.goodspartner.dto.InvoiceDto;
import com.goodspartner.dto.InvoiceProduct;
import com.goodspartner.service.dto.DocumentPdfType;
import com.goodspartner.service.dto.PdfDocumentDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Component
public class PdfDocumentDtoGenerator {

    private static final int PDF = 0;
    private static final int JPG = 0;
    private static final int JPEG = 1;
    private static final int PNG = 2;
    private static final int BMP = 3;

    protected static final String HTML_TEMPLATE_BILL = "bill_template_new.html";
    protected static final String HTML_TEMPLATE_INVOICE = "invoice_template_new.html";
    private final HtmlAggregator htmlAggregator;

    public PdfDocumentDtoGenerator() {
        this(new HtmlAggregator());
    }

    PdfDocumentDtoGenerator(HtmlAggregator htmlAggregator) {
        this.htmlAggregator = htmlAggregator;
    }

    public PdfDocumentDto getPdfDocumentDto(InvoiceDto invoiceDto) {
        requiredNotNull(invoiceDto);

        String bill = getHtmlBill(invoiceDto);
        String invoice = getHtmlInvoice(invoiceDto);
        Map<DocumentPdfType, Set<String>> qualityDocuments = getQualityDocumentLinks(invoiceDto);

        return PdfDocumentDto
                .builder()
                .bill(bill)
                .invoice(invoice)
                .qualityDocuments(qualityDocuments)
                .build();
    }

    private void requiredNotNull(InvoiceDto invoiceDto) {
        if (Objects.isNull(invoiceDto)) {
            throw new RuntimeException("The InvoiceDto cannot be null");
        }
    }

    private String getHtmlBill(InvoiceDto invoiceDto) {
        return htmlAggregator.getEnrichedHtml(invoiceDto, HTML_TEMPLATE_BILL);
    }

    private String getHtmlInvoice(InvoiceDto invoiceDto) {
        return htmlAggregator.getEnrichedHtml(invoiceDto, HTML_TEMPLATE_INVOICE);
    }

    private Map<DocumentPdfType, Set<String>> getQualityDocumentLinks(InvoiceDto invoiceDto) {
        Map<DocumentPdfType, Set<String>> documentLinks = setUpHashMap();

        invoiceDto.getProducts().forEach(product -> {
            boolean isSet = setQualityDocumentLink(product, documentLinks);
            if (!isSet) {
                log.error("Quality document of the product: {} within the Order #: {} was not provided",
                        product.getProductName(), invoiceDto.getOrderNumber());
            }
        });

        return documentLinks;
    }

    private boolean setQualityDocumentLink(InvoiceProduct product, Map<DocumentPdfType, Set<String>> documentLinks) {
        String url = product.getQualityUrl();

        if (Objects.isNull(url) || url.isEmpty()) {
            return false;
        }
        if (isPdf(url)) {
            documentLinks.get(DocumentPdfType.PDF).add(url);
        }
        if (isImage(url)) {
            documentLinks.get(DocumentPdfType.IMAGES).add(url);
        }
        return true;
    }

    private Map<DocumentPdfType, Set<String>> setUpHashMap() {
        return Arrays.stream(DocumentPdfType.values()).collect(Collectors.toMap(value -> value, value -> new HashSet<>()));
    }

    private boolean isPdf(String url) {
        String[] extensions = DocumentPdfType.PDF.getExtensions();
        return url.endsWith(extensions[PDF]);
    }

    private boolean isImage(String url) {
        String[] extensions = DocumentPdfType.IMAGES.getExtensions();
        return url.endsWith(extensions[JPG]) || url.endsWith(extensions[JPEG]) || url.endsWith(extensions[PNG]) || url.endsWith(extensions[BMP]);
    }
}
