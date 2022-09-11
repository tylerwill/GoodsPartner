package com.goodspartner.web.controller;

import com.goodspartner.dto.OrderDto;
import com.goodspartner.service.OrderValidationService;
import com.goodspartner.service.OrderService;
import com.goodspartner.web.controller.response.OrdersCalculation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/orders", produces = MediaType.APPLICATION_JSON_VALUE)
public class OrderController {

    private final OrderService orderService;
    private final OrderValidationService orderValidationService;

    @PreAuthorize("hasAnyRole('ADMIN', 'LOGIST')")
    @GetMapping
    public OrdersCalculation getExternalOrdersByDate(@RequestParam String date) {

        LocalDate calculationDate = LocalDate.parse(date);

        List<OrderDto> orders = orderService.findAllByShippingDate(calculationDate);

        orderValidationService.enrichValidAddress(orders);

        double totalOrdersWeight = orderService.calculateTotalOrdersWeight(orders);

        return OrdersCalculation.builder()
                .date(calculationDate)
                .orders(orders)
                .totalOrdersWeight(totalOrdersWeight)
                .build();
    }
}