package com.goodspartner.service.impl;

import com.goodspartner.AbstractBaseITest;
import com.goodspartner.config.TestSecurityDisableConfig;
import com.goodspartner.dto.OrderDto;
import com.goodspartner.entity.Store;
import com.goodspartner.service.StoreService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletWebRequest;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import static org.mockito.Mockito.when;

// Test integration with 1C server

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestInstance(PER_CLASS)
@Import({TestSecurityDisableConfig.class})
@AutoConfigureMockMvc(addFilters = false)
class GrandeDolceIntegrationServiceITest extends AbstractBaseITest {

    @LocalServerPort
    private int port;

    @MockBean
    private StoreService mockStoreService;
    @Mock
    private Store mockStore;

    @Autowired
    private GrandeDolceIntegrationService grandeDolceOrderService;

    @BeforeAll
    public void setup() {
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        mockRequest.setLocalPort(port);
        RequestAttributes request = new ServletWebRequest(mockRequest);
        RequestContextHolder.setRequestAttributes(request);

        when(mockStoreService.getMainStore()).thenReturn(mockStore);
        when(mockStore.getName()).thenReturn("Склад №1");
    }

    /*
    For some reason 1C is not responding for an orders fo 2020/02/02
     */
    private static final LocalDate DATE = LocalDate.of(2022, 2, 4);

    @Test
    void getOrdersFromExternalSource() {

        List<OrderDto> orders = grandeDolceOrderService.findAllByShippingDate(DATE);

        Assertions.assertEquals(9, orders.size());

        orders.forEach(order -> {
            Assertions.assertNotNull(order.getAddress());
            Assertions.assertNotEquals("", order.getAddress());
        });
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
