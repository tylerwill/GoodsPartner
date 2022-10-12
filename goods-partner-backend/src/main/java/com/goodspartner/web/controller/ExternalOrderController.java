package com.goodspartner.web.controller;

import com.goodspartner.dto.OrderDto;
import com.goodspartner.service.GeocodeService;
import com.goodspartner.service.IntegrationService;
import com.goodspartner.service.util.OrderCommentProcessor;
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
@RequestMapping(path = "/api/v1/orders", produces = MediaType.APPLICATION_JSON_VALUE)
public class ExternalOrderController {

    private final IntegrationService integrationService; // GrangeDolceIntegration
    private final GeocodeService geocodeService;
    private final OrderCommentProcessor orderCommentProcessor;

    @PreAuthorize("hasAnyRole('ADMIN', 'LOGIST')")
    @GetMapping
    public OrdersCalculation getExternalOrdersByDate(@RequestParam String date) {

        LocalDate calculationDate = LocalDate.parse(date);

        List<OrderDto> orders = integrationService.findAllByShippingDate(calculationDate);

        orderCommentProcessor.processOrderComments(orders);

        geocodeService.enrichValidAddress(orders);

        double totalOrdersWeight = integrationService.calculateTotalOrdersWeight(orders);

        return OrdersCalculation.builder()
                .date(calculationDate)
                .orders(orders)
                .totalOrdersWeight(totalOrdersWeight)
                .build();
    }
}