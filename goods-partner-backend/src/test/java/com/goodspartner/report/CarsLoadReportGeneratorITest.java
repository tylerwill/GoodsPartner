package com.goodspartner.report;

import com.goodspartner.dto.CarDto;
import com.goodspartner.dto.OrderDto;
import com.goodspartner.dto.ProductDto;
import com.goodspartner.service.RouteService;
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

class CarsLoadReportGeneratorITest {
    private final static String REPORTS_DESTINATION = "reports";
    private final CarLoadSheetGenerator carLoadSheetGenerator = new CarLoadSheetGenerator();

    @SneakyThrows
    private static void writeReportToFile(ReportResult reportResult, File destinationFile) {
        try (FileOutputStream fileOutputStream = new FileOutputStream(destinationFile)) {
            fileOutputStream.write(reportResult.report());
        }
    }

    @Test
    @DisplayName("Generate Report Of Loading Car With One Order")
    void testGenerateReport_ofLoadingCarWithOneOrder() {
        CarDto carDto = new CarDto(
                1,
                "Mercedes Vito",
                "AA 2222 CT",
                "Ivan Piddubny",
                1000,
                true,
                false,
                59.32,
                10);

        ProductDto product = ProductDto.builder()
                .productName("678968 Суміш для випікання Мрія Маффіни з апельсиновою цедрою")
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

        RoutesCalculation.CarLoadDto carLoadDto =
                new RoutesCalculation.CarLoadDto(carDto, List.of(order));

        RoutesCalculation routesCalculation = RoutesCalculation.builder()
                .carLoadDetails(List.of(carLoadDto))
                .date(LocalDate.of(2022, 7, 12))
                .build();


        RouteService routeService = Mockito.mock(RouteService.class);
        CarsLoadReportGenerator carsLoadReportGenerator = new CarsLoadReportGenerator(routeService, carLoadSheetGenerator);

        Mockito.when(routeService.calculateRoutesByDate(LocalDate.of(2022, 7, 12)))
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

        carsLoadReportGenerator.generateReport(date, reportResultConsumer);
    }

    @Test
    @DisplayName("Generate Report Of Loading Car With Three Orders")
    void testGenerateReport_ofLoadingCarWithThreeOrders() {
        CarDto carDto = new CarDto(
                1,
                "Mercedes Vito",
                "AA 2222 CT",
                "Ivan Piddubny",
                1000,
                true,
                false,
                59.32,
                10);


        ProductDto productFirst = ProductDto.builder()
                .productName("3434 Паста шоколадна")
                .amount(1)
                .unitWeight(1.52)
                .totalProductWeight(1.52)
                .build();

        ProductDto productSecond = ProductDto.builder()
                .productName("46643 Фарба харчова синя натуральна")
                .amount(10)
                .unitWeight(57.8)
                .totalProductWeight(578)
                .build();

        ProductDto productThird = ProductDto.builder()
                .productName("678968 Суміш для випікання Мрія Маффіни з апельсиновою цедрою")
                .amount(10)
                .unitWeight(47.8)
                .totalProductWeight(478)
                .build();

        OrderDto orderFirst = OrderDto.builder()
                .products(List.of(productSecond, productThird))
                .orderNumber(String.valueOf(35665))
                .orderWeight(579.52)
                .id(1)
                .build();
        OrderDto orderSecond = OrderDto.builder()
                .products(List.of(productThird))
                .orderNumber(String.valueOf(36325))
                .orderWeight(1.52)
                .id(2)
                .build();
        OrderDto orderThird = OrderDto.builder()
                .products(List.of(productFirst, productSecond))
                .orderNumber(String.valueOf(353625))
                .orderWeight(579.52)
                .id(3)
                .build();

        RoutesCalculation.CarLoadDto carLoadDto =
                new RoutesCalculation.CarLoadDto(carDto, List.of(orderFirst, orderSecond, orderThird));


        RoutesCalculation routesCalculation = RoutesCalculation.builder()
                .carLoadDetails(List.of(carLoadDto))
                .date(LocalDate.of(2022, 7, 12))
                .build();

        RouteService routeService = Mockito.mock(RouteService.class);
        CarsLoadReportGenerator carsLoadReportGenerator = new CarsLoadReportGenerator(routeService, carLoadSheetGenerator);

        Mockito.when(routeService.calculateRoutesByDate(LocalDate.of(2022, 7, 12)))
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

        carsLoadReportGenerator.generateReport(date, reportResultConsumer);
    }
}