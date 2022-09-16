package com.goodspartner.report;

import com.goodspartner.service.RouteService;
import com.goodspartner.web.controller.response.RoutesCalculation;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.Iterator;

@Service
@RequiredArgsConstructor
public class CarsLoadReportGenerator implements ReportGenerator {
    private static final String TEMPLATE_PATH = "report/template_report_of_cars_with_orders.xlsx";

    private static final String REPORT_NAME = "Завантаження_машин_на_";
    private final RouteService routeService;
    private final CarLoadSheetGenerator carLoadSheetGenerator;

    @SneakyThrows
    @Override
    public ReportResult generateReport(LocalDate date) {
        RoutesCalculation routesCalculation = routeService.calculateRoutesByDate(date);
        String reportName = ReportUtils.generateReportName(REPORT_NAME, date);

        try (InputStream template = ReportUtils.getTemplate(TEMPLATE_PATH);
             ByteArrayOutputStream arrayStream = new ByteArrayOutputStream()) {

            XSSFWorkbook workbook = new XSSFWorkbook(template);
            Iterator<Sheet> sheetIterator = workbook.sheetIterator();

            while (sheetIterator.hasNext()) {
                XSSFSheet sheet = XSSFSheet.class.cast(sheetIterator.next());
                ByteArrayOutputStream sheetArrayStream = carLoadSheetGenerator.generateSheet(sheet, routesCalculation, date);
                sheetArrayStream.writeTo(arrayStream);
            }

            workbook.write(arrayStream);

            return new ReportResult(reportName, arrayStream.toByteArray());
        }
    }
}