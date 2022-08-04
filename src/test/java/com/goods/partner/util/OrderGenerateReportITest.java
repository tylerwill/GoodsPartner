package com.goods.partner.util;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.spring.api.DBRider;
import com.goods.partner.AbstractBaseITest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertTrue;

@DBRider
@DataSet(value = "common/dataset.yml",
        disableConstraints = true)
class OrderGenerateReportITest extends AbstractBaseITest {

    @Autowired
    private OrderGenerateReport orderGenerateReport;

    @DisplayName("Test for generate orders report")
    @Test

    void testGenerateOrderReport() throws IOException {
        LocalDate date = LocalDate.of(2022, 7, 11);
        File destinationFile = new File("src/main/resources/report/report_list_of_orders_" + date + ".xlsx");

        orderGenerateReport.generateReport(date);

        assertTrue(destinationFile.exists());
    }

}