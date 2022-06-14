package com.goods.partner.web.controller;

import com.goods.partner.dto.OrderDto;
import com.goods.partner.entity.Order;
import com.goods.partner.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
public class OrderController {

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/calculate")
    public List<Order> getByDate(@RequestParam LocalDate date) {
        return orderService.getByDate(date);
    }
}
