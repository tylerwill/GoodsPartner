package com.goodspartner.service;

import com.goodspartner.dto.CalculationOrdersDto;
import com.goodspartner.dto.CalculationRoutesDto;
import com.goodspartner.dto.CalculationStoresDto;

import java.time.LocalDate;

public interface OrderService {

    CalculationOrdersDto calculateOrders(LocalDate date);
    CalculationRoutesDto calculateRoutes(LocalDate date);
    CalculationStoresDto calculateStores(LocalDate date);

}