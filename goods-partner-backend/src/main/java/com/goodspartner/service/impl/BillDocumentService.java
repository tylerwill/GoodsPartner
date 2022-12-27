package com.goodspartner.service.impl;

import com.goodspartner.dto.InvoiceDto;
import com.goodspartner.dto.InvoiceProduct;
import com.goodspartner.exception.RoutePointNotFoundException;
import com.goodspartner.repository.RoutePointRepository;
import com.goodspartner.service.DocumentService;
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

@Service("billDocumentService")
@Slf4j
@RequiredArgsConstructor
public class BillDocumentService implements DocumentService {
    private static final Integer HEADER_SIZE = 27;
    private static final Integer HEADER_ROW_1 = 7;
    private static final Integer PLUS_ROWS = 2;
    private static final String BILL_NAME_PREFIX = "bill_";
    private static final String DOCUMENT_NAME_SEPARATOR = "_";
    private final IntegrationService integrationService;
    private final DocumentCreator documentCreator;
    private final RoutePointRepository routePointRepository;

    @Override
    @Transactional(readOnly = true)
    public void saveDocumentsByRouteId(ZipOutputStream zipOutputStream, Long routeId) {
        List<String> orderRefKeys = getOrdersRefKeysFromRoutePointList(routePointRepository.findByRouteId(routeId));
        saveBills(zipOutputStream, orderRefKeys);
    }

    @Override
    @Transactional(readOnly = true)
    public void saveDocumentByRoutePointId(ZipOutputStream zipOutputStream, Long routePointId) {
        List<String> orderRefKeys = routePointRepository.findByIdWithOrders(routePointId)
                .map(ObjectConverterUtil::getOrdersRefKeysFromRoutePoint)
                .orElseThrow(() -> new RoutePointNotFoundException(routePointId));
        saveBills(zipOutputStream, orderRefKeys);
    }

    private void saveBills(ZipOutputStream zipOutputStream, List<String> orderRefKeys) {
        List<InvoiceDto> invoiceByOrderRefKey = integrationService.getInvoicesByOrderRefKeys(orderRefKeys);
        List<PreparedDocumentData> preparedInvoicesData = getPreparedBillsData(invoiceByOrderRefKey);
        documentCreator.createDocuments(zipOutputStream, preparedInvoicesData, BILL_NAME_PREFIX, HEADER_SIZE + 1);

    }

    private List<PreparedDocumentData> getPreparedBillsData(List<InvoiceDto> invoiceDtos) {
        return invoiceDtos.stream()
                .map(invoiceDto -> PreparedDocumentData.builder()
                        .documentName(getDocumentName(invoiceDto))
                        .header(getBillHeader(invoiceDto))
                        .table(getBillTable(invoiceDto.getProducts()))
                        .signature(getBillSignature(invoiceDto))
                        .build())
                .collect(Collectors.toList());
    }

    private String getDocumentName(InvoiceDto invoiceDto) {
        return BILL_NAME_PREFIX
                + invoiceDto.getOrderNumber()
                + DOCUMENT_NAME_SEPARATOR
                + invoiceDto.getOrderDate();
    }

