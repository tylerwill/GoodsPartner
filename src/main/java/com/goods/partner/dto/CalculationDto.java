package com.goods.partner.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;


@Getter
@Setter
public class CalculationDto { // TODO : rename for more meaningful if possible

    private LocalDate date;
    private List<OrderDto> orders;
    private List<ClientDto> clients;
    private List<StoreDto> stores;

}
