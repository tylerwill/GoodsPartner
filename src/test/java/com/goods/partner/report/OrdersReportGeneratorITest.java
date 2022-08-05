package com.goods.partner.report;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.spring.api.DBRider;
import com.goods.partner.AbstractBaseITest;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertTrue;

@DBRider
@DataSet(value = "common/dataset.yml",
        disableConstraints = true)
class OrdersReportGeneratorITest extends AbstractBaseITest {

    @Autowired
    private OrdersReportGenerator ordersReportGenerator;

    @Value("${reports.destination}")
    private String reportsDestination;

    @DisplayName("Test for generate orders report")
    @Test
    void testGenerateOrderReport() throws IOException {
        LocalDate date = LocalDate.of(2022, 7, 11);

        new File(reportsDestination).mkdirs();

        Consumer<ReportResult> reportResultConsumer = r -> {
            File destinationFile = new File(reportsDestination, r.name());
            writeReportToFile(r, destinationFile);
        };

        ordersReportGenerator.generateReport(date, reportResultConsumer);
    }

    @SneakyThrows
    private static void writeReportToFile(ReportResult reportResult, File destinationFile) {
        try (FileOutputStream fileOutputStream = new FileOutputStream(destinationFile)) {
            fileOutputStream.write(reportResult.report());
        }
    }


}