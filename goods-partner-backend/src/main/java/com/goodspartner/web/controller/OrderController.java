package com.goodspartner.web.controller;

import com.goodspartner.dto.CalculationOrdersDto;
import com.goodspartner.dto.CalculationRoutesDto;
import com.goodspartner.dto.CalculationStoresDto;
import com.goodspartner.service.OrderService;
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