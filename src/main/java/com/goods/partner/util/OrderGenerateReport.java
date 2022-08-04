package com.goods.partner.util;

import com.goods.partner.dto.CalculationOrdersDto;
import com.goods.partner.dto.OrderDto;
import com.goods.partner.dto.ProductDto;
import com.goods.partner.service.impl.DefaultOrderService;
import com.google.common.io.Files;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Component
@NoArgsConstructor
@AllArgsConstructor
public class OrderGenerateReport {

    @Autowired
    private DefaultOrderService defaultOrderService;

    private File sourceFile = new File("src/main/resources/report/template_report_list_of_orders.xlsx");
    private XSSFWorkbook workbook;
    private Sheet sheet;

    public void generateReport(LocalDate date) throws IOException {
        File destinationFile = new File("src/main/resources/report/report_list_of_orders_" + date + ".xlsx");
        Files.copy(sourceFile, destinationFile);

        try (FileInputStream fileInputStream = new FileInputStream(sourceFile);
             FileOutputStream fileOutputStream = new FileOutputStream(destinationFile);) {
            workbook = new XSSFWorkbook(fileInputStream);
            sheet = workbook.getSheetAt(0);

            Row rowHeader = sheet.getRow(0);
            rowHeader.getCell(5).setCellValue(date.toString());

            CalculationOrdersDto calculationOrdersDto = defaultOrderService.calculateOrders(date);
            if (calculationOrdersDto.getOrders().isEmpty()){
                sheet.getRow(1).createCell(3).setCellValue("НА ОБРАНУ ДАТУ ЗАМОВЛЕНЬ НЕ ВИЯВЛЕНО");
                workbook.write(fileOutputStream);
                return;
            }

            int ordersQuantity = calculationOrdersDto.getOrders().size();
            Row templateProductRow = sheet.getRow(3);

            addRowsForOrders(templateProductRow, ordersQuantity);
            addRowsForProductsAndFill(calculationOrdersDto, templateProductRow);

            workbook.write(fileOutputStream);
        }
    }

    private void addRowsForProductsAndFill(CalculationOrdersDto calculationOrdersDto, Row sourceProductRow) {
        int rowNumber = 3;
        int orderCount = 1;
        Row currentRow = sheet.getRow(rowNumber);

        for (OrderDto orderDto : calculationOrdersDto.getOrders()) {
            List<ProductDto> productDtos = orderDto.getOrderData().getProducts();
            sheet.shiftRows(rowNumber + 1, sheet.getLastRowNum(), productDtos.size() - 1);

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
            currentRow.getCell(3).setCellValue("Загальна вага замовлення:");
            currentRow.getCell(6).setCellValue(orderDto.getOrderData().getOrderWeight());
            rowNumber++;
            currentRow = sheet.getRow(rowNumber);
        }

        currentRow.getCell(6).setCellValue(calculationOrdersDto.getOrders()
                .stream()
                .mapToDouble(e -> e.getOrderData().getOrderWeight())
                .sum());
    }

    private void addRowsForOrders(Row sourceProductRow, int ordersQuantity) {
        sheet.shiftRows(4, 5, (ordersQuantity - 1) * 2);

        for (int i = 4; i < 4 + (ordersQuantity - 1) * 2; i++) {
            Row sourceWeightRow = sheet.getRow(4 + (ordersQuantity - 1) * 2);
            Row newRow = sheet.createRow(i);
            if (i % 2 == 0) {
                createCell(sourceWeightRow, newRow);
            } else {
                createCell(sourceProductRow, newRow);
            }
        }
    }

    private void createCell(Row sourceRow, Row destinationRow) {
        for (int i = 0; i < sourceRow.getLastCellNum(); i++) {
            Cell oldCell = sourceRow.getCell(i);
            Cell newCell = destinationRow.createCell(i);

            CellStyle newCellStyle = workbook.createCellStyle();
            newCellStyle.cloneStyleFrom(oldCell.getCellStyle());
            newCell.setCellStyle(newCellStyle);

            newCell.setCellType(oldCell.getCellType());
        }
    }
}
