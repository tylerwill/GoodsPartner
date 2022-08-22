package com.goodspartner.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.goodspartner.AbstractBaseITest;
import com.goodspartner.dto.OrderDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

// Test integration with 1C server
@Disabled
public class ExternalOrderServiceITest extends AbstractBaseITest {

    @Autowired
    private ExternalOrderService externalOrderService;

    @Autowired
    private ObjectMapper objectMapper;

    /*
    For some reason 1C is not responding for an orders fo 2020/02/02
     */
    private static final LocalDate DATE = LocalDate.of(2022, 2, 4);

    @Test
    void getOrdersFromExternalSource() throws JsonProcessingException {

        List<OrderDto> orders = externalOrderService.findAllByShippingDate(DATE);

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

        List<OrderDto> ordersByDate = externalOrderService.findAllByShippingDate(DATE);
        double totalOrdersWeight = externalOrderService.calculateTotalOrdersWeight(ordersByDate);

        Assertions.assertEquals(expectedTotalWeight, totalOrdersWeight);
    }

}
