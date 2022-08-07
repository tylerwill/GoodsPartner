package com.goodspartner.service;

import com.goodspartner.dto.CalculationOrdersDto;
import com.goodspartner.dto.CalculationRoutesDto;

import java.time.LocalDate;

public interface OrderService {

    CalculationOrdersDto calculateOrders(LocalDate date);

    CalculationRoutesDto calculateRoutes(LocalDate date);

}