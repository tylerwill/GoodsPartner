package com.goodspartner.report;

import com.goodspartner.AbstractBaseITest;
import com.goodspartner.config.TestSecurityDisableConfig;
import com.goodspartner.dto.Product;
import com.goodspartner.entity.Car;
import com.goodspartner.entity.CarLoad;
import com.goodspartner.entity.Delivery;
import com.goodspartner.entity.OrderExternal;
import com.goodspartner.entity.User;
import com.goodspartner.repository.DeliveryRepository;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletWebRequest;

import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestInstance(PER_CLASS)
@Import({TestSecurityDisableConfig.class})
@DirtiesContext
@AutoConfigureMockMvc(addFilters = false)
@Disabled
class OrdersReportGeneratorITest extends AbstractBaseITest {

    @LocalServerPort
    private int port;

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

        // Prepare data
        Delivery delivery = prepareDeliveryWithOneOrder();

        // given
        DeliveryRepository deliveryRepository = Mockito.mock(DeliveryRepository.class);
        OrdersReportGenerator ordersReportGenerator = new OrdersReportGenerator(deliveryRepository);

        Mockito.when(deliveryRepository.findById(delivery.getId())).thenReturn(Optional.of(delivery));

        // when
        ReportResult reportResult = ordersReportGenerator.generateReport(delivery.getId());// TODO This is testing nothing

        new File(reportsDestination).mkdirs();

        File destinationFile = new File(reportsDestination, reportResult.name());
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
        orderFirst.setId(1L);

        CarLoad carLoad = new CarLoad();
        carLoad.setCar(car);
        carLoad.setOrders(Arrays.asList(orderFirst));
        carLoad.setDelivery(delivery);

        delivery.setCarLoads(List.of(carLoad));
        return delivery;
    }

    @NotNull
    private Car getCar() {

        User user = new User(555,
                "Ivan Piddubny",
                "userEmail@gmail",
                User.UserRole.DRIVER,
                true);

        return new Car(
                1,
                "Mercedes Vito",
                user,
                true,
                false,
                "AA 2222 CT",
                1000,
                10,
                false);
    }

}