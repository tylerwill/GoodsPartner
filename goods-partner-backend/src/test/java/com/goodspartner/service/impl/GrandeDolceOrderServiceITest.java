package com.goodspartner.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.goodspartner.AbstractBaseITest;
import com.goodspartner.config.TestSecurityDisableConfig;
import com.goodspartner.dto.OrderDto;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletWebRequest;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

// Test integration with 1C server

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestInstance(PER_CLASS)
@Import({TestSecurityDisableConfig.class})
@AutoConfigureMockMvc(addFilters = false)
class GrandeDolceOrderServiceITest extends AbstractBaseITest {

    @LocalServerPort
    private int port;

    @Autowired
    private GrandeDolceOrderService grandeDolceOrderService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    public void setup() {
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        mockRequest.setLocalPort(port);
        RequestAttributes request = new ServletWebRequest(mockRequest);
        RequestContextHolder.setRequestAttributes(request);
    }

    /*
    For some reason 1C is not responding for an orders fo 2020/02/02
     */
    private static final LocalDate DATE = LocalDate.of(2022, 2, 4);

    @Test
    void getOrdersFromExternalSource() throws JsonProcessingException {

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
