package com.goodspartner.report;

import com.goodspartner.web.controller.response.OrdersCalculation;
import com.goodspartner.dto.OrderDto;
import com.goodspartner.dto.ProductDto;
import com.goodspartner.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class OrdersReportGenerator {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
    private static final String TEMPLATE_PATH = "report/template_report_list_of_orders.xlsx";
    private static final int FIRST_INSERTED_ROW = 5;
    private final OrderService orderService;

    private static InputStream getTemplate() {
        return OrdersReportGenerator.class.getClassLoader().getResourceAsStream(TEMPLATE_PATH);
    }

    public void generateReport(LocalDate date, Consumer<ReportResult> reportResultConsumer) {
        ReportResult reportResult = generateReport(date);
        reportResultConsumer.accept(reportResult);
    }

    @SneakyThrows
    public ReportResult generateReport(LocalDate date) {
        OrdersCalculation ordersCalculation = orderService.calculateOrders(date);
        String currentTime = DATE_TIME_FORMATTER.format(LocalDateTime.now());
        String reportName = "[" + currentTime + "]Orders_" + date + ".xlsx";

        try (InputStream template = getTemplate();
             ByteArrayOutputStream arrayStream = new ByteArrayOutputStream()) {

            XSSFWorkbook workbook = new XSSFWorkbook(template);
            XSSFSheet sheet = workbook.getSheetAt(0);

            if (ordersCalculation.getOrders().isEmpty()) {
                sheet.getRow(3).createCell(4).setCellValue("НА ОБРАНУ ДАТУ ЗАМОВЛЕНЬ НЕ ВИЯВЛЕНО");
                workbook.write(arrayStream);
                return new ReportResult(reportName, arrayStream.toByteArray());
            }

            int ordersQuantity = ordersCalculation.getOrders().size();
            Row templateProductRow = sheet.getRow(3);

            addRowsForOrders(sheet, templateProductRow, ordersQuantity);
            addRowsForProductsAndFill(sheet, ordersCalculation, templateProductRow, date);

            workbook.write(arrayStream);
            return new ReportResult(reportName, arrayStream.toByteArray());
        }
    }

    private void addRowsForProductsAndFill(XSSFSheet sheet, OrdersCalculation ordersCalculation, Row sourceProductRow, LocalDate date) {
        int rowNumber = 3;
        int orderCount = 1;
        Row currentRow = sheet.getRow(rowNumber);

        for (OrderDto orderDto : ordersCalculation.getOrders()) {
            List<ProductDto> productDtos = orderDto.getProducts();
            if (productDtos.size() > 1) {
                sheet.shiftRows(rowNumber + 1, sheet.getLastRowNum(), productDtos.size() - 1);
                sheet.addMergedRegion(new CellRangeAddress(rowNumber, rowNumber + productDtos.size() - 1, 0, 0));
                sheet.addMergedRegion(new CellRangeAddress(rowNumber, rowNumber + productDtos.size() - 1, 1, 1));
            }

            int productCount = 1;

            currentRow.getCell(0).setCellValue(orderCount++);
            currentRow.getCell(1).setCellValue(orderDto.getOrderNumber());

            for (ProductDto productDto : productDtos) {
                if (currentRow == null) {
                    currentRow = sheet.createRow(rowNumber);
                    createCell(sourceProductRow, currentRow);
                }

                currentRow.getCell(2).setCellValue(productCount++);
                currentRow.getCell(3).setCellValue(productDto.getProductName());
                currentRow.getCell(4).setCellValue(productDto.getUnitWeight());
                currentRow.getCell(5).setCellValue(productDto.getAmount());
                currentRow.getCell(6).setCellValue(productDto.getTotalProductWeight());

                rowNumber++;
                currentRow = sheet.getRow(rowNumber);
            }

            currentRow = sheet.getRow(rowNumber);
            sheet.addMergedRegion(new CellRangeAddress(rowNumber, rowNumber, 4, 5));
            currentRow.getCell(4).setCellValue("Загальна вага замовлення");
            currentRow.getCell(6).setCellValue(orderDto.getOrderWeight());

            rowNumber++;
            sheet.shiftRows(rowNumber, sheet.getLastRowNum(), 1);

            rowNumber++;
            currentRow = sheet.getRow(rowNumber);
        }

        double sum = ordersCalculation.getOrders()
                .stream()
                .mapToDouble(OrderDto::getOrderWeight)
                .sum();

        currentRow.getCell(6).setCellValue(sum);

        rowNumber++;
        currentRow = sheet.getRow(rowNumber);
        String formattedDate = date.format(DateTimeFormatter
                .ofLocalizedDate(FormatStyle.SHORT));
        currentRow.getCell(6).setCellValue(formattedDate.toString());
    }

    private void addRowsForOrders(XSSFSheet sheet, Row sourceProductRow, int ordersQuantity) {
        if (ordersQuantity > 1) {
            sheet.shiftRows(FIRST_INSERTED_ROW, sheet.getLastRowNum(), (ordersQuantity - 1) * 2);

            for (int i = FIRST_INSERTED_ROW; i < FIRST_INSERTED_ROW + (ordersQuantity - 1) * 2; i++) {
                Row sourceWeightRow = sheet.getRow(FIRST_INSERTED_ROW - 1);
                Row newRow = sheet.createRow(i);
                if (i % 2 == 0) {
                    createCell(sourceWeightRow, newRow);
                } else {
                    createCell(sourceProductRow, newRow);
                }
            }
        }
    }

    private void createCell(Row sourceRow, Row destinationRow) {
        for (int i = 0; i < sourceRow.getLastCellNum(); i++) {
            Cell oldCell = sourceRow.getCell(i);
            Cell newCell = destinationRow.createCell(i);

            CellStyle newCellStyle = sourceRow.getSheet().getWorkbook().createCellStyle();
            newCellStyle.cloneStyleFrom(oldCell.getCellStyle());
            newCell.setCellStyle(newCellStyle);
        }
    }
}
