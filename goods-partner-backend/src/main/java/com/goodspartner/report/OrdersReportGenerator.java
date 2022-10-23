package com.goodspartner.report;

import com.goodspartner.dto.Product;
import com.goodspartner.entity.Delivery;
import com.goodspartner.entity.OrderExternal;
import com.goodspartner.exception.DeliveryNotFoundException;
import com.goodspartner.repository.DeliveryRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;
import java.util.UUID;

import static com.goodspartner.report.ReportUtils.getTemplate;

@Service
@RequiredArgsConstructor
public class OrdersReportGenerator implements ReportGenerator {

    private static final String TEMPLATE_PATH = "report/template_report_list_of_orders.xlsx";

    private static final String REPORT_NAME = "Замовлення_на_";
    private static final int FIRST_INSERTED_ROW = 5;

    private final DeliveryRepository deliveryRepository;

    @SneakyThrows
    @Override
    public ReportResult generateReport(UUID deliveryId) {
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new DeliveryNotFoundException(deliveryId));

        String reportName = ReportUtils.generateReportName(REPORT_NAME, delivery.getDeliveryDate());

        try (InputStream template = getTemplate(TEMPLATE_PATH);
             ByteArrayOutputStream arrayStream = new ByteArrayOutputStream()) {

            XSSFWorkbook workbook = new XSSFWorkbook(template);
            XSSFSheet sheet = workbook.getSheetAt(0);

            List<OrderExternal> orders = delivery.getOrders();
            if (orders.isEmpty()) {
                sheet.getRow(3).createCell(4).setCellValue("НА ОБРАНУ ДАТУ ЗАМОВЛЕНЬ НЕ ВИЯВЛЕНО");
                workbook.write(arrayStream);
                return new ReportResult(reportName, arrayStream.toByteArray());
            }

            int ordersQuantity = orders.size();
            Row templateProductRow = sheet.getRow(3);

            addRowsForOrders(sheet, templateProductRow, ordersQuantity);
            addRowsForProductsAndFill(sheet, delivery, templateProductRow);

            workbook.write(arrayStream);
            return new ReportResult(reportName, arrayStream.toByteArray());
        }
    }


    private void addRowsForProductsAndFill(XSSFSheet sheet, Delivery delivery, Row sourceProductRow) {

        int rowNumber = 3;
        int orderCount = 1;
        Row currentRow = sheet.getRow(rowNumber);

        for (OrderExternal order : delivery.getOrders()) {
            List<Product> products = order.getProducts();
            if (products.size() > 1) {
                sheet.shiftRows(rowNumber + 1, sheet.getLastRowNum(), products.size() - 1);
                sheet.addMergedRegion(new CellRangeAddress(rowNumber, rowNumber + products.size() - 1, 0, 0));
                sheet.addMergedRegion(new CellRangeAddress(rowNumber, rowNumber + products.size() - 1, 1, 1));
            }

            int productCount = 1;

            currentRow.getCell(0).setCellValue(orderCount++);
            currentRow.getCell(1).setCellValue(order.getOrderNumber());

            for (Product product : products) {
                if (currentRow == null) {
                    currentRow = sheet.createRow(rowNumber);
                    ReportUtils.copyCells(sourceProductRow, currentRow);
                }

                currentRow.getCell(2).setCellValue(productCount++);
                currentRow.getCell(3).setCellValue(product.getProductName());
                currentRow.getCell(4).setCellValue(product.getUnitWeight());
                currentRow.getCell(5).setCellValue(product.getAmount());
                currentRow.getCell(6).setCellValue(product.getTotalProductWeight());

                rowNumber++;
                currentRow = sheet.getRow(rowNumber);
            }

            currentRow = sheet.getRow(rowNumber);
            sheet.addMergedRegion(new CellRangeAddress(rowNumber, rowNumber, 4, 5));
            currentRow.getCell(4).setCellValue("Загальна вага замовлення");
            currentRow.getCell(6).setCellValue(order.getOrderWeight());

            rowNumber++;
            sheet.shiftRows(rowNumber, sheet.getLastRowNum(), 1);

            rowNumber++;
            currentRow = sheet.getRow(rowNumber);
        }

        double sum = delivery.getOrders()
                .stream()
                .mapToDouble(OrderExternal::getOrderWeight)
                .sum();

        currentRow.getCell(6).setCellValue(sum);

        rowNumber++;
        currentRow = sheet.getRow(rowNumber);
        String formattedDate = delivery.getDeliveryDate().format(DateTimeFormatter
                .ofLocalizedDate(FormatStyle.SHORT));
        currentRow.getCell(6).setCellValue(formattedDate);
    }

    private void addRowsForOrders(XSSFSheet sheet, Row sourceProductRow, int ordersQuantity) {
        if (ordersQuantity > 1) {
            sheet.shiftRows(FIRST_INSERTED_ROW, sheet.getLastRowNum(), (ordersQuantity - 1) * 2);

            for (int i = FIRST_INSERTED_ROW; i < FIRST_INSERTED_ROW + (ordersQuantity - 1) * 2; i++) {
                Row sourceWeightRow = sheet.getRow(FIRST_INSERTED_ROW - 1);
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