    private Map<Integer, List<Pair<Integer, DocumentCellInfoDto>>> getBillHeader(InvoiceDto invoiceDto) {
        Map<Integer, List<Pair<Integer, DocumentCellInfoDto>>> header = new HashMap<>();
        header.put(HEADER_ROW_1,
                List.of(
                        Pair.of(5,
                                DocumentCellInfoDto.builder()
                                        .value(invoiceDto.getEdrpouCode())
                                        .style(CellStyles.HEADER_BOLD)
                                        .build())
                ));
        header.put(HEADER_ROW_1 + 1,
                List.of(
                        Pair.of(21,
                                DocumentCellInfoDto.builder()
                                        .value(invoiceDto.getCompanyAccount())
                                        .style(CellStyles.HEADER_BOLD)
                                        .build())
                ));
        header.put(HEADER_ROW_1 + 3,
                List.of(
                        Pair.of(16,
                                DocumentCellInfoDto.builder()
                                        .value(invoiceDto.getMfoCode())
                                        .style(CellStyles.HEADER_BOLD)
                                        .build())
                ));
        header.put(HEADER_ROW_1 + 4,
                List.of(
                        Pair.of(1,
                                DocumentCellInfoDto.builder()
                                        .value(invoiceDto.getBankName())
                                        .style(CellStyles.HEADER_BOLD)
                                        .build())
                ));
        header.put(HEADER_ROW_1 + 9,
                List.of(
                        Pair.of(11,
                                DocumentCellInfoDto.builder()
                                        .value(invoiceDto.getOrderNumber())
                                        .style(CellStyles.MAIN_HEADER)
                                        .build()),
                        Pair.of(17,
                                DocumentCellInfoDto.builder()
                                        .value(invoiceDto.getOrderDate())
                                        .style(CellStyles.MAIN_HEADER)
                                        .build())
                ));
        header.put(HEADER_ROW_1 + 11,
                List.of(
                        Pair.of(6,
                                DocumentCellInfoDto.builder()
                                        .value(invoiceDto.getCompanyName())
                                        .style(CellStyles.HEADER_BOLD)
                                        .build())
                ));
        header.put(HEADER_ROW_1 + 12,
                List.of(
                        Pair.of(6,
                                DocumentCellInfoDto.builder()
                                        .value(invoiceDto.getCompanyInformation())
                                        .style(CellStyles.HEADER_ORGANISATION)
                                        .build())
                ));
        header.put(HEADER_ROW_1 + 14,
                List.of(
                        Pair.of(6,
                                DocumentCellInfoDto.builder()
                                        .value(invoiceDto.getClientName())
                                        .style(CellStyles.HEADER_BOLD)
                                        .build())
                ));
        header.put(HEADER_ROW_1 + 17,
                List.of(
                        Pair.of(6,
                                DocumentCellInfoDto.builder()
                                        .value(invoiceDto.getClientContract())
                                        .style(CellStyles.HEADER)
                                        .build())
                ));
        return header;
    }

    private Map<Integer, List<Pair<Integer, DocumentCellInfoDto>>> getBillTable(List<InvoiceProduct> products) {
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
                                .value(product.getProductName())
                                .style(CellStyles.BILL_TABLE_CELL_2)
                                .build()),
                Pair.of(20,
                        DocumentCellInfoDto.builder()
                                .value(product.getTotalProductWeight())
                                .style(CellStyles.BILL_TABLE_CELL_3)
                                .build()),
                Pair.of(23,
                        DocumentCellInfoDto.builder()
                                .value(product.getMeasure())
                                .style(CellStyles.BILL_TABLE_CELL_4)
                                .build()),
                Pair.of(25,
                        DocumentCellInfoDto.builder()
                                .value(product.getPrice())
                                .style(CellStyles.BILL_TABLE_CELL_5)
                                .build()),
                Pair.of(29,
                        DocumentCellInfoDto.builder()
                                .value(product.getPriceAmount())
                                .style(CellStyles.BILL_TABLE_CELL_6)
                                .build()
                ));
    }

    private Map<Integer, List<Pair<Integer, DocumentCellInfoDto>>> getBillSignature(InvoiceDto invoiceDto) {
        Map<Integer, List<Pair<Integer, DocumentCellInfoDto>>> signature = new HashMap<>();
        Integer tableRowsCount = invoiceDto.getProducts().size();
        int firstSignatureRow = HEADER_SIZE + tableRowsCount + PLUS_ROWS;
        signature.put(firstSignatureRow,
                List.of(
                        Pair.of(29,
                                DocumentCellInfoDto.builder()
                                        .value(String.valueOf(invoiceDto.getInvoiceAmount()))
                                        .style(CellStyles.AMOUNT_SIGNATURE)
                                        .build())));
        signature.put(firstSignatureRow + 1,
                List.of(
                        Pair.of(29,
                                DocumentCellInfoDto.builder()
                                        .value(String.valueOf(invoiceDto.getInvoiceAmountPDV()))
                                        .style(CellStyles.AMOUNT_SIGNATURE)
                                        .build())));
        signature.put(firstSignatureRow + 3,
                List.of(
                        Pair.of(7,
                                DocumentCellInfoDto.builder()
                                        .value(String.valueOf(tableRowsCount))
                                        .style(CellStyles.RESULT_SIGNATURE)
                                        .build()),
                        Pair.of(12,
                                DocumentCellInfoDto.builder()
                                        .value(String.valueOf(invoiceDto.getInvoiceAmount()))
                                        .style(CellStyles.RESULT_SIGNATURE)
                                        .build())));
        signature.put(firstSignatureRow + 4,
                List.of(
                        Pair.of(1,
                                DocumentCellInfoDto.builder()
                                        .value(invoiceDto.getTextNumeric())
                                        .style(CellStyles.WORD_AMOUNT_DEFINITION)
                                        .build())));
        return signature;
    }
}
