package com.goods.partner.web.controller;

import com.goods.partner.dto.CalculationOrdersDto;
import com.goods.partner.dto.CalculationRoutesDto;
import com.goods.partner.dto.CalculationStoresDto;
import com.goods.partner.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/calculate")
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/orders")
    public CalculationOrdersDto calculateOrders(@RequestParam String date) {
        return orderService.calculateOrders(LocalDate.parse(date));
    }

    @GetMapping("/routes")
    public CalculationRoutesDto calculateRoutes(@RequestParam String date) {
        return orderService.calculateRoutes(LocalDate.parse(date));
    }

    @GetMapping("/stores")
    public CalculationStoresDto calculateStores(@RequestParam String date) {
        return orderService.calculateStores(LocalDate.parse(date));
    }
}