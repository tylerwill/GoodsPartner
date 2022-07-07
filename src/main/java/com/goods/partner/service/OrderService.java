package com.goods.partner.service;

import com.goods.partner.dto.CalculationAddressesDto;
import com.goods.partner.dto.CalculationOrdersDto;
import com.goods.partner.dto.CalculationStoresDto;

import java.time.LocalDate;

public interface OrderService {

    CalculationOrdersDto calculateOrders(LocalDate date);
    CalculationAddressesDto calculateAddresses(LocalDate date);
    CalculationStoresDto calculateStores(LocalDate date);

}