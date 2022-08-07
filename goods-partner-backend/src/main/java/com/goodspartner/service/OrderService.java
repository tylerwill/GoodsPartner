package com.goodspartner.service;

import com.goodspartner.web.controller.response.OrdersCalculation;
import com.goodspartner.web.controller.response.RoutesCalculation;

import java.time.LocalDate;

public interface OrderService {

    OrdersCalculation calculateOrders(LocalDate date);

    RoutesCalculation calculateRoutes(LocalDate date);

}