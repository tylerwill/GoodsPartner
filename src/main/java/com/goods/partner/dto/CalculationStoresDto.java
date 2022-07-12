package com.goods.partner.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class CalculationStoresDto {
    private LocalDate date;
    private List<StoreDto> stores;
}