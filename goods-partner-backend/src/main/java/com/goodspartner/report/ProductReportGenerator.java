package com.goodspartner.report;

import com.goodspartner.dto.Product;
import com.goodspartner.entity.Delivery;
import com.goodspartner.entity.DeliveryType;
import com.goodspartner.entity.OrderExternal;
import com.goodspartner.exception.DeliveryNotFoundException;
import com.goodspartner.mapper.util.MapperUtil;
import com.goodspartner.report.dto.ProductLoadDetails;
import com.goodspartner.repository.DeliveryRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductReportGenerator implements ReportGenerator {
    private static final String REPORT_NAME = "Завантаження_машин_на_";
    private static final String TEMPLATE_PATH = "report/template_car_load_protocol.xlsx";
    private static final int MAIN_HEADER_ROW = 0;
    private static final int FIRST_ITEM_ROW = 3;
    private static final int FIRST_SUB_HEADER_ROW = 2;
    private static final String SEPARATOR = " -#- ";

    private final DeliveryRepository deliveryRepository;

    @SneakyThrows
    @Override
    @Transactional(readOnly = true)
    public ReportResult generateReport(UUID deliveryId, DeliveryType loadType) {
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new DeliveryNotFoundException(deliveryId));
        String reportName = ReportUtils.generateReportName(REPORT_NAME, delivery.getDeliveryDate());
        try (InputStream template = ReportUtils.getTemplate(TEMPLATE_PATH);
             ByteArrayOutputStream arrayStream = new ByteArrayOutputStream()) {
            XSSFWorkbook workbook = new XSSFWorkbook(template);

            List<OrderExternal> orderExternals = delivery.getOrders().stream()
                    .filter(order -> order.getDeliveryType().getName().equals(loadType.getName()))
                    .collect(Collectors.toList());
            Map<String, List<ProductLoadDetails>> loadDetailsMap = getProductLoadDetailsMap(orderExternals);
            processDocument(workbook, loadDetailsMap, delivery.getDeliveryDate(), loadType);

            workbook.write(arrayStream);
            return new ReportResult(reportName, arrayStream.toByteArray());
        }
    }

    private void processDocument(XSSFWorkbook workbook, Map<String, List<ProductLoadDetails>> loadDetailsMap, LocalDate deliveryDate, DeliveryType loadType) {
        XSSFSheet sheet = workbook.getSheetAt(0);
        sheet.getRow(MAIN_HEADER_ROW).getCell(0).setCellValue(getSheetHeaderValue(deliveryDate, loadType));
        if (loadDetailsMap.size() > 0) {
            processProductRows(sheet, loadDetailsMap);
        }

    }

    private void processProductRows(XSSFSheet sheet, Map<String, List<ProductLoadDetails>> carLoadProducts) {
        int currentSubHeaderRow = FIRST_ITEM_ROW;
        for (Map.Entry<String, List<ProductLoadDetails>> stringListEntry : carLoadProducts.entrySet()) {
            currentSubHeaderRow++;
            Row productRow = sheet.createRow(currentSubHeaderRow);
            ReportUtils.copyCells(sheet.getRow(FIRST_SUB_HEADER_ROW), productRow);
            productRow.getCell(0).setCellValue(stringListEntry.getKey());
            for (ProductLoadDetails productLoadDetails : stringListEntry.getValue()) {
                currentSubHeaderRow++;
                Row productItemRow = sheet.createRow(currentSubHeaderRow);
                ReportUtils.copyCells(sheet.getRow(FIRST_ITEM_ROW), productItemRow);
                insertDataToItemRow(productLoadDetails, productItemRow);
            }
        }
        removeTemplateRow(sheet);
        removeTemplateRow(sheet);
    }

    private void insertDataToItemRow(ProductLoadDetails productLoadDetails, Row row) {
        for (int i = 0; i < row.getLastCellNum(); i++) {
            row.getCell(i).setCellValue(getProductItemCellValue(i, productLoadDetails));
        }
    }

    private String getProductItemCellValue(int cellNumber, ProductLoadDetails productLoadDetails) {
        return switch (cellNumber) {
            case 0 -> getProductName(productLoadDetails);
            case 1 -> getPackagingAmountSignatureByAddress(productLoadDetails);
            default -> getUnitAmountSignatureByAddress(productLoadDetails);
        };
    }

    private String getProductName(ProductLoadDetails productLoadDetails) {
        return productLoadDetails.getProduct().getProductName();
    }

    public String getPackagingAmountSignatureByAddress(ProductLoadDetails productLoadDetails) {
        return MapperUtil.getRoundedDouble(productLoadDetails.getProduct().getProductPackaging().getAmount()) +
                StringUtils.SPACE +
                productLoadDetails.getProduct().getProductPackaging().getMeasureStandard();
    }

    private String getUnitAmountSignatureByAddress(ProductLoadDetails productLoadDetails) {
        return productLoadDetails.getProduct().getProductUnit().getAmount() +
                StringUtils.SPACE +
                productLoadDetails.getProduct().getProductUnit().getMeasureStandard();
    }

    private String getSheetHeaderValue(LocalDate deliveryDate, DeliveryType loadType) {
        return "Протокол завантаження на " +
                getTranslatedDeliveryType(loadType.getName()) +
                " станом на " +
                deliveryDate;
    }

    private String getTranslatedDeliveryType(String loadType) {
        return switch (loadType) {
            case "POSTAL" -> "нову пошту";
            case "PRE_PACKING" -> "фасовку";
            case "REGULAR" -> "регулярні відвантаження";
            default -> "самовивіз";
        };
    }

    private Map<String, List<ProductLoadDetails>> getProductLoadDetailsMap(List<OrderExternal> orders) {
        return orders.stream()
                .flatMap(order -> order.getProducts().stream()
                        .map(product -> getProductLoadDetails(product, order)))
                .collect(Collectors.groupingBy(this::getProductLoadDetailsMapKey));
    }

    private String getProductLoadDetailsMapKey(ProductLoadDetails productLoadDetails) {
        return productLoadDetails.getClientName() +
                SEPARATOR +
                productLoadDetails.getClientAddress() +
                SEPARATOR +
                productLoadDetails.getOrderNumber() +
                SEPARATOR +
                productLoadDetails.getComment();
    }

    private ProductLoadDetails getProductLoadDetails(Product product, OrderExternal orderExternal) {
        return ProductLoadDetails.builder()
                .product(product)
                .clientName(orderExternal.getClientName())
                .clientAddress(orderExternal.getAddress())
                .orderNumber(orderExternal.getOrderNumber())
                .comment(orderExternal.getComment())
                .build();
    }

    private void removeTemplateRow(XSSFSheet sheet) {
        sheet.shiftRows(FIRST_SUB_HEADER_ROW + 1, sheet.getLastRowNum(), -1);
    }
}
