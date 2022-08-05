package com.goods.partner.web.controller;

import com.goods.partner.report.OrdersReportGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;

@RestController
@RequestMapping("/reports")
public class ReportController {

    @Autowired
    private OrdersReportGenerator ordersReportGenerator;

    @PostMapping("/orders/generate")
    public void generateOrders(@RequestParam String date) throws IOException {
      //  response.setContentType("application/octet-stream");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String currentDateTime = dateFormatter.format(new Date());

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=orders_at_store " + currentDateTime + ".xlsx";
      //  response.setHeader(headerKey, headerValue);
        ordersReportGenerator.generateReport(LocalDate.parse(date));
    }
}
