package com.goodspartner.web.controller;

import com.goodspartner.report.CarsLoadReportGenerator;
import com.goodspartner.report.ReportResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@RequiredArgsConstructor
@Slf4j
public class ReportController {

    private final CarsLoadReportGenerator carsLoadReportGenerator;

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
        ReportResult reportResult = carsLoadReportGenerator.generateReport(deliveryId);
        return toHttpEntity(reportResult);
    }


}
