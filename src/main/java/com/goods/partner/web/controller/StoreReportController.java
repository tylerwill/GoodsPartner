package com.goods.partner.web.controller;

import com.goods.partner.util.StoreReportExcelGenerator;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;

@RestController
@RequestMapping("/calculate/stores")
public class StoreReportController {

    @Autowired
    private StoreReportExcelGenerator reportExcelGenerator;

    @GetMapping("/generate")
    public void generate(HttpServletResponse response, @RequestParam String date) throws IOException, InvalidFormatException {
        response.setContentType("application/octet-stream");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String currentDateTime = dateFormatter.format(new Date());

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=orders_at_store" + currentDateTime + ".xlsx";
        response.setHeader(headerKey, headerValue);

        reportExcelGenerator.generateExcelFile(response, LocalDate.parse(date));
    }
}
