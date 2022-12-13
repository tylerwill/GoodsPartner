package com.goodspartner.service.impl;

import com.goodspartner.service.AbstractCellStyleService;
import com.goodspartner.service.dto.CellStyles;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.stereotype.Service;

@Service
public class DefaultCellStyleService extends AbstractCellStyleService {
    @Override
    public CellStyle getCellStyle(CellStyles cellStyles, Workbook workbook, Sheet excelSheet, Row row) {
        return switch (cellStyles) {
            case AMOUNT_SIGNATURE -> getAmountSignatureCellStyle(workbook, excelSheet);
            case HEADER_BOLD -> getHeaderBoldCellStyle(workbook, excelSheet);
            case HEADER -> getHeaderCellStyle(workbook, excelSheet);
            case HEADER_ORGANISATION -> getHeaderOrganisationCellStyle(workbook, excelSheet);
            case MAIN_HEADER -> getMainHeaderCellStyle(workbook, excelSheet);
            case RESPONSIBLE_SIGNATURE -> getResponsibleSignatureCellStyle(workbook, excelSheet);
            case RESULT_SIGNATURE -> getResultSignatureCellStyle(workbook, excelSheet);
            case TABLE_CELL_1 -> getTableCell1CellStyle(workbook, excelSheet, row);
            case TABLE_CELL_2 -> getTableCell2CellStyle(workbook, excelSheet, row);
            case TABLE_CELL_3 -> getTableCell3CellStyle(workbook, excelSheet, row);
            case TABLE_CELL_4 -> getTableCell4CellStyle(workbook, excelSheet, row);
            case TABLE_CELL_5 -> getTableCell5CellStyle(workbook, excelSheet, row);
            case TABLE_CELL_6 -> getTableCell6CellStyle(workbook, excelSheet, row);
            case TABLE_CELL_7 -> getTableCell7CellStyle(workbook, excelSheet, row);
            case TABLE_CELL_8 -> getTableCell8CellStyle(workbook, excelSheet, row);
            case TABLE_CELL_9 -> getTableCell9CellStyle(workbook, excelSheet, row);
            case BILL_TABLE_CELL_2 -> getBillTableCell2CellStyle(workbook, excelSheet, row);
            case BILL_TABLE_CELL_3 -> getBillTableCell3CellStyle(workbook, excelSheet, row);
            case BILL_TABLE_CELL_4 -> getBillTableCell4CellStyle(workbook, excelSheet, row);
            case BILL_TABLE_CELL_5 -> getBillTableCell5CellStyle(workbook, excelSheet, row);
            case BILL_TABLE_CELL_6 -> getBillTableCell6CellStyle(workbook, excelSheet, row);
            default -> getWordAmountDefinitionCellStyle(workbook, excelSheet);
        };
    }

    private CellStyle getAmountSignatureCellStyle(Workbook workbook, Sheet excelSheet) {
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.RIGHT);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle.setBorderBottom(BorderStyle.MEDIUM);
        cellStyle.setBorderTop(BorderStyle.MEDIUM);
        cellStyle.setBorderLeft(BorderStyle.MEDIUM);

        cellStyle.setFont(getFont(workbook, ARIAL_FONT, (short) 9, true));

