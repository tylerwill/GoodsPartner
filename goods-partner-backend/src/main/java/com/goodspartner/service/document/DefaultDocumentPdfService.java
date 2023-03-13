package com.goodspartner.service.document;

import com.goodspartner.dto.InvoiceDto;
import com.goodspartner.exception.RoutePointNotFoundException;
import com.goodspartner.repository.RoutePointRepository;
import com.goodspartner.service.IntegrationService;
import com.goodspartner.service.dto.PdfDocumentDto;
import com.goodspartner.service.util.PdfDocumentDtoGenerator;
import com.goodspartner.service.util.PdfFileCompiler;
import com.goodspartner.util.ObjectConverterUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.OutputStream;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class DefaultDocumentPdfService implements DocumentPdfService {

    private final IntegrationService integrationService;
    private final RoutePointRepository routePointRepository;
    private final PdfDocumentDtoGenerator documentDtoGenerator;
    private final PdfFileCompiler pdfFileCompiler;

    @Override
    @Transactional(readOnly = true)
    public OutputStream getPdfDocumentsByRoute(Long routeId) {
        List<String> orderRefKeys = getRefKeysByRouteId(routeId);

        return getPdfDocuments(orderRefKeys);
    }

    @Override
    @Transactional(readOnly = true)
    public OutputStream getPdfDocumentsByRoutePoint(Long routePointId) {
        List<String> orderRefKeys = getRefKeysByRoutePointId(routePointId);

        return getPdfDocuments(orderRefKeys);
    }

    private List<String> getRefKeysByRouteId(Long routeId) {
        return ObjectConverterUtil
                .getOrdersRefKeysFromRoutePointList(routePointRepository.findByRouteId(routeId));
    }

    private List<String> getRefKeysByRoutePointId(Long routePointId) {
        return routePointRepository.findByIdWithOrders(routePointId)
                .map(ObjectConverterUtil::getOrdersRefKeysFromRoutePoint)
                .orElseThrow(() -> new RoutePointNotFoundException(routePointId));
    }

    private OutputStream getPdfDocuments(List<String> orderRefKeys) {
        List<InvoiceDto> invoicesByOrderRefKeys = integrationService.getInvoicesByOrderRefKeys(orderRefKeys);
        List<PdfDocumentDto> documentsDto = invoicesByOrderRefKeys.stream()
                .map(documentDtoGenerator::getPdfDocumentDto)
                .toList();

        return pdfFileCompiler.getInstance().getCompiledPdfFile(documentsDto);
    }
}
