package com.goodspartner.service.util;

import com.goodspartner.report.ReportResult;
import com.goodspartner.report.ReportUtils;
import com.goodspartner.service.CellStyleService;
import com.goodspartner.service.dto.CellStyles;
import com.goodspartner.service.dto.DocumentCellInfoDto;
import com.goodspartner.service.dto.PreparedDocumentData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Component
@Slf4j
@RequiredArgsConstructor
public class DocumentCreator {
    private static final String DOCUMENT_NOT_FOUND = "Document %s not found";
    private static final String DOCUMENT_PATH = "documents/%stemplate.xlsx";
    private static final String XLSX_FILE_FORMAT = ".xlsx";

    private final CellStyleService cellStyleService;

    public void createDocuments(ZipOutputStream zipOutputStream, List<PreparedDocumentData> preparedDocumentData, String documentPrefix, Integer firstTableRow) {
        List<ReportResult> reportResults = preparedDocumentData.stream()
                .map(documentData -> createDocument(documentData, documentPrefix, firstTableRow))
                .collect(Collectors.toList());
        createZip(zipOutputStream, reportResults);
    }

    public ReportResult createDocument(PreparedDocumentData preparedInvoiceData, String documentPrefix, Integer firstTableRow) {
        String fileName = preparedInvoiceData.getDocumentName() + XLSX_FILE_FORMAT;
        try (InputStream template = ReportUtils.getTemplate(String.format(DOCUMENT_PATH, documentPrefix));
             ByteArrayOutputStream byteArrayOut = new ByteArrayOutputStream();
        ) {
            Workbook workBook = new XSSFWorkbook(template);
            Sheet excelSheet = getExcelSheet(workBook);
            enrichDocument(excelSheet, preparedInvoiceData.getHeader(), workBook);

            increaseTableRows(preparedInvoiceData.getTable().size(), excelSheet, firstTableRow);

            enrichDocument(excelSheet, preparedInvoiceData.getTable(), workBook);
            enrichDocument(excelSheet, preparedInvoiceData.getSignature(), workBook);
            workBook.write(byteArrayOut);
            return new ReportResult(fileName, byteArrayOut.toByteArray());
        } catch (IOException e) {
            log.error("file {} not exist", fileName);
        }
        return null;
    }

    public void createZip(ZipOutputStream zipOutputStream, List<ReportResult> reportResults) {
        reportResults.forEach(report -> writeFileToZip(zipOutputStream, report));
    }

    private void writeFileToZip(ZipOutputStream zipOutputStream, ReportResult reportResult){
        Exception exception = null;
        try (InputStream inputStream = new ByteArrayInputStream(reportResult.report())){
            zipOutputStream.putNextEntry(new ZipEntry(reportResult.name()));
            IOUtils.copy(inputStream, zipOutputStream);
        } catch (IOException e) {
            exception = e;
            log.error("Problem writing a file to zip", e);
        } finally {
            try {
                zipOutputStream.closeEntry();
            } catch (IOException e) {
                if (exception != null) {
                    e.addSuppressed(exception);
                }
                log.error("Problem when try close ZipEntry", e);
            }
        }
    }

    private Sheet getExcelSheet(Workbook workbook) {
        return workbook.getSheetAt(0);
    }

    private void increaseTableRows(Integer productsCount, Sheet excelSheet, Integer firstTableRow) {
        if (productsCount > 1) {
            int lastRow = excelSheet.getLastRowNum();
            excelSheet.shiftRows(firstTableRow, lastRow, productsCount - 1, true, true);
        }
    }

    private void enrichDocument(Sheet excelSheet, Map<Integer, List<Pair<Integer, DocumentCellInfoDto>>> header, Workbook workBook) {
        header.entrySet().forEach(entry -> createRow(excelSheet, entry, workBook));
    }

    private void createRow(Sheet excelSheet, Map.Entry<Integer, List<Pair<Integer, DocumentCellInfoDto>>> rowData, Workbook workBook) {
        Row row = excelSheet.getRow(rowData.getKey());
        if (row == null) {
            row = excelSheet.createRow(rowData.getKey());
        }
        setDataInRow(excelSheet, row, rowData.getValue(), workBook);
    }

    private void setDataInRow(Sheet excelSheet, Row row, List<Pair<Integer, DocumentCellInfoDto>> cellsData, Workbook workBook) {
        cellsData.forEach(cellData -> createCell(excelSheet, row, cellData, workBook));
    }

    private void createCell(Sheet excelSheet, Row row, Pair<Integer, DocumentCellInfoDto> cellData, Workbook workBook) {
        Cell cell = row.createCell(cellData.getFirst());
        DocumentCellInfoDto cellInfoDto = cellData.getSecond();
        cell.setCellValue(cellInfoDto.getValue());
        CellStyles cellStyles = cellInfoDto.getStyle();
        cell.setCellStyle(cellStyleService.getCellStyle(cellStyles, workBook, excelSheet, row));
    }
}
