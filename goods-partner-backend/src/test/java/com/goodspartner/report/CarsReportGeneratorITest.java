package com.goodspartner.report;

import com.goodspartner.dto.CarDto;
import com.goodspartner.dto.OrderDto;
import com.goodspartner.dto.ProductDto;
import com.goodspartner.service.OrderService;
import com.goodspartner.web.controller.response.RoutesCalculation;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.util.List;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class CarsReportGeneratorITest  {
    private final static String REPORTS_DESTINATION = "reports";

    @Test
    @DisplayName("Generate Report Of Loading Car With One Order")
    void testGenerateReport_ofLoadingCarWithOneOrder() {
             CarDto carDto = CarDto.builder()
                .id(1)
                .name("Mercedes Vito")
                .driver("Ivan Piddubny")
                .licencePlate("AA 2222 CT")
                .travelCost(10)
                .available(true)
                .cooler(false)
                .weightCapacity(1000)
                .loadSize(59.32)
                .build();

        ProductDto product = ProductDto.builder()
                .productName("3434 Паста шоколадна")
                .amount(1)
                .unitWeight(1.52)
                .totalProductWeight(1.52)
                .build();

        OrderDto order = OrderDto.builder()
                .products(List.of(product))
                .orderNumber(String.valueOf(35635))
                .orderWeight(1.52)
                .id(6)
                .build();

        RoutesCalculation.CarLoadDto carLoadDto = RoutesCalculation.CarLoadDto.builder()
                .car(carDto)
                .orders(List.of(order))
                .build();

        RoutesCalculation routesCalculation = RoutesCalculation.builder()
                .carLoadDetails(List.of(carLoadDto))
                .date(LocalDate.of(2022, 7, 12))
                .build();


        OrderService orderService = Mockito.mock(OrderService.class);
        CarsReportGenerator carsReportGenerator = new CarsReportGenerator(orderService);

        Mockito.when(orderService.calculateRoutes(LocalDate.of(2022, 7, 12)))
                .thenReturn(routesCalculation);


        // when
        LocalDate date = LocalDate.of(2022, 7, 12);

        new File(REPORTS_DESTINATION).mkdirs();

        Consumer<ReportResult> reportResultConsumer = r -> {
            File destinationFile = new File(REPORTS_DESTINATION, r.name());
            writeReportToFile(r, destinationFile);

            assertTrue(destinationFile.exists());
            assertTrue(destinationFile.length() > 0);
        };

        carsReportGenerator.generateReport(date, reportResultConsumer);
    }

    @Test
    @DisplayName("Generate Report Of Loading Car With Three Orders")
    void testGenerateReport_ofLoadingCarWithThreeOrders() {
        CarDto carDto = CarDto.builder()
                .id(1)
                .name("Mercedes Vito")
                .driver("Ivan Piddubny")
                .licencePlate("AA 2222 CT")
                .travelCost(10)
                .available(true)
                .cooler(false)
                .weightCapacity(1000)
                .loadSize(59.32)
                .build();

        ProductDto product1 = ProductDto.builder()
                .productName("3434 Паста шоколадна")
                .amount(1)
                .unitWeight(1.52)
                .totalProductWeight(1.52)
                .build();

        ProductDto product2 = ProductDto.builder()
                .productName("46643 Фарба харчова синя")
                .amount(10)
                .unitWeight(57.8)
                .totalProductWeight(578)
                .build();

        OrderDto order1 = OrderDto.builder()
                .products(List.of(product1, product2))
                .orderNumber(String.valueOf(35665))
                .orderWeight(579.52)
                .id(1)
                .build();
        OrderDto order2 = OrderDto.builder()
                .products(List.of(product1))
                .orderNumber(String.valueOf(36325))
                .orderWeight(1.52)
                .id(2)
                .build();
        OrderDto order3 = OrderDto.builder()
                .products(List.of(product1, product2))
                .orderNumber(String.valueOf(353625))
                .orderWeight(579.52)
                .id(3)
                .build();

        RoutesCalculation.CarLoadDto carLoadDto = RoutesCalculation.CarLoadDto.builder()
                .car(carDto)
                .orders(List.of(order1, order2, order3))
                .build();

        RoutesCalculation routesCalculation = RoutesCalculation.builder()
                .carLoadDetails(List.of(carLoadDto))
                .date(LocalDate.of(2022, 7, 12))
                .build();

        OrderService orderService = Mockito.mock(OrderService.class);
        CarsReportGenerator carsReportGenerator = new CarsReportGenerator(orderService);

        Mockito.when(orderService.calculateRoutes(LocalDate.of(2022, 7, 12)))
                .thenReturn(routesCalculation);


        // when
        LocalDate date = LocalDate.of(2022, 7, 12);

        new File(REPORTS_DESTINATION).mkdirs();

        Consumer<ReportResult> reportResultConsumer = r -> {
            File destinationFile = new File(REPORTS_DESTINATION, r.name());
            writeReportToFile(r, destinationFile);

            assertTrue(destinationFile.exists());
            assertTrue(destinationFile.length() > 0);
        };

        carsReportGenerator.generateReport(date, reportResultConsumer);
    }

    @SneakyThrows
    private static void writeReportToFile(ReportResult reportResult, File destinationFile) {
        try (FileOutputStream fileOutputStream = new FileOutputStream(destinationFile)) {
            fileOutputStream.write(reportResult.report());
        }
    }
}
