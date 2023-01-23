package com.goodspartner.report;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ReportUtils {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");

    public static String reportGenerationTime() {
        return DATE_TIME_FORMATTER.format(LocalDateTime.now());
    }

    public static InputStream getTemplate(String templatePath) {
        return OrdersReportGenerator.class.getClassLoader().getResourceAsStream(templatePath);
    }

    public static String generateReportName(String name, LocalDate date) {
        return "[" + reportGenerationTime() + "]" + name + date + ".xlsx";
    }

    public static void copyCells(Row sourceRow, Row destinationRow) {
        for (int i = 0; i < sourceRow.getLastCellNum(); i++) {
            Cell oldCell = sourceRow.getCell(i);
            Cell newCell = destinationRow.createCell(i);

            CellStyle newCellStyle = sourceRow.getSheet().getWorkbook().createCellStyle();
            newCellStyle.cloneStyleFrom(oldCell.getCellStyle());
            newCell.setCellStyle(newCellStyle);
        }
    }
}
