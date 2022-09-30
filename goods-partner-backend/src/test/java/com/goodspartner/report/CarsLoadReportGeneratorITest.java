package com.goodspartner.report;

import com.goodspartner.dto.Product;
import com.goodspartner.entity.Car;
import com.goodspartner.entity.CarLoad;
import com.goodspartner.entity.Delivery;
import com.goodspartner.entity.OrderExternal;
import com.goodspartner.repository.DeliveryRepository;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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

        // Data prep

        Delivery delivery = prepareDeliveryWithOneOrder();

        // given
        DeliveryRepository deliveryRepository = Mockito.mock(DeliveryRepository.class);
        CarsLoadReportGenerator carsLoadReportGenerator = new CarsLoadReportGenerator(deliveryRepository, carLoadSheetGenerator);

        Mockito.when(deliveryRepository.findById(delivery.getId())).thenReturn(Optional.of(delivery));

        // when
        ReportResult reportResult = carsLoadReportGenerator.generateReport(delivery.getId());

        // then
        new File(REPORTS_DESTINATION).mkdirs();
        File destinationFile = new File(REPORTS_DESTINATION, reportResult.name());
        writeReportToFile(reportResult, destinationFile);

        assertTrue(destinationFile.exists());
        assertTrue(destinationFile.length() > 0);
    }

    @NotNull
    private Delivery prepareDeliveryWithOneOrder() {
        Delivery delivery = new Delivery();
        delivery.setId(UUID.randomUUID());
        delivery.setDeliveryDate(LocalDate.now());

        Car car = getCar();

        Product product = Product.builder()
                .productName("678968 Суміш для випікання Мрія Маффіни з апельсиновою цедрою")
                .amount(1)
                .unitWeight(1.52)
                .totalProductWeight(1.52)
                .build();

        OrderExternal orderFirst = new OrderExternal();
        orderFirst.setProducts(List.of(product));
        orderFirst.setOrderNumber(String.valueOf(35665));
        orderFirst.setOrderWeight(579.52);
        orderFirst.setId(1);

        CarLoad carLoad = new CarLoad();
        carLoad.setCar(car);
        carLoad.setOrders(Arrays.asList(orderFirst));
        carLoad.setDelivery(delivery);

        delivery.setCarLoads(List.of(carLoad));
        return delivery;
    }

    @NotNull
    private Car getCar() {
        return new Car(
                1,
                "Mercedes Vito",
                "Ivan Piddubny",
                true,
                false,
                "AA 2222 CT",
                1000,
                10,
                false);
    }

    @Test
    @DisplayName("Generate Report Of Loading Car With Three Orders")
    void testGenerateReport_ofLoadingCarWithThreeOrders() {
        // Data prep
        Delivery delivery = prepareDeliveryWithThreeOrders();

        // given
        DeliveryRepository deliveryRepository = Mockito.mock(DeliveryRepository.class);
        CarsLoadReportGenerator carsLoadReportGenerator = new CarsLoadReportGenerator(deliveryRepository, carLoadSheetGenerator);

        Mockito.when(deliveryRepository.findById(delivery.getId())).thenReturn(Optional.of(delivery));

        // when
        LocalDate date = LocalDate.of(2022, 7, 12);

        new File(REPORTS_DESTINATION).mkdirs();

        ReportResult reportResult = carsLoadReportGenerator.generateReport(delivery.getId());

        File destinationFile = new File(REPORTS_DESTINATION, reportResult.name());
        writeReportToFile(reportResult, destinationFile);

        assertTrue(destinationFile.exists());
        assertTrue(destinationFile.length() > 0);
    }

    @NotNull
    private Delivery prepareDeliveryWithThreeOrders() {
        Delivery delivery = new Delivery();
        delivery.setId(UUID.randomUUID());
        delivery.setDeliveryDate(LocalDate.now());

        Car car = getCar();

        Product productFirst = Product.builder()
                .productName("3434 Паста шоколадна")
                .amount(1)
                .unitWeight(1.52)
                .totalProductWeight(1.52)
                .build();

        Product productSecond = Product.builder()
                .productName("46643 Фарба харчова синя натуральна")
                .amount(10)
                .unitWeight(57.8)
                .totalProductWeight(578)
                .build();

        Product productThird = Product.builder()
                .productName("678968 Суміш для випікання Мрія Маффіни з апельсиновою цедрою")
                .amount(10)
                .unitWeight(47.8)
                .totalProductWeight(478)
                .build();

        OrderExternal orderFirst = new OrderExternal();
        orderFirst.setProducts(List.of(productFirst, productSecond));
        orderFirst.setOrderNumber(String.valueOf(35665));
        orderFirst.setOrderWeight(579.52);
        orderFirst.setId(1);

        OrderExternal orderSecond = new OrderExternal();
        orderSecond.setProducts(List.of(productThird));
        orderSecond.setOrderNumber(String.valueOf(36325));
        orderSecond.setOrderWeight(1.52);
        orderSecond.setId(2);

        OrderExternal orderThird = new OrderExternal();
        orderThird.setProducts(List.of(productFirst, productSecond, productThird));
        orderThird.setOrderNumber(String.valueOf(353625));
        orderThird.setOrderWeight(579.52);
        orderThird.setId(3);

        CarLoad carLoad = new CarLoad();
        carLoad.setCar(car);
        carLoad.setOrders(Arrays.asList(orderFirst));
        carLoad.setDelivery(delivery);

        delivery.setCarLoads(List.of(carLoad));
        return delivery;
    }
}