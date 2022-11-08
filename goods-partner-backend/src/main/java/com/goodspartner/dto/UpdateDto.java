package com.goodspartner.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Setter
@Getter
public class UpdateDto {

    private List<Integer> ordersIdList;
    private LocalDate deliveryDate;

}
