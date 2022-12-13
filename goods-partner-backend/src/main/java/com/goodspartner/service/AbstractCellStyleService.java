package com.goodspartner.service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;

public abstract class AbstractCellStyleService implements CellStyleService {
    protected String ARIAL_FONT = "Arial";

    protected void setThinCellBorder(CellStyle cellStyle) {
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
    }

    protected void setThinCellRangeBorder(CellRangeAddress region, Sheet excelSheet) {
        RegionUtil.setBorderTop(BorderStyle.THIN, region, excelSheet);
        RegionUtil.setBorderBottom(BorderStyle.THIN, region, excelSheet);
        RegionUtil.setBorderLeft(BorderStyle.THIN, region, excelSheet);
        RegionUtil.setBorderRight(BorderStyle.THIN, region, excelSheet);
    }

    protected Font getFont(Workbook workbook, String name, short size, boolean isBold) {
        Font font = workbook.createFont();
        font.setFontName(name);
        font.setFontHeightInPoints(size);
        font.setBold(isBold);
        return font;
    }
}
