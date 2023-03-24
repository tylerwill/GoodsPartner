package com.goodspartner.service.document;

import com.goodspartner.configuration.properties.ClientProperties;
import com.goodspartner.dto.InvoiceDto;
import com.goodspartner.entity.RoutePoint;
import com.goodspartner.exception.RoutePointNotFoundException;
import com.goodspartner.repository.RoutePointRepository;
import com.goodspartner.service.IntegrationService;
import com.goodspartner.service.dto.DocumentContent;
import com.goodspartner.service.dto.DocumentDto;
import com.goodspartner.util.ObjectConverterUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.OutputStream;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class DefaultDocumentPdfService implements DocumentPdfService {

    private final IntegrationService integrationService;
    private final RoutePointRepository routePointRepository;
    private final AbstractDocumentFactory documentFactory;
    private final ClientProperties clientProperties;
    private final WebClient webClient;

    @Override
    @Transactional(readOnly = true)
    public DocumentDto getPdfDocumentsByRoute(Long routeId) {
        List<RoutePoint> routePoints = routePointRepository.findByRouteId(routeId);

        return getDocumentDto(routePoints);
    }

    @Override
    @Transactional(readOnly = true)
    public DocumentDto getPdfDocumentsByRoutePoint(Long routePointId) {
        List<RoutePoint> routePoint = Collections.singletonList(routePointRepository.findByIdWithOrders(routePointId).orElseThrow(() -> new RoutePointNotFoundException(routePointId)));

        return getDocumentDto(routePoint);
    }

    private DocumentDto getDocumentDto(List<RoutePoint> routePoints) {
        List<String> refKeys = ObjectConverterUtil.getOrdersRefKeysFromRoutePointList(routePoints);
        List<InvoiceDto> invoicesDto = integrationService.getInvoicesByOrderRefKeys(refKeys);

        return createDocumentDto(routePoints, invoicesDto);
    }

    private DocumentDto createDocumentDto(List<RoutePoint> routePoints, List<InvoiceDto> invoicesDto) {
        DocumentDto documentDto = new DocumentDto();

        setCarNameAndLicencePlateToDocumentDto(routePoints, documentDto);
        setOrderAndDeliveryDateToDocumentDto(invoicesDto, documentDto);
        setPdfContentToDocumentDto(invoicesDto, documentDto);
        return documentDto;
    }

    private void setCarNameAndLicencePlateToDocumentDto(List<RoutePoint> routePoints, DocumentDto documentDto) {
        DataExtractor extractor = documentFactory.createDataExtractor();
        extractor.setRoutePoints(routePoints);

        String carName = extractor.extractCarName();
        String carLicencePlate = extractor.extractCarLicencePlate();

        documentDto.setCarName(carName);
        documentDto.setCarLicencePlate(carLicencePlate);
    }

    private void setOrderAndDeliveryDateToDocumentDto(List<InvoiceDto> invoicesDto, DocumentDto documentDto) {
        DataExtractor dataExtractor = documentFactory.createDataExtractor();
        dataExtractor.setInvoiceDtos(invoicesDto);
        String orderNumber = dataExtractor.extractOrderNumber();
        String deliveryDate = dataExtractor.extractDeliveryDate();
        documentDto.setOrderNumber(orderNumber);
        documentDto.setDeliveryDate(deliveryDate);
    }

    private void setPdfContentToDocumentDto(List<InvoiceDto> invoicesDto, DocumentDto documentDto) {
        FileFetcher fileFetcher = documentFactory.createFileFetcher(clientProperties, webClient);
        HtmlAggregator htmlAggregator = documentFactory.createHtmlAggregator();
        List<DocumentContent> documentContents = invoicesDto.stream()
                .map(invoiceDto -> getDocumentContent(htmlAggregator, invoiceDto))
                .toList();
        OutputStream pdfDocument = documentFactory.createFileCompiler(fileFetcher).getCompiledPdfFile(documentContents);
        documentDto.setDocumentContent(pdfDocument);
    }

    private DocumentContent getDocumentContent(HtmlAggregator aggregator, InvoiceDto invoiceDto) {
        DocumentContentGenerator contentGenerator = documentFactory.createDocumentContentGenerator(aggregator);
        return contentGenerator.getDocumentContent(invoiceDto);
    }
}