        return cellStyle;
    }

    private CellStyle getHeaderBoldCellStyle(Workbook workbook, Sheet excelSheet) {
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.LEFT);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        cellStyle.setFont(getFont(workbook, ARIAL_FONT, (short) 9, true));

        return cellStyle;
    }

    private CellStyle getHeaderCellStyle(Workbook workbook, Sheet excelSheet) {
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.LEFT);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        cellStyle.setFont(getFont(workbook, ARIAL_FONT, (short) 10, false));

        return cellStyle;
    }

    private CellStyle getHeaderOrganisationCellStyle(Workbook workbook, Sheet excelSheet) {
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.LEFT);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle.setWrapText(true);

        cellStyle.setFont(getFont(workbook, ARIAL_FONT, (short) 9, false));

        return cellStyle;
    }

    private CellStyle getMainHeaderCellStyle(Workbook workbook, Sheet excelSheet) {
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle.setBorderBottom(BorderStyle.MEDIUM);

        cellStyle.setFont(getFont(workbook, ARIAL_FONT, (short) 14, true));

        return cellStyle;
    }

    private CellStyle getResponsibleSignatureCellStyle(Workbook workbook, Sheet excelSheet) {
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.RIGHT);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        cellStyle.setFont(getFont(workbook, ARIAL_FONT, (short) 8, true));

        return cellStyle;
    }

    private CellStyle getResultSignatureCellStyle(Workbook workbook, Sheet excelSheet) {
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.RIGHT);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        cellStyle.setFont(getFont(workbook, ARIAL_FONT, (short) 8, false));

        return cellStyle;
    }

    private CellStyle getTableCell1CellStyle(Workbook workbook, Sheet excelSheet, Row row) {
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setVerticalAlignment(VerticalAlignment.TOP);
        cellStyle.setWrapText(true);

        CellRangeAddress region = new CellRangeAddress(row.getRowNum(), row.getRowNum(), 1, 2);
        excelSheet.addMergedRegion(region);

        row.setHeight((short) 800);

        setThinCellBorder(cellStyle);
        setThinCellRangeBorder(region, excelSheet);

        cellStyle.setFont(getFont(workbook, ARIAL_FONT, (short) 10, false));

        return cellStyle;
    }

    private CellStyle getTableCell2CellStyle(Workbook workbook, Sheet excelSheet, Row row) {
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.LEFT);
        cellStyle.setVerticalAlignment(VerticalAlignment.TOP);
        cellStyle.setWrapText(true);

        CellRangeAddress region = new CellRangeAddress(row.getRowNum(), row.getRowNum(), 3, 5);
        excelSheet.addMergedRegion(region);

        setThinCellBorder(cellStyle);
        setThinCellRangeBorder(region, excelSheet);

        cellStyle.setFont(getFont(workbook, ARIAL_FONT, (short) 10, false));

        return cellStyle;
    }

    private CellStyle getTableCell3CellStyle(Workbook workbook, Sheet excelSheet, Row row) {
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.LEFT);
        cellStyle.setVerticalAlignment(VerticalAlignment.TOP);
        cellStyle.setWrapText(true);

        CellRangeAddress region = new CellRangeAddress(row.getRowNum(), row.getRowNum(), 6, 14);
        excelSheet.addMergedRegion(region);

        setThinCellBorder(cellStyle);
        setThinCellRangeBorder(region, excelSheet);

        cellStyle.setFont(getFont(workbook, ARIAL_FONT, (short) 10, false));

        return cellStyle;
    }

    private CellStyle getBillTableCell2CellStyle(Workbook workbook, Sheet excelSheet, Row row) {
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.LEFT);
        cellStyle.setVerticalAlignment(VerticalAlignment.TOP);
        cellStyle.setWrapText(true);

        CellRangeAddress region = new CellRangeAddress(row.getRowNum(), row.getRowNum(), 3, 19);
        excelSheet.addMergedRegion(region);

        setThinCellBorder(cellStyle);
        setThinCellRangeBorder(region, excelSheet);

        cellStyle.setFont(getFont(workbook, ARIAL_FONT, (short) 10, false));

        return cellStyle;
    }

    private CellStyle getTableCell4CellStyle(Workbook workbook, Sheet excelSheet, Row row) {
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.RIGHT);
        cellStyle.setVerticalAlignment(VerticalAlignment.TOP);
        cellStyle.setWrapText(true);

        CellRangeAddress region = new CellRangeAddress(row.getRowNum(), row.getRowNum(), 15, 17);
        excelSheet.addMergedRegion(region);

        setThinCellBorder(cellStyle);
        setThinCellRangeBorder(region, excelSheet);

        cellStyle.setFont(getFont(workbook, ARIAL_FONT, (short) 10, false));

        return cellStyle;
    }

    private CellStyle getBillTableCell3CellStyle(Workbook workbook, Sheet excelSheet, Row row) {
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.RIGHT);
        cellStyle.setVerticalAlignment(VerticalAlignment.TOP);
        cellStyle.setWrapText(true);

        CellRangeAddress region = new CellRangeAddress(row.getRowNum(), row.getRowNum(), 20, 22);
        excelSheet.addMergedRegion(region);

        setThinCellBorder(cellStyle);
        setThinCellRangeBorder(region, excelSheet);

        cellStyle.setFont(getFont(workbook, ARIAL_FONT, (short) 10, false));

        return cellStyle;
    }

    private CellStyle getTableCell5CellStyle(Workbook workbook, Sheet excelSheet, Row row) {
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.LEFT);
        cellStyle.setVerticalAlignment(VerticalAlignment.TOP);
        cellStyle.setWrapText(true);

        CellRangeAddress region = new CellRangeAddress(row.getRowNum(), row.getRowNum(), 18, 19);
        excelSheet.addMergedRegion(region);

        setThinCellBorder(cellStyle);
        setThinCellRangeBorder(region, excelSheet);

        cellStyle.setFont(getFont(workbook, ARIAL_FONT, (short) 10, false));

        return cellStyle;
    }

    private CellStyle getBillTableCell4CellStyle(Workbook workbook, Sheet excelSheet, Row row) {
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.LEFT);
        cellStyle.setVerticalAlignment(VerticalAlignment.TOP);
        cellStyle.setWrapText(true);

        CellRangeAddress region = new CellRangeAddress(row.getRowNum(), row.getRowNum(), 23, 24);
        excelSheet.addMergedRegion(region);

        setThinCellBorder(cellStyle);
        setThinCellRangeBorder(region, excelSheet);

        cellStyle.setFont(getFont(workbook, ARIAL_FONT, (short) 10, false));

        return cellStyle;
    }

    private CellStyle getTableCell6CellStyle(Workbook workbook, Sheet excelSheet, Row row) {
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.RIGHT);
        cellStyle.setVerticalAlignment(VerticalAlignment.TOP);
        cellStyle.setWrapText(true);

        CellRangeAddress region = new CellRangeAddress(row.getRowNum(), row.getRowNum(), 20, 23);
        excelSheet.addMergedRegion(region);

        setThinCellBorder(cellStyle);
        setThinCellRangeBorder(region, excelSheet);

        cellStyle.setFont(getFont(workbook, ARIAL_FONT, (short) 10, false));

        return cellStyle;
    }

    private CellStyle getTableCell7CellStyle(Workbook workbook, Sheet excelSheet, Row row) {
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.RIGHT);
        cellStyle.setVerticalAlignment(VerticalAlignment.TOP);
        cellStyle.setWrapText(true);

        CellRangeAddress region = new CellRangeAddress(row.getRowNum(), row.getRowNum(), 24, 27);
        excelSheet.addMergedRegion(region);

        setThinCellBorder(cellStyle);
        setThinCellRangeBorder(region, excelSheet);

        cellStyle.setFont(getFont(workbook, ARIAL_FONT, (short) 10, false));

        return cellStyle;
    }

    private CellStyle getTableCell8CellStyle(Workbook workbook, Sheet excelSheet, Row row) {
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.RIGHT);
        cellStyle.setVerticalAlignment(VerticalAlignment.TOP);
        cellStyle.setWrapText(true);

        CellRangeAddress region = new CellRangeAddress(row.getRowNum(), row.getRowNum(), 28, 31);
        excelSheet.addMergedRegion(region);

        setThinCellBorder(cellStyle);
        setThinCellRangeBorder(region, excelSheet);

        cellStyle.setFont(getFont(workbook, ARIAL_FONT, (short) 10, false));

        return cellStyle;
    }

    private CellStyle getTableCell9CellStyle(Workbook workbook, Sheet excelSheet, Row row) {
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.RIGHT);
        cellStyle.setVerticalAlignment(VerticalAlignment.TOP);
        cellStyle.setWrapText(true);

        CellRangeAddress region = new CellRangeAddress(row.getRowNum(), row.getRowNum(), 32, 35);
        excelSheet.addMergedRegion(region);

        setThinCellBorder(cellStyle);
        setThinCellRangeBorder(region, excelSheet);

        cellStyle.setFont(getFont(workbook, ARIAL_FONT, (short) 10, false));

        return cellStyle;
    }

    private CellStyle getBillTableCell5CellStyle(Workbook workbook, Sheet excelSheet, Row row) {
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.RIGHT);
        cellStyle.setVerticalAlignment(VerticalAlignment.TOP);
        cellStyle.setWrapText(true);

        CellRangeAddress region = new CellRangeAddress(row.getRowNum(), row.getRowNum(), 25, 28);
        excelSheet.addMergedRegion(region);

        setThinCellBorder(cellStyle);
        setThinCellRangeBorder(region, excelSheet);

        cellStyle.setFont(getFont(workbook, ARIAL_FONT, (short) 10, false));

        return cellStyle;
    }

    private CellStyle getBillTableCell6CellStyle(Workbook workbook, Sheet excelSheet, Row row) {
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.RIGHT);
        cellStyle.setVerticalAlignment(VerticalAlignment.TOP);
        cellStyle.setWrapText(true);

        CellRangeAddress region = new CellRangeAddress(row.getRowNum(), row.getRowNum(), 29, 32);
        excelSheet.addMergedRegion(region);

        setThinCellBorder(cellStyle);
        setThinCellRangeBorder(region, excelSheet);

        cellStyle.setFont(getFont(workbook, ARIAL_FONT, (short) 10, false));

        return cellStyle;
    }

    private CellStyle getWordAmountDefinitionCellStyle(Workbook workbook, Sheet excelSheet) {
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setWrapText(true);

        cellStyle.setFont(getFont(workbook, ARIAL_FONT, (short) 9, true));

        return cellStyle;
    }


}
