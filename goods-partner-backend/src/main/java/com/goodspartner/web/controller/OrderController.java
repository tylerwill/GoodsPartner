package com.goodspartner.web.controller;

import com.goodspartner.web.controller.response.OrdersCalculation;
import com.goodspartner.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/orders")
    public OrdersCalculation calculateOrders(@RequestParam String date) {
        return orderService.calculateOrders(LocalDate.parse(date));
    }


}