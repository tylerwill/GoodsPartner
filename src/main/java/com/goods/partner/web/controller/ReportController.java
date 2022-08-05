package com.goods.partner.web.controller;

import com.goods.partner.report.OrdersReportGenerator;
import com.goods.partner.report.ReportResult;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;

@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
public class ReportController {

    private final OrdersReportGenerator ordersReportGenerator;

    @GetMapping("/orders/generate")
    public void generateOrders(@RequestParam String date, HttpServletResponse response) {
        ordersReportGenerator.generateReport(LocalDate.parse(date), (r) -> {
            response.setContentType("application/octet-stream");
            String headerKey = "Content-Disposition";
            String headerValue = "attachment; filename=" + r.name();
            response.setHeader(headerKey, headerValue);
            writeReport(response, r);
        });
    }

    @SneakyThrows
    private static void writeReport(HttpServletResponse response, ReportResult r) {
        response.getOutputStream().write(r.report());
    }


}
