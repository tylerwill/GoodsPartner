package com.goodspartner.service.impl;

import com.goodspartner.dto.InvoiceDto;
import com.goodspartner.dto.InvoiceProduct;
import com.goodspartner.report.ReportResult;
import com.goodspartner.service.util.DocumentCreator;
import com.goodspartner.service.DocumentService;
import com.goodspartner.service.IntegrationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.zip.ZipOutputStream;

@Slf4j
@Service("qualityDocumentService")
@RequiredArgsConstructor
public class QualityDocumentService implements DocumentService {
    private static final String ROOT_QUALITY_PATH = "C:\\Посвідчення якості";
    private static final String SERVER_QUALITY_URL = "http://localhost:80/link";
    private final IntegrationService integrationService;
    private final DocumentCreator documentCreator;

    @Override
    public void saveDocumentsByOrderRefKeys(ZipOutputStream zipOutputStream, List<String> orderRefKeys) {
        List<InvoiceDto> invoices = integrationService.getInvoicesByOrderRefKeys(orderRefKeys);
        invoices
                .forEach(invoiceDto -> invoiceDto.getProducts()
                        .forEach(this::setServerQualityUrl
                        )
                );

        List<ReportResult> reportResults = invoices.stream()
                .flatMap(invoice -> getReportResults(invoice.getNumber() + File.separator, invoice.getProducts()).stream())
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        documentCreator.createZip(zipOutputStream, reportResults);
    }

    @Override
    public ReportResult saveDocumentByOrderRefKey(String orderRefKey) {
        return null;
    }

    private void setServerQualityUrl(InvoiceProduct product) {
        if (StringUtils.hasText(product.getQualityUrl())) {
            product.setQualityUrl(product.getQualityUrl().replace(ROOT_QUALITY_PATH, SERVER_QUALITY_URL));
//            product.setQualityUrl("http://localhost:80/link/test-file.txt");
        }
    }

    private List<ReportResult> getReportResults(String name, List<InvoiceProduct> products) {
        return products.stream()
                .map(product -> getReportResult(name + getFileName(product), product))
//                .map(product -> getReportResult(name + "test-file.txt", product))
                .collect(Collectors.toList());
    }

    private String getFileName(InvoiceProduct product) {
        return getDocumentName(product.getQualityUrl());
    }

    private String getDocumentName(String qualityUrl){
        String[] splittedUrl = qualityUrl.split(Pattern.quote(System.getProperty("file.separator")));
        return splittedUrl[splittedUrl.length - 1];
    }

    private ReportResult getReportResult(String name, InvoiceProduct product) {
        if (StringUtils.hasText(product.getQualityUrl())) {
            try (InputStream inputStream = new URL(product.getQualityUrl()).openStream()) {
                return new ReportResult(name, IOUtils.toByteArray(inputStream));
            } catch (Exception e){
                log.error("Quality document {} does not exist", product.getQualityUrl());
            }
        }
        return null;
    }
}
