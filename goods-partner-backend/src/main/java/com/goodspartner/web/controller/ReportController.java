package com.goodspartner.web.controller;

import com.goodspartner.report.CarsLoadReportGenerator;
import com.goodspartner.report.OrdersReportGenerator;
import com.goodspartner.report.ReportResult;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.util.function.Consumer;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class ReportController {

    private final OrdersReportGenerator ordersReportGenerator;
    private final CarsLoadReportGenerator carsLoadReportGenerator;

    @NotNull
    private static Consumer<ReportResult> writeReport(HttpServletResponse response) {
        return (r) -> {
            // TODO: Replace with spring constant
            response.setContentType("application/octet-stream");
            String headerKey = "Content-Disposition";
            String headerValue = "attachment; filename=" + r.name();
            response.setHeader(headerKey, headerValue);
            writeReport(response, r);
        };
    }

    @SneakyThrows
    private static void writeReport(HttpServletResponse response, ReportResult r) {
        response.getOutputStream().write(r.report());
    }

    @GetMapping("/orders")
    public void generateOrdersReport(@RequestParam String date, HttpServletResponse response) {
        ordersReportGenerator.generateReport(LocalDate.parse(date), writeReport(response));
    }

    @GetMapping("/carsload")
    public void generateCarsLoadReport(@RequestParam String date, HttpServletResponse response) {
        carsLoadReportGenerator.generateReport(LocalDate.parse(date), writeReport(response));
    }


}
