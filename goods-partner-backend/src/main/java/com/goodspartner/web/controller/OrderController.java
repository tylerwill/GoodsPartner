package com.goodspartner.web.controller;

import com.goodspartner.dto.OrderDto;
import com.goodspartner.service.OrderService;
import com.goodspartner.web.controller.response.OrdersCalculation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1", produces = MediaType.APPLICATION_JSON_VALUE)
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/orders")
    public OrdersCalculation calculateOrders(@RequestParam String date) {

        LocalDate calculationDate = LocalDate.parse(date);
        List<OrderDto> ordersByDate = orderService.findAllByShippingDate(calculationDate);
        double totalOrdersWeight = orderService.calculateTotalOrdersWeight(ordersByDate);

        OrdersCalculation ordersCalculation = new OrdersCalculation();
        ordersCalculation.setDate(calculationDate);
        ordersCalculation.setOrders(ordersByDate);
        ordersCalculation.setTotalOrdersWeight(totalOrdersWeight);
        return ordersCalculation;

    }


}