package com.goodspartner.service;

import com.goodspartner.service.dto.CellStyles;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

public interface CellStyleService {
    CellStyle getCellStyle(CellStyles cellStyles, Workbook workbook, Sheet excelSheet, Row row);
}
