package com.goodspartner.web.controller;

import com.goodspartner.entity.DeliveryType;
import com.goodspartner.report.ReportGenerator;
import com.goodspartner.report.ReportResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/reports")
@Slf4j
public class ReportController {

    @Autowired
    @Qualifier("carsLoadReportGenerator")
    private ReportGenerator carsLoadReportGenerator;

    @Autowired
    @Qualifier("productReportGenerator")
    private ReportGenerator productReportGenerator;

    private static HttpEntity<byte[]> toHttpEntity(ReportResult reportResult) {
        ContentDisposition contentDisposition = ContentDisposition.builder("attachment")
                .filename(reportResult.name(), StandardCharsets.UTF_8)
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDisposition(contentDisposition);
        return new HttpEntity<>(reportResult.report(), headers);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'LOGISTICIAN', 'DRIVER')")
    @GetMapping("/carsload")
    public HttpEntity<byte[]> generateCarsLoadReport(@RequestParam UUID deliveryId) {
        ReportResult reportResult = carsLoadReportGenerator.generateReport(deliveryId, DeliveryType.REGULAR);
        return toHttpEntity(reportResult);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'LOGISTICIAN', 'DRIVER')")
    @GetMapping("/shipment")
    public HttpEntity<byte[]> generateProductsLoadReport(@RequestParam UUID deliveryId, @RequestParam DeliveryType deliveryType) {
        ReportResult reportResult = productReportGenerator.generateReport(deliveryId, deliveryType);
        return toHttpEntity(reportResult);
    }

}
