package com.goodspartner.report;

import com.goodspartner.dto.CarDto;
import com.goodspartner.dto.OrderDto;
import com.goodspartner.dto.ProductDto;
import com.goodspartner.service.RouteService;
import com.goodspartner.web.controller.response.RoutesCalculation;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.List;

import static com.goodspartner.report.ReportUtils.copyCells;

@Service
@RequiredArgsConstructor
public class CarsLoadReportGenerator implements ReportGenerator {
    private static final String TEMPLATE_PATH = "report/template_report_of_cars_with_orders.xlsx";
    private static final String REPORT_NAME = "Замовлення_на_";
    private static final int FIRST_INSERTED_ROW = 4;
    private static final int LAST_INSERTED_ROW = 5;
    private final RouteService routeService;

    @SneakyThrows
    @Override
    public ReportResult generateReport(LocalDate date) {
        RoutesCalculation routesCalculation = routeService.calculateRoutes(date);
        String reportName = ReportUtils.generateReportName(REPORT_NAME, date);

        try (InputStream template = ReportUtils.getTemplate(TEMPLATE_PATH);
             ByteArrayOutputStream arrayStream = new ByteArrayOutputStream()) {

            XSSFWorkbook workbook = new XSSFWorkbook(template);
            XSSFSheet sheet = workbook.getSheetAt(0);

            Row rowHeader = sheet.getRow(0);
            rowHeader.getCell(1).setCellValue(date.toString());

            if (routesCalculation.getCarLoadDetails().isEmpty()) {
                sheet.getRow(7).createCell(3).setCellValue("НА ОБРАНУ ДАТУ ЗАВАНТАЖЕНЬ НЕМАЄ");
                workbook.write(arrayStream);
                return new ReportResult(reportName, arrayStream.toByteArray());
            }
            for (RoutesCalculation.CarLoadDto carLoadDetail : routesCalculation.getCarLoadDetails()) {
                int ordersQuantity = carLoadDetail.getOrders().size();

                Row templateProductRow = sheet.getRow(3);

                addRows(sheet, templateProductRow, ordersQuantity);
                addRowsForProductsAndFill(sheet, routesCalculation, templateProductRow);
                workbook.write(arrayStream);
            }
            return new ReportResult(reportName, arrayStream.toByteArray());
        }
    }

    private void addRowsForProductsAndFill(XSSFSheet sheet, RoutesCalculation routesCalculation, Row sourceProductRow) {
        int rowNumber = 3;
        int orderCount = 1;
        Row currentRow = sheet.getRow(rowNumber);

        for (RoutesCalculation.CarLoadDto carLoadDetail : routesCalculation.getCarLoadDetails()) {
            for (OrderDto orderDto : carLoadDetail.getOrders()) {
                List<ProductDto> productDtos = orderDto.getProducts();
                if (productDtos.size() > 1) {
                    sheet.shiftRows(rowNumber + 1, sheet.getLastRowNum(), productDtos.size() - 1);
                }
                CarDto carDto = carLoadDetail.getCar();

                currentRow.getCell(0).setCellValue(orderCount++);
                currentRow.getCell(1).setCellValue(carDto.getName());
                currentRow.getCell(2).setCellValue(carDto.getLicencePlate());
                currentRow.getCell(3).setCellValue(orderDto.getOrderNumber());

                for (ProductDto productDto : productDtos) {
                    if (currentRow == null) {
                        currentRow = sheet.createRow(rowNumber);
                        copyCells(sourceProductRow, currentRow);
                    }
                    currentRow.getCell(4).setCellValue(productDto.getProductName());
                    currentRow.getCell(5).setCellValue(productDto.getUnitWeight());
                    currentRow.getCell(6).setCellValue(productDto.getAmount());
                    currentRow.getCell(7).setCellValue(productDto.getTotalProductWeight());

                    rowNumber++;
                    currentRow = sheet.getRow(rowNumber);
                }
                currentRow = sheet.getRow(rowNumber);
                currentRow.getCell(6).setCellValue("Загальна вага замовлення");
                currentRow.getCell(7).setCellValue(orderDto.getOrderWeight());
                rowNumber++;
                currentRow = sheet.getRow(rowNumber);
            }
            double sum = carLoadDetail.getOrders()
                    .stream()
                    .mapToDouble(OrderDto::getOrderWeight)
                    .sum();

            currentRow.getCell(7).setCellValue(sum);
        }
    }

    private void addRows(XSSFSheet sheet, Row sourceProductRow, int ordersQuantity) {
        if (ordersQuantity > 1) {
            sheet.shiftRows(FIRST_INSERTED_ROW, LAST_INSERTED_ROW, (ordersQuantity - 1) * 2);

            for (int i = FIRST_INSERTED_ROW; i < FIRST_INSERTED_ROW + (ordersQuantity - 1) * 2; i++) {
                Row sourceWeightRow = sheet.getRow(FIRST_INSERTED_ROW + (ordersQuantity - 1) * 2);
                Row newRow = sheet.createRow(i);
                if (i % 2 == 0) {
                    ReportUtils.copyCells(sourceWeightRow, newRow);
                } else {
                    ReportUtils.copyCells(sourceProductRow, newRow);
                }
            }
        }
    }


}