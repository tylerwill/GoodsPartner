package com.goods.partner.service;

import com.goods.partner.dto.CalculationDto;

import java.time.LocalDate;

public interface OrderService {

    CalculationDto calculate(LocalDate date);

}
