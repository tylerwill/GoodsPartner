package com.goods.partner.web.controller;

import com.goods.partner.dto.CalculationDto;
import com.goods.partner.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/calculate")
    public CalculationDto calculateOrders(@RequestParam LocalDate date) {
        return orderService.calculate(date);
    }
}
