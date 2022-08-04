package com.goods.partner.util;

import com.goods.partner.dto.CalculationOrdersDto;
import com.goods.partner.dto.OrderDto;
import com.goods.partner.dto.ProductDto;
import com.goods.partner.service.impl.DefaultOrderService;
import com.google.common.io.Files;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
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

    public void generateExcelFile(LocalDate date) throws IOException {
        File sourceFile = new File("src/main/resources/report/template_report_list_of_orders.xlsx");
        File destinationFile = new File("src/main/resources/report/report_list_of_orders_" + date + ".xlsx");

        Files.copy(sourceFile, destinationFile);

//        try {
        FileInputStream fileInputStream = new FileInputStream(sourceFile);

        XSSFWorkbook workbook = new XSSFWorkbook(fileInputStream);

        Sheet sheet = workbook.getSheetAt(0);

        Row row = sheet.getRow(0);
        row.getCell(5).setCellValue(date.toString());

        CalculationOrdersDto calculationOrdersDto = defaultOrderService.calculateOrders(date);

        int ordersQuantity = calculationOrdersDto.getOrders().size();

        //shift rows
        Row sourceProductRow = sheet.getRow(3);
        Row sourceWeightRow;

        sheet.shiftRows(4, 5, (ordersQuantity - 1) * 2);

        //create new empty rows
        for (int i = 4; i < 4 + (ordersQuantity - 1) * 2; i++) {
            sourceWeightRow = sheet.getRow(4 + (ordersQuantity - 1) * 2);
            Row newRow = sheet.createRow(i);
            if (i % 2 == 0) {
                createCell(i, sourceWeightRow, newRow, workbook, sheet);
            } else {
                createCell(i, sourceProductRow, newRow, workbook, sheet);
            }
        }

        int rowNumber = 3;
        int orderCount = 0;
        Row currentRow = sheet.getRow(rowNumber);

        for (OrderDto orderDto : calculationOrdersDto.getOrders()) {

            List<ProductDto> productDtos = orderDto.getOrderData().getProducts();
            sheet.shiftRows(rowNumber + 1, sheet.getLastRowNum(), productDtos.size() - 1);

            int productCount = 0;

            currentRow.getCell(0).setCellValue(++orderCount);
            currentRow.getCell(1).setCellValue(orderDto.getOrderNumber());

            for (ProductDto productDto : productDtos) {
                if (currentRow == null){
                    currentRow = sheet.createRow(rowNumber);
                    createCell(rowNumber, sourceProductRow, currentRow, workbook, sheet);
                }
                currentRow.getCell(2).setCellValue(++productCount);
                currentRow.getCell(3).setCellValue(productDto.getProductName());
                currentRow.getCell(4).setCellValue(productDto.getUnitWeight());
                currentRow.getCell(5).setCellValue(productDto.getAmount());
                currentRow.getCell(6).setCellValue(productDto.getTotalProductWeight());

                rowNumber++;
                currentRow = sheet.getRow(rowNumber);
            }
            currentRow = sheet.getRow(rowNumber);
            currentRow.getCell(3).setCellValue("Загальна вага замовлення:");
            //sheet.addMergedRegion(new CellRangeAddress(1,1,0,5));
            currentRow.getCell(6).setCellValue(orderDto.getOrderData().getOrderWeight());
            rowNumber++;
            currentRow = sheet.getRow(rowNumber);
        }

        currentRow.getCell(6).setCellValue(calculationOrdersDto.getOrders()
                .stream()
                .mapToDouble(e -> e.getOrderData().getOrderWeight())
                .sum());

//        }

        fileInputStream.close();

        FileOutputStream fileOutputStream = new FileOutputStream(destinationFile);
        workbook.write(fileOutputStream);
        fileOutputStream.close();
    }

    private void createCell(int row, Row sourceRow, Row destinationRow, Workbook workbook, Sheet sheet) {
        for (int j = 0; j < sourceRow.getLastCellNum(); j++) {
            Cell oldCell = sourceRow.getCell(j);
            Cell newCell = destinationRow.createCell(j);

            CellStyle newCellStyle = workbook.createCellStyle();
            newCellStyle.cloneStyleFrom(oldCell.getCellStyle());
            newCell.setCellStyle(newCellStyle);

            newCell.setCellType(oldCell.getCellType());
        }
    }
}
