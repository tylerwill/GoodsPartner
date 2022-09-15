package com.goodspartner.report;

import com.goodspartner.AbstractBaseITest;
import com.goodspartner.config.TestSecurityDisableConfig;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletWebRequest;

import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestInstance(PER_CLASS)
@Import({TestSecurityDisableConfig.class})
@AutoConfigureMockMvc(addFilters = false)
class OrdersReportGeneratorITest extends AbstractBaseITest {

    @LocalServerPort
    private int port;

    @Autowired
    private OrdersReportGenerator ordersReportGenerator;

    @Value("${reports.destination}")
    private String reportsDestination;

    @SneakyThrows
    private static void writeReportToFile(ReportResult reportResult, File destinationFile) {
        try (FileOutputStream fileOutputStream = new FileOutputStream(destinationFile)) {
            fileOutputStream.write(reportResult.report());
        }
    }

    @BeforeAll
    public void setup() {
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        mockRequest.setLocalPort(port);
        RequestAttributes request = new ServletWebRequest(mockRequest);
        RequestContextHolder.setRequestAttributes(request);
    }

    @DisplayName("Test for generate orders report with three orders")
    @Test
    void testGenerateOrderReportWithThreeOrders() {
        LocalDate date = LocalDate.of(2022, 2, 4);

        new File(reportsDestination).mkdirs();

        Consumer<ReportResult> reportResultConsumer = r -> {
            File destinationFile = new File(reportsDestination, r.name());
            writeReportToFile(r, destinationFile);

            assertTrue(destinationFile.exists());
            assertTrue(destinationFile.length() > 0);
        };

        ordersReportGenerator.generateReport(date, reportResultConsumer);
    }


}