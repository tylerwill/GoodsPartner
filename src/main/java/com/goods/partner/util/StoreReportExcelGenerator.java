package com.goods.partner.util;

import com.goods.partner.entity.projection.StoreProjection;
import com.goods.partner.repository.StoreRepository;
import lombok.Builder;
import lombok.Getter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
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
public class StoreReportExcelGenerator {

    @Autowired
    private StoreRepository storeRepository;

    public void generateExcelFile(HttpServletResponse response, LocalDate date) throws IOException {

        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet orders = workbook.createSheet("orders_at_stores");

        CellStyle style = workbook.createCellStyle();

        XSSFFont font = workbook.createFont();
        font.setFontHeight(14);
        style.setFont(font);

        XSSFRow header = orders.createRow(0);
        createCell(header, 1, "Список замовлень на " + date, style);
        List<ExcelFormatDto> storesByDate = getStoresByDate(date);

        XSSFRow storeNumber = orders.createRow(1);
        createCell(storeNumber, 1, storesByDate.get(0).storeName, style);

        XSSFRow tableHeader = orders.createRow(2);
        createCell(tableHeader, 1, "Замовлення", style);
        createCell(tableHeader, 2, "Вага, кг", style);

        int rowCount = 3;
        int itemNumber = 1;
        double totalWeight = 0;

        for (ExcelFormatDto excelFormatDto : storesByDate) {
            int columnCount = 0;

            Row row = orders.createRow(rowCount++);
            createCell(row, columnCount++, itemNumber++, style);
            createCell(row, columnCount++, excelFormatDto.getOrderNumber(), style);
            totalWeight += excelFormatDto.getTotalOrderWeight();
            createCell(row, columnCount++, excelFormatDto.getTotalOrderWeight(), style);
        }

        XSSFRow total = orders.createRow(rowCount);
        createCell(total, 1, "Разом:", style);
        createCell(total, 2, totalWeight, style);

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
        private Double totalOrderWeight;
    }
}
