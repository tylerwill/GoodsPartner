package com.goodspartner.web.controller;

import com.goodspartner.report.OrdersReportGenerator;
import com.goodspartner.report.ReportResult;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class ReportController {

    private final OrdersReportGenerator ordersReportGenerator;

    @SneakyThrows
    private static void writeReport(HttpServletResponse response, ReportResult r) {
        response.getOutputStream().write(r.report());
    }

    @GetMapping("/orders")
    public void generateOrders(@RequestParam String date, HttpServletResponse response) {
        ordersReportGenerator.generateReport(LocalDate.parse(date), (r) -> {
            // TODO: Replace with spring constant
            response.setContentType("application/octet-stream");
            String headerKey = "Content-Disposition";
            String headerValue = "attachment; filename=" + r.name();
            response.setHeader(headerKey, headerValue);
            writeReport(response, r);
        });
    }


}