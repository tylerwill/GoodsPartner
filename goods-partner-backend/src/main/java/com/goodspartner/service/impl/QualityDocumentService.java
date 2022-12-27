package com.goodspartner.service.impl;

import com.goodspartner.configuration.properties.ClientProperties;
import com.goodspartner.dto.InvoiceDto;
import com.goodspartner.dto.InvoiceProduct;
import com.goodspartner.exception.RoutePointNotFoundException;
import com.goodspartner.report.ReportResult;
import com.goodspartner.repository.RoutePointRepository;
import com.goodspartner.service.DocumentService;
import com.goodspartner.service.IntegrationService;
import com.goodspartner.service.util.DocumentCreator;
import com.goodspartner.util.ObjectConverterUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.zip.ZipOutputStream;

import static com.goodspartner.util.ObjectConverterUtil.getOrdersRefKeysFromRoutePointList;

@Slf4j
@Service("qualityDocumentService")
@RequiredArgsConstructor
public class QualityDocumentService implements DocumentService {
    private static final String ROOT_QUALITY_PATH = "C:\\Посвідчення якості";
    private static final String VALID_SEPARATOR = "/";
    private static final String INVALID_SEPARATOR = "\\";
    private static final String ENCODED_SPACE = "%20";
    private final IntegrationService integrationService;
    private final DocumentCreator documentCreator;
    private final RoutePointRepository routePointRepository;

    private final ClientProperties clientProperties;

    @Override
    @Transactional(readOnly = true)
    public void saveDocumentsByRouteId(ZipOutputStream zipOutputStream, Long routeId) {
        List<String> orderRefKeys = getOrdersRefKeysFromRoutePointList(routePointRepository.findByRouteId(routeId));
        saveQualityDocuments(zipOutputStream, orderRefKeys);
    }

    @Override
    @Transactional(readOnly = true)
    public void saveDocumentByRoutePointId(ZipOutputStream zipOutputStream, Long routePointId) {
        List<String> orderRefKeys = routePointRepository.findByIdWithOrders(routePointId)
                .map(ObjectConverterUtil::getOrdersRefKeysFromRoutePoint)
                .orElseThrow(() -> new RoutePointNotFoundException(routePointId));
        saveQualityDocuments(zipOutputStream, orderRefKeys);
    }

    private void saveQualityDocuments(ZipOutputStream zipOutputStream, List<String> orderRefKeys) {
        List<InvoiceDto> invoices = integrationService.getInvoicesByOrderRefKeys(orderRefKeys);
        invoices
                .forEach(invoiceDto -> invoiceDto.getProducts()
                        .forEach(this::setServerQualityUrl
                        )
                );
        List<ReportResult> reportResults = invoices.stream()
                .flatMap(invoice -> getReportResults(invoice.getNumber() + File.separator, invoice.getProducts()).stream())
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
        documentCreator.createZip(zipOutputStream, reportResults);
    }

    private void setServerQualityUrl(InvoiceProduct product) {
        if (StringUtils.hasText(product.getQualityUrl())) {
            product.setQualityUrl(getPreparedQualityUrl(product));
//            product.setQualityUrl("http://localhost:80/link/test-file.txt");
        }
    }

    private String getPreparedQualityUrl(InvoiceProduct product) {
        return product.getQualityUrl()
                .replace(ROOT_QUALITY_PATH, clientProperties.getClientServerURL() + clientProperties.getDocumentsUriPrefix())
                .replace(INVALID_SEPARATOR, VALID_SEPARATOR)
                .replace(org.apache.commons.lang3.StringUtils.SPACE, ENCODED_SPACE);
    }

    private List<ReportResult> getReportResults(String name, List<InvoiceProduct> products) {
        return products.stream()
                .map(product -> getReportResult(name + getFileName(product), product))
//                .map(product -> getReportResult(name + "test-file.txt", product))
                .collect(Collectors.toList());
    }

    private String getFileName(InvoiceProduct product) {
        return product.getProductName() + VALID_SEPARATOR + getDocumentName(product.getQualityUrl());
    }

    private String getDocumentName(String qualityUrl) {
        String[] splittedUrl = qualityUrl.split(VALID_SEPARATOR);
        return splittedUrl[splittedUrl.length - 1];
    }

    private ReportResult getReportResult(String name, InvoiceProduct product) {
        if (StringUtils.hasText(product.getQualityUrl())) {
            try (InputStream inputStream = new URL(product.getQualityUrl()).openStream()) {
                return new ReportResult(name, IOUtils.toByteArray(inputStream));
            } catch (Exception e) {
                log.error("Quality document {} does not exist", product.getQualityUrl());
            }
        }
        return null;
    }
}
