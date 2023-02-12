package com.goodspartner.report;

import com.goodspartner.dto.Product;
import com.goodspartner.entity.Car;
import com.goodspartner.entity.CarLoad;
import com.goodspartner.entity.Delivery;
import com.goodspartner.entity.OrderExternal;
import com.goodspartner.exception.DeliveryNotFoundException;
import com.goodspartner.report.dto.CarLoadDetails;
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
public class CarsLoadReportGenerator implements ReportGenerator {
    private static final String TEMPLATE_PATH = "report/template_car_load_protocol.xlsx";
    private static final int MAIN_HEADER_ROW = 0;
    private static final int FIRST_SUB_HEADER_ROW = 2;
    private static final int FIRST_ITEM_ROW = 3;
    private static final int TEMPLATE_SHEET_NUMBER = 0;
    private static final String REPORT_NAME = "Завантаження_машин_на_";
    private final DeliveryRepository deliveryRepository;

    @SneakyThrows
    @Override
    @Transactional(readOnly = true)
    public ReportResult generateReport(UUID deliveryId) {
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new DeliveryNotFoundException(deliveryId));

        String reportName = ReportUtils.generateReportName(REPORT_NAME, delivery.getDeliveryDate());

        try (InputStream template = ReportUtils.getTemplate(TEMPLATE_PATH);
             ByteArrayOutputStream arrayStream = new ByteArrayOutputStream()) {
            XSSFWorkbook workbook = new XSSFWorkbook(template);

            List<CarLoad> carLoads = delivery.getCarLoads();
            List<CarLoadDetails> carLoadDetails = getCarLoadDetails(carLoads);

            cloneSheets(workbook, carLoadDetails);
            processDocument(workbook, carLoadDetails, delivery.getDeliveryDate());

            workbook.write(arrayStream);

            return new ReportResult(reportName, arrayStream.toByteArray());
        }
    }

    private List<CarLoadDetails> getCarLoadDetails(List<CarLoad> carLoads) {
        return carLoads.stream()
                .map(this::buildCarLoadDetails)
                .collect(Collectors.toList());
    }

    private CarLoadDetails buildCarLoadDetails(CarLoad carLoad) {
        return CarLoadDetails.builder()
                .car(carLoad.getCar())
                .carLoadProducts(getProductLoadDetailsMap(carLoad.getOrders()))
                .build();
    }

    private Map<String, List<ProductLoadDetails>> getProductLoadDetailsMap(List<OrderExternal> orders) {
        return orders.stream()
                .flatMap(order -> order.getProducts().stream()
                        .map(product -> getProductLoadDetails(product, order)))
                .collect(Collectors.groupingBy(productLoadDetails -> productLoadDetails.getProduct().getProductName()));
    }

    private ProductLoadDetails getProductLoadDetails(Product product, OrderExternal orderExternal) {
        return ProductLoadDetails.builder()
                .product(product)
                .clientName(orderExternal.getClientName())
                .clientAddress(orderExternal.getAddress())
                .build();
    }

    private void cloneSheets(XSSFWorkbook workbook, List<CarLoadDetails> carLoads) {
        for (CarLoadDetails carLoadDetails : carLoads) {
            workbook.cloneSheet(TEMPLATE_SHEET_NUMBER, getSheetName(carLoadDetails));
        }
        workbook.removeSheetAt(TEMPLATE_SHEET_NUMBER);
    }

    private void processDocument(XSSFWorkbook workbook, List<CarLoadDetails> carLoadDetails, LocalDate deliveryDate) {
        for (CarLoadDetails carLoadDetail : carLoadDetails) {
            XSSFSheet sheet = workbook.getSheet(getSheetName(carLoadDetail));
            Car car = carLoadDetail.getCar();
            sheet.getRow(MAIN_HEADER_ROW).getCell(0).setCellValue(getSheetHeaderValue(car, deliveryDate));
            processProductRows(sheet, carLoadDetail.getCarLoadProducts());
        }
    }

    private String getSheetName(CarLoadDetails carLoadDetails) {
        return carLoadDetails.getCar().getLicencePlate() + StringUtils.SPACE + carLoadDetails.getCar().getDriver().getUserName();
    }

    private String getSheetHeaderValue(Car car, LocalDate deliveryDate) {
        return "Протокол завантаження в автомобіль " +
                car.getName() +
                StringUtils.SPACE +
                car.getLicencePlate() +
                StringUtils.SPACE +
                "(водій " +
                car.getDriver().getUserName() +
                ") на " +
                deliveryDate;
    }

    private void processProductRows(XSSFSheet sheet, Map<String, List<ProductLoadDetails>> carLoadProducts) {
        int currentSubHeaderRow = FIRST_ITEM_ROW;
        for (Map.Entry<String, List<ProductLoadDetails>> stringListEntry : carLoadProducts.entrySet()) {
            currentSubHeaderRow++;
            Row productRow = sheet.createRow(currentSubHeaderRow);
            ReportUtils.copyCells(sheet.getRow(FIRST_SUB_HEADER_ROW), productRow);
            insertDataToProductRow(stringListEntry.getValue(), productRow);
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

    private void insertDataToProductRow(List<ProductLoadDetails> productLoadDetails, Row productRow) {
        for (int i = 0; i < productRow.getLastCellNum(); i++) {
            productRow.getCell(i).setCellValue(getProductCellValue(i, productLoadDetails));
        }
    }

    private String getProductCellValue(int cellNumber, List<ProductLoadDetails> productLoadDetails) {
        return switch (cellNumber) {
            case 0 -> productLoadDetails.get(0).getProduct().getProductName();
            case 1 -> String.valueOf(getTotalAmount(productLoadDetails));
            default -> getMeasureTotalWeight(productLoadDetails);

        };
    }

    private Integer getTotalAmount(List<ProductLoadDetails> productLoadDetails) {
        return productLoadDetails.stream()
                .map(productLoadDetail -> productLoadDetail.getProduct().getAmount())
                .reduce(0, Integer::sum);
    }

    private String getMeasureTotalWeight(List<ProductLoadDetails> productLoadDetails) {
        return getTotalWeight(productLoadDetails) + StringUtils.SPACE + productLoadDetails.get(0).getProduct().getMeasure();
    }

    private Double getTotalWeight(List<ProductLoadDetails> productLoadDetails) {
        return productLoadDetails.stream()
                .map(productLoadDetail -> productLoadDetail.getProduct().getTotalProductWeight())
                .reduce(0d, Double::sum);
    }

    private void insertDataToItemRow(ProductLoadDetails productLoadDetails, Row row) {
        for (int i = 0; i < row.getLastCellNum(); i++) {
            row.getCell(i).setCellValue(getProductItemCellValue(i, productLoadDetails));
        }
    }

    private String getProductItemCellValue(int cellNumber, ProductLoadDetails productLoadDetails) {
        return switch (cellNumber) {
            case 0 -> getClientDestinationPoint(productLoadDetails);
            case 1 -> String.valueOf(productLoadDetails.getProduct().getAmount());
            default -> getTotalProductWeight(productLoadDetails);
        };
    }

    private String getClientDestinationPoint(ProductLoadDetails productLoadDetails) {
        return productLoadDetails.getClientName() + StringUtils.SPACE + productLoadDetails.getClientAddress();
    }

    private String getTotalProductWeight(ProductLoadDetails productLoadDetails) {
        return productLoadDetails.getProduct().getTotalProductWeight() + StringUtils.SPACE + productLoadDetails.getProduct().getMeasure();
    }

    private void removeTemplateRow(XSSFSheet sheet) {
        sheet.shiftRows(FIRST_SUB_HEADER_ROW + 1, sheet.getLastRowNum(), -1);
    }
}