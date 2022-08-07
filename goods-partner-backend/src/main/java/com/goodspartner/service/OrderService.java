package com.goodspartner.service;

import com.goodspartner.response.OrdersCalculation;
import com.goodspartner.response.RoutesCalculation;

import java.time.LocalDate;

public interface OrderService {

    OrdersCalculation calculateOrders(LocalDate date);

    RoutesCalculation calculateRoutes(LocalDate date);

}