package com.goodspartner.web.controller;

import com.goodspartner.dto.OrderDto;
import com.goodspartner.service.OrderService;
import com.goodspartner.web.controller.response.OrdersCalculation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/orders")
    public OrdersCalculation calculateOrders(@RequestParam String date) {

        LocalDate calculationDate = LocalDate.parse(date);
        List<OrderDto> ordersByDate = orderService.findAllByShippingDate(calculationDate);

        OrdersCalculation ordersCalculation = new OrdersCalculation();
        ordersCalculation.setDate(calculationDate);
        ordersCalculation.setOrders(ordersByDate);
        return ordersCalculation;

    }


}