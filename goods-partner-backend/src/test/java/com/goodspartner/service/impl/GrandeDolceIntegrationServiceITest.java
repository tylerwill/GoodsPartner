package com.goodspartner.service.impl;

import com.goodspartner.AbstractBaseITest;
import com.goodspartner.config.TestSecurityDisableConfig;
import com.goodspartner.dto.OrderDto;
import com.goodspartner.entity.Store;
import com.goodspartner.service.StoreService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.mockito.Mockito.when;

// Test integration with 1C server and mockData processing

@Disabled
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@Import({TestSecurityDisableConfig.class})
@AutoConfigureMockMvc(addFilters = false)
class GrandeDolceIntegrationServiceITest extends AbstractBaseITest {

    private static final LocalDate DATE = LocalDate.of(2022, 2, 4);

    @MockBean
    private StoreService mockStoreService;

    @Autowired
    private GrandeDolceIntegrationService grandeDolceOrderService;

    @BeforeEach
    public void setup() {

        Store store = new Store(UUID.fromString("5688492e-ede4-45d3-923b-5f9773fd3d4b"),
                "Склад №1",
                "15, Калинова вулиця, Фастів, Фастівська міська громада, Фастівський район, Київська область, 08500, Україна",
                50.08340335,
                29.885050630832627);

        when(mockStoreService.getMainStore()).thenReturn(store);
    }


    @Test
    void getOrdersFromExternalSource() {
        // given
        final String orderExcludedByDeletedMark = "00000002414";
        final String orderExcludedByDeletedInvoice = "00000002515";
        final String orderExcludedAsNoInvoiceFound = "00000002413";
        // when
        List<OrderDto> orders = grandeDolceOrderService.findAllByShippingDate(DATE);
        // then
        Assertions.assertEquals(9, orders.size());

        orders.forEach(order -> {
            Assertions.assertNotNull(order.getAddress());
            Assertions.assertNotEquals("", order.getAddress());
        });

        Map<String, String> excludedList = orders.stream()
                .filter(OrderDto::isExcluded)
                .collect(Collectors.toMap(OrderDto::getOrderNumber, OrderDto::getExcludeReason));

        Assertions.assertEquals(3, excludedList.size());
        Assertions.assertEquals("Замовлення: 00000002414 має флаг видалення в 1С", excludedList.get(orderExcludedByDeletedMark));
        Assertions.assertEquals("Відсутня або видалена видаткова в 1С для замовлення: 00000002515", excludedList.get(orderExcludedByDeletedInvoice));
        Assertions.assertEquals("Відсутня або видалена видаткова в 1С для замовлення: 00000002413", excludedList.get(orderExcludedAsNoInvoiceFound));
    }

    @Test
    void testMockDataFetchingAndMapping() {
        grandeDolceOrderService.findAllByShippingDate(LocalDate.of(2022, 2, 4));
        grandeDolceOrderService.findAllByShippingDate(LocalDate.of(2022, 2, 7));
        grandeDolceOrderService.findAllByShippingDate(LocalDate.of(2022, 2, 17));
        grandeDolceOrderService.findAllByShippingDate(LocalDate.of(2022, 2, 21));
    }

    @Test
    @DisplayName("when CalculateOrders then Correct Total Orders Weight Returned")
    void givenOrders_whenCalculateTotalOrdersWeight_thenCorrectResultReturned() {
        double expectedTotalWeight = 2494;

        List<OrderDto> ordersByDate = grandeDolceOrderService.findAllByShippingDate(DATE);
        double totalOrdersWeight = grandeDolceOrderService.calculateTotalOrdersWeight(ordersByDate);

        Assertions.assertEquals(expectedTotalWeight, totalOrdersWeight);
    }
}
