package com.goodspartner.report;

import com.goodspartner.entity.Delivery;
import com.goodspartner.exception.DeliveryNotFoundException;
import com.goodspartner.repository.DeliveryRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Iterator;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CarsLoadReportGenerator implements ReportGenerator {
    private static final String TEMPLATE_PATH = "report/template_report_of_cars_with_orders.xlsx";

    private static final String REPORT_NAME = "Завантаження_машин_на_";
    private final DeliveryRepository deliveryRepository;
    private final CarLoadSheetGenerator carLoadSheetGenerator;

    @SneakyThrows
    @Override
    public ReportResult generateReport(UUID deliveryId) {
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new DeliveryNotFoundException(deliveryId));

        String reportName = ReportUtils.generateReportName(REPORT_NAME, delivery.getDeliveryDate());

        try (InputStream template = ReportUtils.getTemplate(TEMPLATE_PATH);
             ByteArrayOutputStream arrayStream = new ByteArrayOutputStream()) {

            XSSFWorkbook workbook = new XSSFWorkbook(template);
            Iterator<Sheet> sheetIterator = workbook.sheetIterator();

            while (sheetIterator.hasNext()) {
                XSSFSheet sheet = (XSSFSheet) sheetIterator.next();
                ByteArrayOutputStream sheetArrayStream = carLoadSheetGenerator.generateSheet(sheet, delivery);
                sheetArrayStream.writeTo(arrayStream);
            }

            workbook.write(arrayStream);

            return new ReportResult(reportName, arrayStream.toByteArray());
        }
    }
}