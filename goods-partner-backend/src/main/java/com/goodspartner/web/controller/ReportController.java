package com.goodspartner.web.controller;

import com.goodspartner.report.CarsLoadReportGenerator;
import com.goodspartner.report.OrdersReportGenerator;
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
import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
@Slf4j
public class ReportController {

    private final OrdersReportGenerator ordersReportGenerator;
    private final CarsLoadReportGenerator carsLoadReportGenerator;

    @PreAuthorize("hasAnyRole('ADMIN', 'LOGIST')")
    @GetMapping("/orders")
    public HttpEntity<byte[]> generateOrdersReport(@RequestParam LocalDate date) {
        ReportResult reportResult = ordersReportGenerator.generateReport(date);
        return toHttpEntity(reportResult);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'LOGIST', 'DRIVER')")
    @GetMapping("/carsload")
    public HttpEntity<byte[]> generateCarsLoadReport(@RequestParam LocalDate date) {
        ReportResult reportResult = carsLoadReportGenerator.generateReport(date);
        return toHttpEntity(reportResult);
    }

    private static HttpEntity<byte[]> toHttpEntity(ReportResult reportResult) {
        ContentDisposition contentDisposition = ContentDisposition.builder("attachment")
                .filename(reportResult.name(), StandardCharsets.UTF_8)
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDisposition(contentDisposition);
        return new HttpEntity<>(reportResult.report(), headers);
    }


}
