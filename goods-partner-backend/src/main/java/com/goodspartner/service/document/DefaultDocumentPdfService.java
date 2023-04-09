package com.goodspartner.service.document;

import com.goodspartner.configuration.properties.ClientProperties;
import com.goodspartner.dto.InvoiceDto;
import com.goodspartner.entity.DeliveryType;
import com.goodspartner.entity.OrderExternal;
import com.goodspartner.entity.RoutePoint;
import com.goodspartner.exception.NoOrdersFoundForDelivery;
import com.goodspartner.exception.RoutePointNotFoundException;
import com.goodspartner.mapper.RoutePointMapper;
import com.goodspartner.repository.OrderExternalRepository;
import com.goodspartner.repository.RoutePointRepository;
import com.goodspartner.service.IntegrationService;
import com.goodspartner.service.document.dto.RouteSheet;
import com.goodspartner.service.document.mapper.RouteSheetMapper;
import com.goodspartner.service.dto.DocumentContent;
import com.goodspartner.service.dto.DocumentDto;
import com.goodspartner.util.ObjectConverterUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class DefaultDocumentPdfService implements DocumentPdfService {

    private final static int ITINERARY_FIRST = 0;
    private final IntegrationService integrationService;
    private final RoutePointRepository routePointRepository;
    private final OrderExternalRepository orderExternalRepository;
    private final AbstractDocumentFactory documentFactory;
    private final ClientProperties clientProperties;
    private final WebClient webClient;
    private final RoutePointMapper mapper;
    private final RouteSheetMapper routeSheetMapper;

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

    @Override
    @Transactional(readOnly = true)
    public DocumentDto getPdfDocumentsByDeliveryId(UUID deliveryId, DeliveryType deliveryType) {
        List<OrderExternal> orders = orderExternalRepository.findOrdersByDeliveryIdAndDeliveryType(deliveryId, deliveryType);
        if (orders.isEmpty()) {
            throw new NoOrdersFoundForDelivery(deliveryId, deliveryType);
        }
        return getDocumentDtoByOrderExternal(orders);
    }

    private DocumentDto getDocumentDtoByOrderExternal(List<OrderExternal> orders) {
        List<String> refKeys = ObjectConverterUtil.getOrdersRefKeysFromOrders(orders);
        List<InvoiceDto> invoicesDto = integrationService.getInvoicesByOrderRefKeys(refKeys);

        return createDocumentDtoByOrders(invoicesDto, orders);
    }

    private DocumentDto getDocumentDto(List<RoutePoint> routePoints) {
        List<String> refKeys = ObjectConverterUtil.getOrdersRefKeysFromRoutePointList(routePoints);
        List<InvoiceDto> invoicesDto = integrationService.getInvoicesByOrderRefKeys(refKeys);

        return createDocumentDto(routePoints, invoicesDto);
    }

    private DocumentDto createDocumentDtoByOrders(List<InvoiceDto> invoicesDto, List<OrderExternal> orders) {
        DocumentDto documentDto = new DocumentDto();

        setRouteSheetToDocumentDto(null, orders, invoicesDto, documentDto);
        setOrderAndDeliveryDateToDocumentDto(invoicesDto, documentDto);
        setPdfContentToDocumentDto(invoicesDto, documentDto);
        return documentDto;
    }

    private DocumentDto createDocumentDto(List<RoutePoint> routePoints, List<InvoiceDto> invoicesDto) {
        DocumentDto documentDto = new DocumentDto();

        setRouteSheetToDocumentDto(routePoints, new ArrayList<>(), invoicesDto, documentDto);
        setCarNameLicencePlateDriverNameToDocumentDto(routePoints, documentDto);
        setOrderAndDeliveryDateToDocumentDto(invoicesDto, documentDto);
        setPdfContentToDocumentDto(invoicesDto, documentDto);
        return documentDto;
    }

    private void setRouteSheetToDocumentDto(List<RoutePoint> routePoints, List<OrderExternal> orders, List<InvoiceDto> invoicesDto, DocumentDto documentDto) {
        Map<String, InvoiceDto> invoiceByOrderRefKey = mapInvoicesByOrderRefKey(invoicesDto);
        List<RouteSheet> routeSheets = Optional.ofNullable(routePoints)
                .map(routePointList -> getRouteSheetsByRoutePoints(routePointList, invoiceByOrderRefKey))
                .orElse(getRouteSheetsByOrders(orders, invoiceByOrderRefKey));
        documentDto.setRouteSheets(routeSheets);
    }

    private List<RouteSheet> getRouteSheetsByOrders(List<OrderExternal> orders, Map<String, InvoiceDto> invoiceByOrderRefKey) {
        return orders.stream()
                .map(order -> routeSheetMapper.map(order, invoiceByOrderRefKey.get(order.getRefKey())))
                .collect(Collectors.toList());
    }

    private List<RouteSheet> getRouteSheetsByRoutePoints(List<RoutePoint> routePoints, Map<String, InvoiceDto> invoiceByOrderRefKey) {
        return routePoints.stream()
                .flatMap(routePoint -> routePoint.getOrders().stream()
                        .map(order -> routeSheetMapper.map(routePoint, order, invoiceByOrderRefKey.get(order.getRefKey()))))
                .collect(Collectors.toList());
    }

    private Map<String, InvoiceDto> mapInvoicesByOrderRefKey(List<InvoiceDto> invoicesDto) {
        return invoicesDto.stream()
                .collect(Collectors.toMap(InvoiceDto::getOrderRefKey, Function.identity()));
    }

    private boolean isAnswerForRouteId(int CollectionSize) {
        return CollectionSize > 1;
    }

    private void setCarNameLicencePlateDriverNameToDocumentDto(List<RoutePoint> routePoints, DocumentDto documentDto) {
        DataExtractor extractor = documentFactory.createDataExtractor();
        extractor.setRoutePoints(routePoints);

        String carName = extractor.extractCarName();
        String carLicencePlate = extractor.extractCarLicencePlate();
        String driverName = extractor.extractDriverName();

        documentDto.setCarName(carName);
        documentDto.setCarLicencePlate(carLicencePlate);
        documentDto.setDriverName(driverName);
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

        List<DocumentContent> documentContents = resolveDocumentContent(invoicesDto, documentDto);

        String pdfDocument = documentFactory.createFileCompiler(fileFetcher).getCompiledPdfFile(documentContents);
        documentDto.setDocumentContent(pdfDocument);
    }

    private List<DocumentContent> resolveDocumentContent(List<InvoiceDto> invoicesDto, DocumentDto documentDto) {
        HtmlAggregator htmlAggregator = documentFactory.createHtmlAggregator();

        List<DocumentContent> documentContents = invoicesDto.stream()
                .map(invoiceDto -> getDocumentContent(htmlAggregator, invoiceDto))
                .collect(Collectors.toCollection(LinkedList::new));

        if (isAnswerForRouteId(invoicesDto.size())) {
            documentContents.add(ITINERARY_FIRST, getItinerary(htmlAggregator, documentDto));
        }

        return documentContents;
    }

    private DocumentContent getDocumentContent(HtmlAggregator aggregator, InvoiceDto invoiceDto) {
        DocumentContentGenerator contentGenerator = documentFactory.createDocumentContentGenerator(aggregator);
        return contentGenerator.getDocumentContent(invoiceDto);
    }

    private DocumentContent getItinerary(HtmlAggregator aggregator, DocumentDto documentDto) {
        ItineraryContentGenerator contentGenerator = documentFactory.createItineraryContentGenerator(aggregator);
        return contentGenerator.getItineraryContent(documentDto);
    }
}
