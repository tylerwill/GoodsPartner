package com.goods.partner.util;

import com.goods.partner.entity.projection.StoreProjection;
import com.goods.partner.repository.StoreRepository;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class StoreGenerateReport {

    @Autowired
    private StoreRepository storeRepository;

    public void generateExcelFile(HttpServletResponse response, LocalDate date) throws IOException {

        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet orders = workbook.createSheet("orders_at_store");

        CellStyle headerStyle = workbook.createCellStyle();
        XSSFFont headerFont = workbook.createFont();
        headerFont.setFontHeight(14);
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);

        CellStyle tableStyle = workbook.createCellStyle();
        XSSFFont tableFont = workbook.createFont();
        tableFont.setFontHeight(12);
        tableStyle.setFont(tableFont);
        tableStyle.setAlignment(HorizontalAlignment.CENTER);
        tableStyle.setBorderBottom(BorderStyle.THIN);
        tableStyle.setBorderLeft(BorderStyle.THIN);
        tableStyle.setBorderRight(BorderStyle.THIN);
        tableStyle.setBorderTop(BorderStyle.THIN);
        tableStyle.setWrapText(true);

        XSSFRow header = orders.createRow(0);
        createCell(header, 1, "Список замовлень на " + date, headerStyle);
        orders.createRow(1);

        List<ExcelFormatDto> storesByDate = getStoresByDate(date);

        XSSFRow storeNumber = orders.createRow(2);
        createCell(storeNumber, 1, storesByDate.get(0).storeName, headerStyle);
        orders.createRow(3);

        XSSFRow tableHeader = orders.createRow(4);
        createCell(tableHeader, 0, "№", tableStyle);
        createCell(tableHeader, 1, "Замовлення", tableStyle);
        createCell(tableHeader, 2, "Вага, кг", tableStyle);

        int rowCount = 5;
        int itemNumber = 1;
        double totalWeight = 0;

        for (ExcelFormatDto excelFormatDto : storesByDate) {
            int columnCount = 0;

            Row row = orders.createRow(rowCount++);
            createCell(row, columnCount++, itemNumber++, tableStyle);
            createCell(row, columnCount++, excelFormatDto.getOrderNumber(), tableStyle);
            totalWeight += excelFormatDto.getTotalOrderWeight();
            createCell(row, columnCount++, excelFormatDto.getTotalOrderWeight(), tableStyle);
        }

        XSSFRow total = orders.createRow(rowCount);
        createCell(total, 1, "Разом:", tableStyle);
        createCell(total, 2, totalWeight, tableStyle);

        orders.autoSizeColumn(0);
        orders.autoSizeColumn(1);
        orders.autoSizeColumn(2);

        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();
        outputStream.close();

    }

    private void createCell(Row row, int columnCount, Object cellValue, CellStyle style) {
        Cell cell = row.createCell(columnCount);
        if (cellValue instanceof Integer) {
            cell.setCellValue((Integer) cellValue);
        } else if (cellValue instanceof Double) {
            cell.setCellValue((Double) cellValue);
        } else if (cellValue instanceof String) {
            cell.setCellValue((String) cellValue);
        }
        cell.setCellStyle(style);
    }

    private List<ExcelFormatDto> getStoresByDate(LocalDate date) {
        List<StoreProjection> storeProjections = storeRepository.groupStoresByOrders(date);
        List<ExcelFormatDto> excelFormatDtos = new ArrayList<>();
        for (StoreProjection storeProjection : storeProjections) {
            excelFormatDtos.add(ExcelFormatDto.builder()
                    .storeName(storeProjection.getStoreName())
                    .orderNumber(storeProjection.getOrderNumber())
                    .totalOrderWeight(storeProjection.getTotalOrderWeight())
                    .build()
            );
        }

        return excelFormatDtos;
    }

    @Getter
    @Builder
    private static class ExcelFormatDto {
        private String storeName;
        private int orderNumber;
        private double totalOrderWeight;
    }
}
