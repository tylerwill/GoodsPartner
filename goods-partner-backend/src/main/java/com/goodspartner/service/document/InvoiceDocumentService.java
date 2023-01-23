package com.goodspartner.service.document;

import com.goodspartner.dto.InvoiceDto;
import com.goodspartner.dto.InvoiceProduct;
import com.goodspartner.exception.RoutePointNotFoundException;
import com.goodspartner.repository.RoutePointRepository;
import com.goodspartner.service.IntegrationService;
import com.goodspartner.service.dto.CellStyles;
import com.goodspartner.service.dto.DocumentCellInfoDto;
import com.goodspartner.service.dto.PreparedDocumentData;
import com.goodspartner.service.util.DocumentCreator;
import com.goodspartner.util.ObjectConverterUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.zip.ZipOutputStream;

import static com.goodspartner.util.ObjectConverterUtil.getOrdersRefKeysFromRoutePointList;

@Service("invoiceDocumentService")
@Slf4j
@RequiredArgsConstructor
public class InvoiceDocumentService implements DocumentService {
    private static final Integer HEADER_SIZE = 20;
    private static final Integer HEADER_ROW_1 = 2;
    private static final Integer PLUS_ROWS = 2;
    private static final String INVOICE_NAME_PREFIX = "invoice_";
    private static final String DOCUMENT_NAME_SEPARATOR = "_";
    private final IntegrationService integrationService;
    private final DocumentCreator documentCreator;
    private final RoutePointRepository routePointRepository;

    @Override
    @Transactional(readOnly = true)
    public void saveDocumentsByRouteId(ZipOutputStream zipOutputStream, Long routeId) {
        List<String> orderRefKeys = getOrdersRefKeysFromRoutePointList(routePointRepository.findByRouteId(routeId));
        saveInvoices(zipOutputStream, orderRefKeys);
    }

    @Override
    @Transactional(readOnly = true)
    public void saveDocumentByRoutePointId(ZipOutputStream zipOutputStream, Long routePointId) {
        List<String> orderRefKeys = routePointRepository.findByIdWithOrders(routePointId)
                .map(ObjectConverterUtil::getOrdersRefKeysFromRoutePoint)
                .orElseThrow(() -> new RoutePointNotFoundException(routePointId));
        saveInvoices(zipOutputStream, orderRefKeys);
    }

    private void saveInvoices(ZipOutputStream zipOutputStream, List<String> orderRefKeys) {
        List<InvoiceDto> invoiceByOrderRefKey = integrationService.getInvoicesByOrderRefKeys(orderRefKeys);
        List<PreparedDocumentData> preparedInvoicesData = getPreparedInvoicesData(invoiceByOrderRefKey);
        documentCreator.createDocuments(zipOutputStream, preparedInvoicesData, INVOICE_NAME_PREFIX, HEADER_SIZE + 1);
    }

    private List<PreparedDocumentData> getPreparedInvoicesData(List<InvoiceDto> invoiceDtos) {
        return invoiceDtos.stream()
                .map(invoiceDto -> PreparedDocumentData.builder()
                        .documentName(getDocumentName(invoiceDto))
                        .header(getInvoiceHeader(invoiceDto))
                        .table(getInvoiceTable(invoiceDto.getProducts()))
                        .signature(getInvoiceSignature(invoiceDto))
                        .build())
                .collect(Collectors.toList());
    }

    private String getDocumentName(InvoiceDto invoiceDto) {
        return INVOICE_NAME_PREFIX
                + invoiceDto.getNumber()
                + DOCUMENT_NAME_SEPARATOR
                + invoiceDto.getDocumentDate();
    }

    private Map<Integer, List<Pair<Integer, DocumentCellInfoDto>>> getInvoiceHeader(InvoiceDto invoiceDto) {
        Map<Integer, List<Pair<Integer, DocumentCellInfoDto>>> header = new HashMap<>();
        header.put(HEADER_ROW_1,
                List.of(
                        Pair.of(11,
                                DocumentCellInfoDto.builder()
                                        .value(invoiceDto.getNumber())
                                        .style(CellStyles.MAIN_HEADER)
                                        .build()),
                        Pair.of(17,
                                DocumentCellInfoDto.builder()
                                        .value(invoiceDto.getDocumentDate())
                                        .style(CellStyles.MAIN_HEADER)
                                        .build())
                ));
        header.put(HEADER_ROW_1 + 2,
                List.of(
                        Pair.of(6,
                                DocumentCellInfoDto.builder()
                                        .value(invoiceDto.getCompanyName())
                                        .style(CellStyles.HEADER_BOLD)
                                        .build())
                ));
        header.put(HEADER_ROW_1 + 3,
                List.of(
                        Pair.of(6,
                                DocumentCellInfoDto.builder()
                                        .value(invoiceDto.getCompanyInformation())
                                        .style(CellStyles.HEADER_ORGANISATION)
                                        .build())
                ));
        header.put(HEADER_ROW_1 + 5,
                List.of(
                        Pair.of(6,
                                DocumentCellInfoDto.builder()
                                        .value(invoiceDto.getClientName())
                                        .style(CellStyles.HEADER_BOLD)
                                        .build())
                ));
        header.put(HEADER_ROW_1 + 8,
                List.of(
                        Pair.of(6,
                                DocumentCellInfoDto.builder()
                                        .value(invoiceDto.getClientContract())
                                        .style(CellStyles.HEADER)
                                        .build())
                ));
        header.put(HEADER_ROW_1 + 10,
                List.of(
                        Pair.of(6,
                                DocumentCellInfoDto.builder()
                                        .value(invoiceDto.getOrderInfo())
                                        .style(CellStyles.HEADER)
                                        .build())
                ));
        header.put(HEADER_ROW_1 + 12,
                List.of(
                        Pair.of(6,
                                DocumentCellInfoDto.builder()
                                        .value(invoiceDto.getDeliveryAddress())
                                        .style(CellStyles.HEADER)
                                        .build())));
        header.put(HEADER_ROW_1 + 15,
                List.of(
                        Pair.of(6,
                                DocumentCellInfoDto.builder()
                                        .value(invoiceDto.getStoreAddress())
                                        .style(CellStyles.HEADER)
                                        .build())));
        return header;
    }

