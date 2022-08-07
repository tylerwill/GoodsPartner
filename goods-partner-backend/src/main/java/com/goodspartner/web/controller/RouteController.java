package com.goodspartner.web.controller;

import com.goodspartner.response.RoutesCalculation;
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
public class RouteController {
    private final OrderService orderService;

    @GetMapping("/routes")
    public RoutesCalculation calculateRoutes(@RequestParam String date) {
        return orderService.calculateRoutes(LocalDate.parse(date));
    }
}
