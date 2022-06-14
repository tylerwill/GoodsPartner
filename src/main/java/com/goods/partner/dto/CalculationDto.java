package com.goods.partner.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

// TODO : rename for more meaningful if possible
@Getter
@Setter
public class CalculationDto {

    private LocalDate date;
    private List<OrderDto> orders;
    private List<AddressDto> addresses;
    private List<StoreDto> stores;

}