    private Map<Integer, List<Pair<Integer, DocumentCellInfoDto>>> getInvoiceTable(List<InvoiceProduct> products) {
        return products.stream().collect(Collectors.toMap(
                product -> Integer.parseInt(product.getLineNumber()) + HEADER_SIZE,
                this::getTableCellData));
    }

    private List<Pair<Integer, DocumentCellInfoDto>> getTableCellData(InvoiceProduct product) {
        return List.of(
                Pair.of(1,
                        DocumentCellInfoDto.builder()
                                .value(product.getLineNumber())
                                .style(CellStyles.TABLE_CELL_1)
                                .build()),
                Pair.of(3,
                        DocumentCellInfoDto.builder()
                                .value(product.getUktzedCode())
                                .style(CellStyles.TABLE_CELL_2)
                                .build()),
                Pair.of(6,
                        DocumentCellInfoDto.builder()
                                .value(product.getProductName())
                                .style(CellStyles.TABLE_CELL_3)
                                .build()),
                Pair.of(15,
                        DocumentCellInfoDto.builder()
                                .value(product.getTotalProductWeight())
                                .style(CellStyles.TABLE_CELL_4)
                                .build()),
                Pair.of(18,
                        DocumentCellInfoDto.builder()
                                .value(product.getMeasure())
                                .style(CellStyles.TABLE_CELL_5)
                                .build()),
                Pair.of(20,
                        DocumentCellInfoDto.builder()
                                .value(product.getPriceWithoutPDV())
                                .style(CellStyles.TABLE_CELL_6)
                                .build()),
                Pair.of(24,
                        DocumentCellInfoDto.builder()
                                .value(product.getAmountWithoutPDV())
                                .style(CellStyles.TABLE_CELL_7)
                                .build()),
                Pair.of(28,
                        DocumentCellInfoDto.builder()
                                .value(product.getPrice())
                                .style(CellStyles.TABLE_CELL_8)
                                .build()),
                Pair.of(32,
                        DocumentCellInfoDto.builder()
                                .value(product.getPriceAmount())
                                .style(CellStyles.TABLE_CELL_9)
                                .build()
                ));
    }

    private Map<Integer, List<Pair<Integer, DocumentCellInfoDto>>> getInvoiceSignature(InvoiceDto invoiceDto) {
        Map<Integer, List<Pair<Integer, DocumentCellInfoDto>>> signature = new HashMap<>();
        Integer tableRowsCount = invoiceDto.getProducts().size();
        int firstSignatureRow = HEADER_SIZE + tableRowsCount + PLUS_ROWS;
        signature.put(firstSignatureRow,
                List.of(
                        Pair.of(32,
                                DocumentCellInfoDto.builder()
                                        .value(String.valueOf(invoiceDto.getInvoiceAmountWithoutPDV()))
                                        .style(CellStyles.AMOUNT_SIGNATURE)
                                        .build())));
        signature.put(firstSignatureRow + 1,
                List.of(
                        Pair.of(32,
                                DocumentCellInfoDto.builder()
                                        .value(String.valueOf(invoiceDto.getInvoiceAmountPDV()))
                                        .style(CellStyles.AMOUNT_SIGNATURE)
                                        .build())));
        signature.put(firstSignatureRow + 2,
                List.of(
                        Pair.of(32,
                                DocumentCellInfoDto.builder()
                                        .value(String.valueOf(invoiceDto.getInvoiceAmount()))
                                        .style(CellStyles.AMOUNT_SIGNATURE)
                                        .build())));
        signature.put(firstSignatureRow + 4,
                List.of(
                        Pair.of(6,
                                DocumentCellInfoDto.builder()
                                        .value(String.valueOf(tableRowsCount))
                                        .style(CellStyles.RESULT_SIGNATURE)
                                        .build()),
                        Pair.of(10,
                                DocumentCellInfoDto.builder()
                                        .value(String.valueOf(invoiceDto.getInvoiceAmount()))
                                        .style(CellStyles.RESULT_SIGNATURE)
                                        .build())));
        signature.put(firstSignatureRow + 5,
                List.of(
                        Pair.of(1,
                                DocumentCellInfoDto.builder()
                                        .value(invoiceDto.getTextNumeric())
                                        .style(CellStyles.WORD_AMOUNT_DEFINITION)
                                        .build())));
        signature.put(firstSignatureRow + 10,
                List.of(
                        Pair.of(1,
                                DocumentCellInfoDto.builder()
                                        .value(invoiceDto.getManagerFullName())
                                        .style(CellStyles.RESPONSIBLE_SIGNATURE)
                                        .build())));
        return signature;
    }
}
