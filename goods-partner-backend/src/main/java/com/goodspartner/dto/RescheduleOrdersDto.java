package com.goodspartner.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Setter
@Getter
public class RescheduleOrdersDto {

    private LocalDate rescheduleDate;

    private List<Integer> orderIds;

}
